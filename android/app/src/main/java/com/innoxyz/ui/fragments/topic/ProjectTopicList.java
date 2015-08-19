package com.innoxyz.ui.fragments.topic;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.innoxyz.data.remote.AddressURIs;
import com.innoxyz.data.remote.StringRequestBuilder;
import com.innoxyz.data.remote.response.JsonResponseHandler;
import com.innoxyz.data.runtime.model.Pager;
import com.innoxyz.data.runtime.model.Protocol;
import com.innoxyz.data.runtime.model.topic.Topic;
import com.innoxyz.data.runtime.model.user.User;
import com.innoxyz.network.URL;
import com.innoxyz.ui.activities.DetailActivity;
import com.innoxyz.ui.commands.ActivityCommand;
import com.innoxyz.ui.commands.FragmentCommand;
import com.innoxyz.ui.customviews.RefreshListView;
import com.innoxyz.ui.fragments.common.BaseFragment;
import com.innoxyz.ui.fragments.common.CommonListViewAdapter;
import com.innoxyz.ui.interfaces.OnRefreshListener;
import com.innoxyz.ui.utils.DateFunctions;
import com.innoxyz.R;
import com.innoxyz.util.Factory;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with yj
 * User: InnoXYZ
 * Date: 15-1-29
 * Time: 下午5:26
 */
public class ProjectTopicList extends BaseFragment implements OnRefreshListener {
    //显示项目的所有通知
    List<Topic> topicList;
    RefreshListView topicListView;
    TopicListViewAdapter  topicAdapter;

    //网络请求参数-任务回复
    private RequestParams params = new RequestParams();
    //项目信息
    String projectName;
    String projectId;
    //获取页数
    private int page = 0;
    private Protocol<Topic> theProtocol;
    private Protocol<Topic> protocol;
    private Pager<Topic> pager;

    ActionBar actionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getActivity().getActionBar();
        //获取参数
        projectName = getArguments().getString("projectName");
        projectId = String.valueOf(getArguments().getInt("projectId"));
        //初始化参数
        topicListView = new RefreshListView(getActivity(), null);
        topicAdapter = new TopicListViewAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_topic_list);
        }


        View rootview = inflater.inflate(R.layout.project_announcement_list, container, false);
        topicListView = (RefreshListView) rootview.findViewById(R.id.project_announcement_listview);

        //初始化数据参数，如果在onCreate中初始化，当页面切换到A，再回来本页面时，会发生数据重复
        topicList = new ArrayList<>();

        //绑定下拉刷新监听
        topicListView.setOnRefreshListener(this);
        //绑定adapter
        topicListView.setAdapter(topicAdapter);

        //每个item绑定点击监听
        topicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //如果处于刷新状态，则不响应
                if (topicListView.isRefreshing())
                    return;
                //进入话题详情
                Topic topic = (Topic) view.getTag(R.id.item_object);
                Bundle bundle = new Bundle();
                bundle.putAll(getArguments());
                bundle.putString("topicId", topic.getId());
                bundle.putString("topicName", topic.getSubject());
                new ActivityCommand(DetailActivity.class, TopicHome.class, ProjectTopicList.this.getActivity(), bundle, null).Execute();
            }
        });

        //启动刷新
        topicListView.startRefresh();
        //显示任务列表
        showTopicList();

        return rootview;

    }

    //显示任务列表
    private void showTopicList() {
        //参数
        params.put("lprojectId", projectId);
        params.put("page", String.valueOf(page));

        Factory.getHttpClient().get(URL.LIST_TOPIC_IN_LPROJECT, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                theProtocol = new Protocol<Topic>();
                protocol = theProtocol.fromJson(response, new TypeToken<Protocol<Topic>>(){}.getType());
                if (protocol.isOk()) {
                    pager = protocol.getPager();
                    if (topicListView.isRefreshing()) {
                        topicList.clear();
                    }
                    topicList.addAll(pager.getData());
                    //刷新数据
                    topicAdapter.notifyDataSetChanged();
                    topicListView.setDataPager(pager);
                }else {
                    Toast.makeText(getActivity(), "数据获取错误!", Toast.LENGTH_SHORT).show();
                }
                //隐藏刷新/加载头
                topicListView.hideHeaderFooter();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "网络连接失败，请检查网络连接！", Toast.LENGTH_SHORT).show();
                //隐藏刷新/加载头
                topicListView.hideHeaderFooter();
            }
        });

    }

    @Override
    public void onDownPullRefresh() {
        page = 0;
        //重新获取数据
        showTopicList();
    }

    @Override
    public void onLoadingMore() {
        page ++;
        showTopicList();
    }


    private class TopicListViewAdapter extends BaseAdapter {

        //子控件
        class ViewHolder{
            TextView topic_item_name, topic_item_lastreply;
        }
        @Override
        public int getCount() {
            return topicList.size();
        }

        @Override
        public Object getItem(int position) {
            return topicList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                view = inflater.inflate(R.layout.listitem_topic, null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.topic_item_name = (TextView)view.findViewById(R.id.topic_item_name);
                viewHolder.topic_item_lastreply = (TextView)view.findViewById(R.id.topic_item_lastreply);

                view.setTag(viewHolder);
            }

            ViewHolder holder = (ViewHolder) view.getTag();

            //存储当成item数据
            Topic topic = topicList.get(position);
            view.setTag(R.id.item_object, topic);

            //任务名称
            holder.topic_item_name.setText(topic.getSubject());
            holder.topic_item_lastreply.setText(DateFunctions.RewriteDate(topic.getCreateTime(), "yyyy-M-d", "unknown"));
            return view;


//            最近回复,参考 beans->topic->Topics->Normalize方法，转化PostMap数据才能显示
//            String lastReply = "";
//            if ( topic.getLastPostId() == null ) {
//                //无人回复
//                lastReply = getResources().getString(R.string.hint_topicitem_noreply);
//            } else {
//                lastReply = getResources().getString(R.string.template_from)
//                        .replace("%user%", topic.getCreatorName())
//                        .replace("%date%",DateFunctions.RewriteDate(topic.getLastPost().getCreateTime(), "yy-M-d", "unknown"));
//            }
//            holder.topic_item_lastreply.setText(lastReply);

        }
    }
}
