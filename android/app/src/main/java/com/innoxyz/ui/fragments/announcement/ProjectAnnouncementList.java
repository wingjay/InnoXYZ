package com.innoxyz.ui.fragments.announcement;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.innoxyz.data.runtime.model.Pager;
import com.innoxyz.data.runtime.model.Protocol;
import com.innoxyz.data.runtime.model.announcement.Announcement;
import com.innoxyz.data.runtime.model.attachment.Attachment;
import com.innoxyz.data.runtime.model.attachment.ParcelableAttachment;
import com.innoxyz.network.URL;
import com.innoxyz.ui.activities.DetailActivity;
import com.innoxyz.ui.commands.ActivityCommand;
import com.innoxyz.ui.customviews.RefreshListView;
import com.innoxyz.ui.fragments.common.BaseFragment;
import com.innoxyz.ui.interfaces.OnRefreshListener;
import com.innoxyz.ui.utils.DateFunctions;

import java.util.ArrayList;
import java.util.List;

import com.innoxyz.R;
import com.innoxyz.util.Factory;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * Created with yj
 * User: InnoXYZ
 * Date: 15-1-29
 * Time: 下午3:33
 */
public class ProjectAnnouncementList extends BaseFragment implements OnRefreshListener{

    //显示项目的所有通知
    List<Announcement> announcementList;
    RefreshListView announcementListView;
    AnnouncementListViewAdapter  announcementAdapter;

    //网络请求参数-任务回复
    private RequestParams params = new RequestParams();
    //项目信息
    String projectName;
    String projectId;
    //获取页数
    private int page = 0;
    private Protocol<Announcement> theProtocol;
    private Protocol<Announcement> protocol;
    private Pager<Announcement> pager;

    ActionBar actionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getActivity().getActionBar();
        //获取参数
        projectName = getArguments().getString("projectName");
        projectId = String.valueOf(getArguments().getInt("projectId"));
        //初始化参数
        announcementListView = new RefreshListView(getActivity(), null);
        announcementAdapter = new AnnouncementListViewAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_announcement_list);
        }

        View rootview = inflater.inflate(R.layout.project_announcement_list, container, false);
        announcementListView = (RefreshListView) rootview.findViewById(R.id.project_announcement_listview);

        //初始化数据参数，如果在onCreate中初始化，当页面切换到A，再回来本页面时，会发生数据重复
        announcementList = new ArrayList<>();

        //绑定下拉刷新监听
        announcementListView.setOnRefreshListener(this);
        //绑定adapter
        announcementListView.setAdapter(announcementAdapter);

        //每个item绑定点击监听
        announcementListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //如果处于刷新状态，则不响应
                if (announcementListView.isRefreshing())
                    return;
                //进入通知详情
                Announcement announcement = (Announcement)view.getTag(R.id.item_object);
                Bundle bundle = new Bundle();
                //通知内容
                bundle.putString("projectName", projectName);
                bundle.putString("creatorName", announcement.getCreatorName());
                bundle.putString("title", announcement.getTitle());
                bundle.putString("content", announcement.getContent());
                //附件 序列化传递
                if (announcement.getAttachments() != null) {
                    //要传递的序列化对象
                    ArrayList<ParcelableAttachment> attachments = new ArrayList<>();
                    //把原始数据放入序列化对象中
                    for (Attachment attachment : announcement.getAttachments()) {
                        ParcelableAttachment a = new ParcelableAttachment(attachment.getId(), attachment.getName());
                        attachments.add(a);
                    }
                    //传递序列化对象
                    bundle.putParcelableArrayList("attachments", attachments);
                }
                new ActivityCommand(DetailActivity.class, AnnouncementHome.class, ProjectAnnouncementList.this.getActivity(), bundle, null).Execute();
            }
        });

        //显示项目列表
        showAnnouncementList();

        return rootview;
    }


    //显示通知
    private void showAnnouncementList() {
        //参数
        params.put("thingId", projectId);
        params.put("page", String.valueOf(page));

        Factory.getHttpClient().get(URL.LIST_ANNOUNCEMENT_IN_PROJECT, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                theProtocol = new Protocol<>();
                protocol = theProtocol.fromJson(response, new TypeToken<Protocol<Announcement>>(){}.getType());
                if (protocol.isOk()) {
                    pager = protocol.getPager();
                    announcementListView.setDataPager(pager);
                    if (announcementListView.isRefreshing()) {
                        announcementList.clear();
                    }
                    announcementList.addAll(pager.getData());
                    //刷新数据
                    announcementAdapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(getActivity(), "数据获取错误!", Toast.LENGTH_SHORT).show();
                }
                //隐藏刷新/加载头
                announcementListView.hideHeaderFooter();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "网络连接失败，请检查网络连接！", Toast.LENGTH_SHORT).show();
                //隐藏刷新/加载头
                announcementListView.hideHeaderFooter();
            }
        });

    }

    @Override
    public void onDownPullRefresh() {
        page = 0;
        //重新获取数据
        showAnnouncementList();
    }

    @Override
    public void onLoadingMore() {
        if (!announcementListView.hasMore) {
            announcementListView.hideFooterView();
        } else {
            page ++;
            showAnnouncementList();
        }
    }


    private class AnnouncementListViewAdapter extends BaseAdapter {

        //子控件
        class ViewHolder{
            TextView announcement_item_name,announcement_item_lastupdate;
        }
        @Override
        public int getCount() {
            return announcementList.size();
        }

        @Override
        public Object getItem(int position) {
            return announcementList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                view = inflater.inflate(R.layout.listitem_announcement, null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.announcement_item_name = (TextView)view.findViewById(R.id.announcement_item_name);
                viewHolder.announcement_item_lastupdate = (TextView)view.findViewById(R.id.announcement_item_lastupdate);

                view.setTag(viewHolder);
            }

            ViewHolder holder = (ViewHolder) view.getTag();

            //获取当前显示的通知item
            Announcement announcement = announcementList.get(position);
            view.setTag(R.id.item_object, announcement);

            //显示内容
            holder.announcement_item_name.setText(announcement.getTitle());
            holder.announcement_item_lastupdate.setText(DateFunctions.RewriteDate(announcement.getCreateTime(), "yy-M-d", "unknown"));

            return view;
        }
    }



}
