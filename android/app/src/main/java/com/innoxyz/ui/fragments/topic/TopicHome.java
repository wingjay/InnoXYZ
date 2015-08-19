package com.innoxyz.ui.fragments.topic;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;

import com.innoxyz.data.remote.AddressURIs;
import com.innoxyz.data.runtime.model.ParcelableUser;
import com.innoxyz.data.runtime.model.Pager;
import com.innoxyz.data.runtime.model.attachment.Attachment;
import com.innoxyz.data.runtime.model.project.Member;
import com.innoxyz.data.runtime.model.project.MembersProtocol;
import com.innoxyz.data.runtime.model.topic.Topic;
import com.innoxyz.data.runtime.model.topic.TopicReply;
import com.innoxyz.data.runtime.model.topic.TopicReplyProtocol;
import com.innoxyz.global.InnoXYZApp;
import com.innoxyz.network.URL;
import com.innoxyz.ui.activities.NewActivity;
import com.innoxyz.ui.adapters.AttachmentListAdapter;
import com.innoxyz.ui.commands.ActivityCommand;
import com.innoxyz.ui.commands.AddInvolvedUserDialog;
import com.innoxyz.ui.commands.FragmentCommand;
import com.innoxyz.ui.customviews.RefreshListView;
import com.innoxyz.ui.fragments.common.BaseFragment;
import com.innoxyz.ui.fragments.project.ProjectHome;
import com.innoxyz.ui.interfaces.OnRefreshListener;

import com.innoxyz.ui.utils.DateFunctions;
import com.innoxyz.util.Factory;

import java.util.ArrayList;
import java.util.List;
import com.innoxyz.R;
import com.innoxyz.util.Util;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.w3c.dom.Text;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-22
 * Time: 下午5:45
 * To change this template use File | Settings | File Templates.
 */
//讨论详情页fragment，放置于DetailActivity，接收Arguments数据
public class TopicHome extends BaseFragment implements OnRefreshListener{
    //任务回复列表
    private RefreshListView topicListView;
    private TopicListViewAdapter topicAdapter;

    //网络请求参数-任务回复
    private RequestParams params = new RequestParams();
    //获取页数
    private int page = 0;
    private TopicReplyProtocol protocol;
    private Pager<TopicReply> pager;
    //话题回复包括两部分：该话题内容Topic + 若干条回复TopicReply
    private Topic topic;
    private List<Attachment> topicAttachments; //主题附件
    private AttachmentListAdapter topicAttachAdapter;
    private ListView titleAttachListView;
    private List<TopicReply> topicReplyList;


    //Notify中传递过来的数据
    String topicName;
    private int projectId;
    String hostType;

    //当前的ActionBar
    ActionBar actionBar;

    private TextView topicSubjectTextView;
    private TextView topicContentTextView;
    private TextView topicCreatorNameTextView;
    private TextView topicCreateTimeTextView;


    //参与者：修该参与者、前往所在项目
    MembersProtocol membersProtocol;
    //序列化数据
    ArrayList<ParcelableUser> parcelableMembers;
    //实际数据
    List<Member> membersList;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        topicName = getArguments().getString("topicName");
        //项目ID
        projectId = getArguments().getInt("projectId");
        //类型供上传附件用
        hostType = getArguments().getString("hostType");
        //获取顶部菜单
        actionBar = getActivity().getActionBar();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获取项目成员信息
        parcelableMembers = getArguments().getParcelableArrayList("memberList");
        if (parcelableMembers != null) {
            return;
        }
        parcelableMembers = new ArrayList<>();
        //从服务器获取项目成员信息并序列化供后面使用
        membersList = new ArrayList<>();
        RequestParams params1 = new RequestParams();
        params1.put("thingId",String.valueOf(projectId));
        params1.put("pageSize", "100");

        Factory.getHttpClient().get(URL.GET_MEMBER_OF_PROJECT, params1, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                membersProtocol = MembersProtocol.fromJson(response);
                if (membersProtocol.isOk()) {
                    membersList = membersProtocol.getMembers();
                    //序列化存入parcelableMembers
                    if (membersList != null) {
                        for (Member m : membersList) {
                            ParcelableUser parcelableUser = new ParcelableUser(m.getUser().getName(), m.getUser().getId());
                            parcelableMembers.add(parcelableUser);
                        }
                    }else {
                        parcelableMembers = null;
                    }
                }
                else {
                    Toast.makeText(getActivity(), "项目参与人员数据拉取失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_topic_home, container, false);

        setHasOptionsMenu(true);
        if (actionBar != null){
            if (TextUtils.isEmpty(topicName))
                actionBar.setTitle(R.string.title_topic_home);
            else
                actionBar.setTitle(topicName);
        }

        //更新项目名称
        ((TextView)ret.findViewById(R.id.topichome_projectname))
                .setText(getResources().getString(R.string.template_topichome_projectname).replace("%name%", getArguments().getString("projectName")));

        //讨论回复列表
        topicListView = (RefreshListView) ret.findViewById(R.id.topic_reply_listview);
        //创建存储列表
        topicReplyList = new ArrayList<>();
        //每次重新绘制视图，都令page=0
        page = 0;
        //设置topic信息为header
        View topicInfoView = inflater.inflate(R.layout.fragment_topic_home_header, null);
        //头部附件列表
        titleAttachListView = (ListView)topicInfoView.findViewById(R.id.title_attachment_listview);
        topicAttachments = new ArrayList<>();
        topicAttachAdapter = new AttachmentListAdapter(getActivity(), topicAttachments);
        titleAttachListView.setAdapter(topicAttachAdapter);

        topicListView.addHeaderView(topicInfoView);

        //获取topic信息控件
        topicSubjectTextView = (TextView)topicInfoView.findViewById(R.id.topic_subject);
        topicContentTextView = (TextView)topicInfoView.findViewById(R.id.topic_content);
        topicCreateTimeTextView = (TextView)topicInfoView.findViewById(R.id.topic_create_time);
        topicCreatorNameTextView = (TextView)topicInfoView.findViewById(R.id.topic_creator_name_value);

        //绑定下拉刷新监听器
        topicListView.setOnRefreshListener(this);
        topicAdapter = new TopicListViewAdapter();
        topicListView.setAdapter(topicAdapter);

        //刷新任务回复列表
        updateTopicReplyList();

        //回复任务、前往所在项目绑定监听器
        ret.findViewById(R.id.topic_home_reply).setOnClickListener(onReplyClicked);
        ret.findViewById(R.id.topic_home_goto_project).setOnClickListener(onGoToProjectClicked);


        return ret;
    }

    //获取讨论列表数据并显示
    private void updateTopicReplyList() {
        params.put("topicId", getArguments().getString("topicId"));
        params.put("page", String.valueOf(page));

        Factory.getHttpClient().get(URL.TOPIC_DETAIL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String response = new String(bytes);
                protocol = TopicReplyProtocol.fromJson(response);
                if (protocol.isOk()) {
                    //刷新topic内容
                    topic = protocol.getTopic();
                    //获取主题附件
                    topicAttachments = topic.getAttachments();
                    showTopic();
                    //获取话题回复
                    pager = protocol.getPager();
                    if (topicListView.isRefreshing()) {
                        topicReplyList.clear();
                    }
                    topicReplyList.addAll(pager.getData());
                    //刷新视图
                    topicAdapter.notifyDataSetChanged();
                    //告诉listView的pager内容
                    topicListView.setDataPager(pager);
                }else {
                    Toast.makeText(getActivity(), "数据获取错误!", Toast.LENGTH_SHORT).show();
                }
                //隐藏刷新/加载头
                topicListView.hideHeaderFooter();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(getActivity(), "网络连接失败，请检查网络连接！", Toast.LENGTH_SHORT).show();
                //隐藏刷新/加载头
                topicListView.hideHeaderFooter();
            }
        });

    }

    //回复项目
    private View.OnClickListener onReplyClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //清空引用内容
            Bundle bundle = getArguments();
            bundle.putString("quotedUser", "");
            bundle.putString("quotedContent", "");
            //跳转到回复界面
            new ActivityCommand(NewActivity.class, com.innoxyz.ui.fragments.topic.TopicReply.class, TopicHome.this.getActivity(), bundle, null).Execute();
        }
    };


    //点击前往所在项目，切换fragment
    protected View.OnClickListener onGoToProjectClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //把从NotifyList中传递过来的Arguments传递到 ProjectHome
            Bundle bundle = new Bundle();
            bundle.putAll(getArguments());
            //把获取的序列化项目成员信息存入bundle中
            if (parcelableMembers != null) {
                bundle.putParcelableArrayList("memberList", parcelableMembers);
            }
            //DetailActivity 切换到 ProjectHome fragment中
            new FragmentCommand(TopicHome.class, ProjectHome.class, TopicHome.this.getActivity(), bundle, null).Execute();
        }
    };

    //显示topic基本信息
    private void showTopic() {
        if (topic != null) {
            topicSubjectTextView.setText(topic.getSubject());
            topicContentTextView.setText(topic.getContent());
            topicCreatorNameTextView.setText(topic.getCreatorName());
            topicCreateTimeTextView.setText(DateFunctions.RewriteDate(topic.getCreateTime(), "yyyy-M-d", "unknown"));
            //附件
            if (topicAttachments != null) {
                topicAttachAdapter.updateAttachments(topicAttachments);
                //因为嵌套了附件子listView，为防止子listView显示不全，要设置高度
                Util.setListViewHeight(titleAttachListView);
                topicAttachAdapter.notifyDataSetChanged();
            }
        }
    }

    //刷新
    @Override
    public void onDownPullRefresh() {
        page = 0;
        updateTopicReplyList();
    }

    //加载
    @Override
    public void onLoadingMore() {
        page ++;
        updateTopicReplyList();
    }

    //话题回复TopicReply
    private class TopicListViewAdapter extends BaseAdapter {
        //子控件
        class ViewHolder{
            TextView topichome_item_username,topichome_item_content,topichome_item_date,quote;
            NetworkImageView topichome_item_avatar;
            ListView topic_attath_list;
        }


        @Override
        public int getCount() {
            return topicReplyList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                view = inflater.inflate(R.layout.listitem_topichome_topic, null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.topichome_item_username = (TextView)view.findViewById(R.id.topichome_item_username);
                viewHolder.topichome_item_content = (TextView)view.findViewById(R.id.topichome_item_content);
                viewHolder.topichome_item_date = (TextView)view.findViewById(R.id.topichome_item_date);
                viewHolder.topichome_item_avatar = (NetworkImageView)view.findViewById(R.id.topichome_item_avatar);
                //引用
                viewHolder.quote = (TextView)view.findViewById(R.id.quote);
                //附件列表
                viewHolder.topic_attath_list = (ListView)view.findViewById(R.id.topic_attach_list);

                view.setTag(viewHolder);
            }

            ViewHolder holder = (ViewHolder) view.getTag();

            //获取当前显示的这条回复
            final TopicReply topicReply = topicReplyList.get(i);
            //附件列表绑定Adapter
            if (topicReply.getAttachments() != null) {
                //把本条回复的附件复制出来
                List<Attachment> attachments = new ArrayList<>();
                for(Attachment attach : topicReply.getAttachments()) {
                    attachments.add(attach);
                }
                AttachmentListAdapter attachmentListAdapter = new AttachmentListAdapter(getActivity(), attachments);
                holder.topic_attath_list.setAdapter(attachmentListAdapter);
            } else {
                //如果没有附件则要清空adapter，否则会有缓存
                holder.topic_attath_list.setAdapter(null);
            }
            //因为嵌套了附件子listView，为防止子listView显示不全，要设置高度
            Util.setListViewHeight(holder.topic_attath_list);

            holder.topichome_item_username.setText(getResources().getString(R.string.template_topichome_username).replace("%name%", topicReply.getCreatorName()));
            //[quote][strong]小明：[/strong]这是小明说的[/quote]这是我自己说的
            String content = topicReply.getContent();
            content = Util.quoteModified(content);
            holder.topichome_item_content.setText(Html.fromHtml(content));
            holder.topichome_item_date.setText(DateFunctions.RewriteDate(topicReply.getCreateTime(), "yyyy-M-d", "unknown"));
            holder.topichome_item_avatar.setImageUrl(AddressURIs.getUserAvatarURL(topicReply.getCreatorId()), InnoXYZApp.getApplication().getImageLoader());


            //引用 绑定点击函数-跳转界面，带上引用内容
            holder.quote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //将回复者和内容存入bundle
                    Bundle bundle = getArguments();
                    bundle.putString("quotedUser", topicReply.getCreatorName());
                    bundle.putString("quotedContent", topicReply.getContent());
                    //跳转到回复界面
                    new ActivityCommand(NewActivity.class, com.innoxyz.ui.fragments.topic.TopicReply.class, TopicHome.this.getActivity(), bundle, null).Execute();
                }
            });


            return view;
        }

    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.topichome_fragment_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        int id = item.getItemId();
        if (id == R.id.action_topichome_adduser){
            //比较当前话题创建者ID是否为本人
            if(topic.getCreatorId() == InnoXYZApp.getApplication().getCurrentUserId()){
                Bundle bundle = new Bundle();
                bundle.putAll(getArguments());
                bundle.putString(AddInvolvedUserDialog.TYPE_NAME, AddInvolvedUserDialog.TYPE_TOPIC);
                new AddInvolvedUserDialog(getActivity(), bundle)
                        .setItemsAndIds(parcelableMembers)
                        .show();
            }
            else{
                new AlertDialog.Builder(getActivity())
                        .setMessage(getString(R.string.topic_modify_involvers))
                        .setPositiveButton(getString(R.string.OK), null)
                        .show();
            }

        }
        return false;
    }

}
