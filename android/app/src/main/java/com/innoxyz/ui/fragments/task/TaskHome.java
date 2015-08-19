package com.innoxyz.ui.fragments.task;

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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;

import com.google.gson.reflect.TypeToken;
import com.innoxyz.data.runtime.model.ParcelableUser;
import com.innoxyz.data.runtime.beans.common.StateMap;
import com.innoxyz.data.runtime.model.Pager;
import com.innoxyz.data.runtime.model.Protocol;
import com.innoxyz.data.runtime.model.attachment.Attachment;
import com.innoxyz.data.runtime.model.project.Member;
import com.innoxyz.data.runtime.model.project.MembersProtocol;
import com.innoxyz.data.runtime.model.task.Pushstate;
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

/**
 * Created with yj.
 * User: InnoXYZ
 * Date: 13-10-15
 * Time: 下午1:29
 */
//任务详情页fragment，放置于DetailActivity
public class TaskHome extends BaseFragment implements OnRefreshListener {

    //任务回复列表
    private RefreshListView taskListView;
    private TaskListViewAdapter taskAdapter;

    //网络请求参数-任务回复
    private RequestParams params = new RequestParams();
    //获取页数
    private int page = 0;
    private Protocol<Pushstate> Protocol;
    private Protocol<Pushstate> protocol;
    private Pager<Pushstate> pager;
    //存储任务回复
    private List<Pushstate> taskReplyList;

    //Notify中传递过来的数据
    String taskName;
    String projectName;
    private int projectId;
    String hostType;
    private int taskCreatorId;

    //当前的ActionBar
    ActionBar actionBar;

    //参与者：修该参与者、前往所在项目
    MembersProtocol membersProtocol;
    //序列化数据
    ArrayList<ParcelableUser> parcelableMembers;
    //实际数据
    List<Member> membersList;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        taskName = getArguments().getString("taskName");
        projectName = getArguments().getString("projectName");
        projectId = getArguments().getInt("projectId");//项目ID
        hostType = getArguments().getString("hostType");
        taskCreatorId = getArguments().getInt("creatorId");//创建者ID
        //获取项目成员信息
        parcelableMembers = getArguments().getParcelableArrayList("memberList");
        actionBar = getActivity().getActionBar();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    //显示任务介绍
    private void showTaskInfo(View view, Bundle args) {
        ((TextView)view.findViewById(R.id.task_home_creatorName)).setText(args.getString("creatorName"));
        ((TextView)view.findViewById(R.id.task_home_assignees)).setText(args.getString("assignees"));
        ((TextView)view.findViewById(R.id.task_home_desc)).setText(args.getString("description"));
        ((TextView)view.findViewById(R.id.task_home_deadline)).setText(DateFunctions.RewriteDate(args.getString("deadline"), "yyyy-M-dd", "unknown"));
        ((TextView)view.findViewById(R.id.task_home_priority)).setText(args.getString("priority"));
        ((TextView)view.findViewById(R.id.task_home_state)).setText(args.getString("state"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_task_home, container, false);
        setHasOptionsMenu(true);
        if (actionBar != null){
            if (TextUtils.isEmpty(taskName))
                actionBar.setTitle(R.string.title_task_home);
            else
                actionBar.setTitle(taskName);
        }

        //显示项目名称
        ((TextView)ret.findViewById(R.id.taskhome_projectname)).setText( getResources().getString(R.string.template_topichome_projectname)
                                                                                        .replace("%name%", projectName) );

        //任务列表
        taskListView = (RefreshListView) ret.findViewById(R.id.task_home_listview);
        //把任务信息绑定到 taskListView 的头布局中
        View taskInfoView = inflater.inflate(R.layout.fragment_task_home_header, null);
        taskListView.addHeaderView(taskInfoView);
        //显示任务信息
        showTaskInfo(ret, getArguments());

        //创建存储列表
        taskReplyList = new ArrayList<>();
        //每次重新绘制视图，都令page=0
        page = 0;
        //绑定下拉刷新监听器
        taskListView.setOnRefreshListener(this);
        taskAdapter = new TaskListViewAdapter();
        taskListView.setAdapter(taskAdapter);
        //刷新任务回复列表
        updateTaskReplyList();

        //回复任务、前往所在项目绑定监听器
        ret.findViewById(R.id.task_home_reply).setOnClickListener(onReplyClicked);
        ret.findViewById(R.id.task_home_goto_project).setOnClickListener(onGoToProjectClicked);


        return ret;
    }

    //获取任务列表数据并显示
    private void updateTaskReplyList() {
        params.put("taskId", getArguments().getString("taskId"));
        params.put("page", String.valueOf(page));

        Factory.getHttpClient().get(URL.LIST_PUSHSTATE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String response = new String(bytes);
                Protocol = new Protocol<Pushstate>();
                protocol = Protocol.fromJson(response, new TypeToken<Protocol<Pushstate>>(){}.getType());
                if (protocol.isOk()) {
                    pager = protocol.getPager();
                    if (taskListView.isRefreshing()) {
                        taskReplyList.clear();
                    }
                    taskReplyList.addAll(pager.getData());
                    //刷新视图
                    taskAdapter.notifyDataSetChanged();
                    // 把pager数据传递给RefreshListView，让它来管理Tip内容和自动加载
                    taskListView.setDataPager(pager);
                }else {
                    Toast.makeText(getActivity(), "数据获取错误!", Toast.LENGTH_SHORT).show();
                }
                //隐藏刷新/加载头
                taskListView.hideHeaderFooter();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(getActivity(), "网络连接失败，请检查网络连接！", Toast.LENGTH_SHORT).show();
                //隐藏刷新/加载头
                taskListView.hideHeaderFooter();
            }
        });

    }

    //刷新
    @Override
    public void onDownPullRefresh() {
        //设置刷新
        page = 0;
        updateTaskReplyList();
    }

    //加载,自动判断是否还有更多
    @Override
    public void onLoadingMore() {
        //设置加载
        page ++;
        updateTaskReplyList();
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.taskhome_fragment_actions, menu); //添加右上角菜单
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        int id = item.getItemId();
        if (id == R.id.action_taskhome_adduser){
            if(taskCreatorId == InnoXYZApp.getApplication().getCurrentUserId()){
                //修改参与者
                Bundle bundle = new Bundle();
                bundle.putAll(getArguments());
                bundle.putString(AddInvolvedUserDialog.TYPE_NAME, AddInvolvedUserDialog.TYPE_TASK);
                new AddInvolvedUserDialog(getActivity(), bundle)
                        .setItemsAndIds(parcelableMembers)
                        .show();
            }
            else{
                new AlertDialog.Builder(getActivity())
                        .setMessage(getString(R.string.task_modify_involvers))
                        .setPositiveButton(getString(R.string.OK), null)
                        .show();
            }
        }

        return false;
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
            new ActivityCommand(NewActivity.class, TaskReply.class, TaskHome.this.getActivity(), bundle, null).Execute();
        }
    };
    //前往所在项目
    protected View.OnClickListener onGoToProjectClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putAll(getArguments());
            //把获取的序列化项目成员信息存入bundle中
            if (parcelableMembers != null) {
                bundle.putParcelableArrayList("memberList", parcelableMembers);
            }
            new FragmentCommand(TaskHome.class, ProjectHome.class, TaskHome.this.getActivity(), bundle, null).Execute();
        }
    };

    //任务回复列表
    private class TaskListViewAdapter extends BaseAdapter {

        //子控件
        class ViewHolder{
            TextView taskhome_item_username,taskhome_item_date,taskhome_item_content,taskhome_item_state,taskhome_item_stateTitle,quote;
            NetworkImageView taskhome_item_avatar;
            ListView task_attath_list;
        }

        @Override
        public int getCount() {
            return taskReplyList.size();
        }

        @Override
        public Object getItem(int position) {
            return taskReplyList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            //有无附件
            boolean hasAttachment = false;
            //存储当前回复的附件
            final List<Attachment> attachments = new ArrayList<>();
            if (view == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                view = inflater.inflate(R.layout.listitem_taskhome_pushstate, null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.taskhome_item_username = (TextView)view.findViewById(R.id.taskhome_item_username);
                viewHolder.taskhome_item_date = (TextView)view.findViewById(R.id.taskhome_item_date);
                viewHolder.taskhome_item_content = (TextView)view.findViewById(R.id.taskhome_item_content);
                viewHolder.taskhome_item_state = (TextView)view.findViewById(R.id.taskhome_item_state);
                viewHolder.taskhome_item_stateTitle = (TextView)view.findViewById(R.id.taskhome_item_stateTitle);
                viewHolder.taskhome_item_avatar = (NetworkImageView)view.findViewById(R.id.taskhome_item_avatar);
                //引用
                viewHolder.quote = (TextView)view.findViewById(R.id.quote);

                viewHolder.task_attath_list = (ListView)view.findViewById(R.id.task_attach_list);
                view.setTag(viewHolder);
            }

            ViewHolder holder = (ViewHolder) view.getTag();

            //获取当前显示的这条回复
            final Pushstate taskReply = taskReplyList.get(position);
            //附件列表绑定Adapter
            if (taskReply.getAttachments() != null) {
                //把本条回复的附件复制出来
                attachments.clear();
                for(Attachment attach : taskReply.getAttachments()) {
                    attachments.add(attach);
                }
                AttachmentListAdapter attachmentListAdapter = new AttachmentListAdapter(getActivity(), attachments);
                holder.task_attath_list.setAdapter(attachmentListAdapter);
            } else {
                //如果没有附件则要清空adapter，否则会有缓存
                holder.task_attath_list.setAdapter(null);
            }
            //因为嵌套了附件子listView，为防止子listView显示不全，要设置高度
            Util.setListViewHeight(holder.task_attath_list);

            //回复者及回复内容
            holder.taskhome_item_username.setText(getResources().getString(R.string.template_topichome_username).replace("%name%", taskReply.getCreatorName()));

            //[quote][strong]小明：[/strong]这是小明说的[/quote]这是我自己说的
            String content = taskReply.getComment();
            content = Util.quoteModified(content);
            holder.taskhome_item_content.setText(Html.fromHtml(content));

            holder.taskhome_item_date.setText(DateFunctions.RewriteDate(taskReply.getCreateTime(), "yyyy-M-d", "未指定"));

            //状态变更
            if ( taskReply.getModifications() != null && taskReply.getModifications().getStateChange() != null ) {
                holder.taskhome_item_state.setText(StateMap.getText(taskReply.getModifications().getStateChange().getNewValue()));
                holder.taskhome_item_state.setVisibility(View.VISIBLE);
                holder.taskhome_item_stateTitle.setVisibility(View.VISIBLE);
            } else {
                holder.taskhome_item_state.setVisibility(View.GONE);
                holder.taskhome_item_stateTitle.setVisibility(View.GONE);
            }
            //用户头像
            holder.taskhome_item_avatar.setImageUrl(URL.getUserAvatarURL(taskReply.getCreatorId()), InnoXYZApp.getApplication().getImageLoader());

            //引用 绑定点击函数-跳转界面，带上引用内容
            holder.quote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //将回复者和内容存入bundle
                    Bundle bundle = getArguments();
                    bundle.putString("quotedUser", taskReply.getCreatorName());
                    bundle.putString("quotedContent", taskReply.getComment());
                    //跳转到回复界面
                    new ActivityCommand(NewActivity.class, TaskReply.class, TaskHome.this.getActivity(), bundle, null).Execute();
                }
            });

            return view;
        }
    }

}
