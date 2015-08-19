package com.innoxyz.ui.fragments.notify;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;


import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.gson.reflect.TypeToken;
import com.innoxyz.data.runtime.model.Pager;
import com.innoxyz.data.runtime.model.Protocol;
import com.innoxyz.data.runtime.model.attachment.Attachment;
import com.innoxyz.data.runtime.model.attachment.ParcelableAttachment;
import com.innoxyz.data.runtime.model.notify.Notify;
import com.innoxyz.data.runtime.model.user.User;
import com.innoxyz.global.InnoXYZApp;
import com.innoxyz.network.URL;
import com.innoxyz.ui.activities.DetailActivity;
import com.innoxyz.ui.activities.MainActivity;
import com.innoxyz.ui.commands.ActivityCommand;
import com.innoxyz.ui.customviews.RefreshListView;
import com.innoxyz.ui.fragments.document.DocumentList;
import com.innoxyz.ui.fragments.project.ProjectList;
import com.innoxyz.ui.fragments.announcement.AnnouncementHome;
import com.innoxyz.ui.fragments.common.BaseFragment;
import com.innoxyz.ui.fragments.message.MessageList;
import com.innoxyz.ui.fragments.task.TaskHome;
import com.innoxyz.ui.fragments.topic.TopicHome;
import com.innoxyz.ui.fragments.user.Home;
import com.innoxyz.ui.interfaces.OnRefreshListener;
import com.innoxyz.ui.interfaces.OnSwitchMainTabListener;
import com.innoxyz.ui.utils.DateFunctions;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.innoxyz.R;
import com.innoxyz.util.Factory;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import lombok.Data;

/**
 * Created with IntelliJ IDEA
 * User: InnoXYZ
 * Date: 13-11-26
 * Time: 下午8:19
 * 显示所有通知。
 * 机制：从服务器获取已读/未读通知，显示
 */
// 显示最新的通知
public class NotifyList extends BaseFragment implements OnRefreshListener {

    //指南ID
    private String tutorialId = "0";
    private HashMap<String, String> tutorialMap = new HashMap<>();
    //通知类型
    private class NotifyType{
        public int imageId;
        public int stringId;

        public NotifyType(int stringId, int imageId){
            this.stringId = stringId;
            this.imageId = imageId;
        }
    }
    //指南
    @Data
    private class TutorialType {
        public int subjectId;
        public int introductionId;
        public int toFragmentId; // 要跳转的fragment

        public TutorialType (int subject, int introduction, int toFragmentId) {
            this.subjectId = subject;
            this.introductionId = introduction;
            this.toFragmentId = toFragmentId; // 要跳转的fragment

        }
    }
    public final static HashMap<String,NotifyType> visibleNotifyType = new HashMap<>();
    public final static HashMap<String,Integer> subNotifyType = new HashMap<>();

    //指南内容
    public final static HashMap<String, TutorialType> tutorialList = new HashMap<>();
    //显示通知
    private RefreshListView notifyListView;
    NotifyAdapter notifyAdapter;

    //当前显示的通知
    List<Notify> visibleNotifyList;
    //未读通知
    List<Notify> unDoneNotifyList;
    //已读通知
    List<Notify> doneNotifyList;

    //当前显示已处理/未处理标志
    boolean doneFlag = false;

    //网络请求参数
    RequestParams params;
    //请求第几页，从0开始
    int unDonePage = 0;
    int donePage = 0;
    //是否还有更多，允许继续加载标志
    boolean hasMoreUndone = false;
    boolean hasMoreDone = false;

    //与服务器通信数据对象
    Protocol<Notify> Protocol;
    Protocol<Notify> protocol;//存储通知
    Pager<Notify> pager;


    //当前的ActionBar，修改显示文字
    ActionBar actionBar;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Activity a = getActivity();
        actionBar = a.getActionBar();

        //设置可见通知类型（名称和图片）。当要显示时，给出'TOPIC'，则可以get到一个NotifyType对象，从中可获取中文类型和图片
        //讨 论
        visibleNotifyType.put(getResources().getString(R.string.TOPIC), new NotifyType(R.string.notify_list_topic, R.drawable.icon_discusslist));
        //任 务
        visibleNotifyType.put(getResources().getString(R.string.TASK), new NotifyType(R.string.notify_list_task, R.drawable.icon_tasklist));
        //通 知
        visibleNotifyType.put(getResources().getString(R.string.ANNOUNCEMENT), new NotifyType(R.string.notify_list_announcement, R.drawable.icon_announcementlist));
        //文 件
        visibleNotifyType.put(getResources().getString(R.string.FILE), new NotifyType(R.string.notify_list_file, R.drawable.icon_documentlist));

        //指南
        tutorialList.put("0",
                new TutorialType(R.string.notify_list_tutorial_project, R.string.notify_list_tutorial_project_intro, MainActivity.fragmentID.get(ProjectList.class.getName())));
        tutorialList.put("1",
                new TutorialType(R.string.notify_list_tutorial_setting, R.string.notify_list_tutorial_setting_intro, MainActivity.fragmentID.get(Home.class.getName())));
        tutorialList.put("2",
                new TutorialType(R.string.notify_list_tutorial_user, R.string.notify_list_tutorial_user_intro, MainActivity.fragmentID.get(Home.class.getName())));
        tutorialList.put("3",
                new TutorialType(R.string.notify_list_tutorial_welcome, R.string.notify_list_tutorial_welcome_intro, MainActivity.fragmentID.get(Home.class.getName())));
        tutorialList.put("4",
                new TutorialType(R.string.notify_list_tutorial_mail, R.string.notify_list_tutorial_mail_intro, MainActivity.fragmentID.get(MessageList.class.getName())));

        //设置子通知类型。根据服务器来的字段获取中文标识
        //发起了讨论
        subNotifyType.put("TOPIC_NEW", R.string.notify_list_topic_topic_new);
        //回复了讨论
        subNotifyType.put("REPLY_NEW", R.string.notify_list_topic_reply_new);
        //将你加入讨论
        subNotifyType.put("INVOLVED_NEW", R.string.notify_list_topic_involved_new);
        //分配了任务
        subNotifyType.put("TASK_NEW", R.string.notify_list_task_task_new);
        //更新了任务状态
        subNotifyType.put("TASK_PUSHSTATE", R.string.notify_list_task_task_pushstate);
        //上传了文件
        subNotifyType.put("FILE_NEW", R.string.notify_list_file_file_new);
        //发布了通知
        subNotifyType.put("ANNOUNCE_NEW", R.string.notify_list_announcement_announce_new);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // 创建下拉刷新
        notifyListView = new RefreshListView(getActivity(), null);
        // 创建adapter
        notifyAdapter = new NotifyAdapter();
        // 通知
        visibleNotifyList = new ArrayList<>();
        unDoneNotifyList = new ArrayList<>();
        doneNotifyList = new ArrayList<>();
        // 设置网络请求参数
        params = new RequestParams();
        params.add("pageSize", "10");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_notifylist, container, false);
        notifyListView = (RefreshListView) rootView.findViewById(R.id.notifylist);
        notifyListView.setOnRefreshListener(this);
        //添加Menu
        setHasOptionsMenu(true);
        //请求第几页，从0开始
        unDonePage = 0;
        donePage = 0;
        //绑定adapter
        notifyListView.setAdapter(notifyAdapter);
        //默认显示未读通知
        notifyListView.startRefresh();
        //给每个item绑定点击事件，进入详情页
        notifyListView.setOnItemClickListener(new ClickToDetailListener());
        updateUnDoneNotify();

        return rootView;
    }

    //当下拉刷新时触发
    @Override
    public void onDownPullRefresh() {
        //设置刷新
//        CURRENT_STATUS = REFRESH;
        //刷新当前状态的第0页
        //已读
        if (doneFlag) {
            donePage = 0;
            updateDoneNotify();
        }else {
            unDonePage = 0;
            updateUnDoneNotify();
        }
    }
    //当上拉加载时触发
    @Override
    public void onLoadingMore() {
        //设置加载
//        CURRENT_STATUS = LOAD;
        //已读通知
        if (doneFlag) {
//            if (!hasMoreDone) {
//                notifyListView.hideFooterView();
//            }else {
                donePage++;
                updateDoneNotify();
//            }
        }else {
//            if (!hasMoreUndone) {
//                notifyListView.hideFooterView();
//            }else {
                unDonePage++;
                updateUnDoneNotify();
//            }
        }
    }

    //刷新未读通知
    private void updateUnDoneNotify() {
        //设置加载页码
        params.put("page", String.valueOf(unDonePage));
        params.put("read", "false");
        Factory.getHttpClient().get(URL.LIST_NOTIFY, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String response = new String(bytes);
                Protocol = new Protocol<Notify>();
                protocol = Protocol.fromJson(response, new TypeToken<Protocol<Notify>>() {}.getType());
                if (protocol.isOk()) {
                    pager = protocol.getPager();
                    //如果是刷新，则清空；加载则不清空
                    if (notifyListView.isRefreshing()) {
                        unDoneNotifyList.clear();
                    }
                    unDoneNotifyList.addAll(pager.getData());
                    //可见列表指向它
                    visibleNotifyList = unDoneNotifyList;
                    notifyAdapter.notifyDataSetChanged();
                    //告诉listView的pager内容
                    notifyListView.setDataPager(pager);
                }else {
                    Toast.makeText(getActivity(), "网络故障，请重试", Toast.LENGTH_SHORT).show();
                }
                //修改视图
                changeView();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(getActivity(), "网络连接失败。。。", Toast.LENGTH_SHORT).show();
                //可见列表指向它
                visibleNotifyList = unDoneNotifyList;
                //修改视图
                changeView();
            }
        });
    }
    //刷新已读通知
    private void updateDoneNotify() {
        //设置加载页码
        params.put("page", String.valueOf(donePage));
        params.put("read", "true");
        Factory.getHttpClient().get(URL.LIST_NOTIFY, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String response = new String(bytes);
                Protocol = new Protocol<Notify>();
                protocol = Protocol.fromJson(response, new TypeToken<Protocol<Notify>>() {
                }.getType());
                if (protocol.isOk()) {
                    pager = protocol.getPager();
                    //hasMoreDone = pager.hasMore();
                    //如果是刷新，则清空；加载则不清空
                    if (notifyListView.isRefreshing()) {
                        doneNotifyList.clear();
                    }
                    doneNotifyList.addAll(pager.getData());
                    //可见列表指向它
                    visibleNotifyList = doneNotifyList;
                    notifyAdapter.notifyDataSetChanged();
                    //告诉listView的pager内容
                    notifyListView.setDataPager(pager);
                } else {
                    Toast.makeText(getActivity(), "网络故障，请重试", Toast.LENGTH_SHORT).show();
                }
                //修改视图
                changeView();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(getActivity(), "网络连接失败。。。", Toast.LENGTH_SHORT).show();
                //可见列表指向它
                visibleNotifyList = doneNotifyList;
                //修改视图
                changeView();
            }
        });
    }

    //修改视图,更新通知数据，隐藏下拉刷新和下拉加载头
    private void changeView() {
        notifyListView.hideHeaderFooter();
    }

    public class NotifyAdapter extends BaseAdapter {
        //存放子控件
        class ViewHolder{
            TextView listnotify_button_textView;
            TextView listnotify_item_name;
            TextView listnotify_projname;
            TextView listnotify_last_update_time;
            TextView listnotify_last_update_username;
            TextView listnotify_last_update_content;
            ImageView listnotify_button_imageView;
        }

        @Override
        public int getCount() {
            return visibleNotifyList.size();
        }

        @Override
        public Object getItem(int position) {
            return visibleNotifyList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int i, View view, ViewGroup parent) {
            if(view == null){
                LayoutInflater inflater = getActivity().getLayoutInflater();
                view = inflater.inflate(R.layout.listitem_notify, null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.listnotify_button_imageView = (ImageView) view.findViewById(R.id.listnotify_button_imageView); //通知类型图片
                viewHolder.listnotify_button_textView = (TextView) view.findViewById(R.id.listnotify_button_textView); //通知类型
                viewHolder.listnotify_item_name = (TextView) view.findViewById(R.id.listnotify_item_name); //通知标题
                viewHolder.listnotify_projname = (TextView) view.findViewById(R.id.listnotify_projname); //项目名称
                viewHolder.listnotify_last_update_time = (TextView) view.findViewById(R.id.listnotify_last_update_time); //上次更新时间
                viewHolder.listnotify_last_update_username = (TextView) view.findViewById(R.id.listnotify_last_update_username); //上次更新用户
                viewHolder.listnotify_last_update_content = (TextView) view.findViewById(R.id.listnotify_last_update_content); //上次更新内容

                view.setTag(viewHolder);
            }

            ViewHolder holder = (ViewHolder) view.getTag();

            //给整个一行绑定点击事件用
            view.setTag(R.id.item_object, visibleNotifyList.get(i));
            //通知的左边图片部分
            View acceptButton = view.findViewById(R.id.listnotify_button);
            acceptButton.setTag(R.id.item_object, visibleNotifyList.get(i));//acceptButton 可见的 visibleNotifyList 信息全部存入 R.id.item_object
            acceptButton.setOnClickListener(new ClickToSwitchNotify());

            //从visibleNotifyList获取的thingType，传递给 visibleNotifyType 获取到对应的中文类型和图片
            //如果是指南
            if (visibleNotifyList.get(i).getSubNotifies().size() == 1 && visibleNotifyList.get(i).getSubNotifies().get(0).getSubNotifyType().equals("TUTORIAL")) {
                holder.listnotify_button_textView.setText(R.string.notify_list_tutorial); //指南类型
                holder.listnotify_button_imageView.setImageResource(R.drawable.icon_tutoriallist); //指南图片 R.drawable.icon_tutorial
                holder.listnotify_item_name.setText(visibleNotifyList.get(i).getSubNotifies().get(0).getCreatorName() + ",欢迎加入InnoXYZ!");//标题
                holder.listnotify_last_update_time.setText(DateFunctions.RewriteDate(visibleNotifyList.get(i).getLast(), "yyyy-M-d", "unknown")); //上次更新时间
                //一个指南ID对应一个数字字符 "0"->"4"
                if (!tutorialMap.containsKey(visibleNotifyList.get(i).getId())) {
                    tutorialMap.put(visibleNotifyList.get(i).getId(), String.valueOf(tutorialMap.size()));
                }
                tutorialId = tutorialMap.get(visibleNotifyList.get(i).getId());
                holder.listnotify_last_update_content.setText(tutorialList.get(tutorialId).getIntroductionId());//介绍性文字
                holder.listnotify_last_update_username.setText(""); //最新回复的用户
                holder.listnotify_projname.setText(""); //项目名称
            } else {
                //不是指南
                holder.listnotify_button_textView.setText(visibleNotifyType.get(visibleNotifyList.get(i).getThingType()).stringId);
                holder.listnotify_button_imageView.setImageResource(visibleNotifyType.get(visibleNotifyList.get(i).getThingType()).imageId);
                holder.listnotify_item_name.setText(visibleNotifyList.get(i).getThing().getName()); //通知标题
                holder.listnotify_projname.setText(visibleNotifyList.get(i).getHostName()); //项目名称
                holder.listnotify_last_update_time.setText(DateFunctions.RewriteDate(visibleNotifyList.get(i).getLast(), "yyyy-M-d", "unknown")); //上次更新时间
                //显示最新的一条子回复
                if(visibleNotifyList.get(i).getSubNotifies().size() > 0){
                    holder.listnotify_last_update_username.setText(visibleNotifyList.get(i).getSubNotifies().get(0).creatorName); //最新回复的用户
                    holder.listnotify_last_update_content.setText(subNotifyType.get(visibleNotifyList.get(i).getSubNotifies().get(0).subNotifyType)); //获取中文标识
                }
            }

            return view;
        }
    }

    //点击则进入详情页
    private class ClickToDetailListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //检测刷新头是否出现，若出现，则屏蔽此次点击事件
            if (!notifyListView.isHeaderHidden())
                return;
            //view里的Tag-R.id.item_object包含了这条通知的全部信息,在adapter已经绑定
            Notify notify = (Notify) view.getTag(R.id.item_object);
            //TUTORIAL
            if (tutorialMap.containsKey(notify.getId())) {
                //跳转到对应的Fragment
                OnSwitchMainTabListener switchTab = (OnSwitchMainTabListener)getActivity();
                switchTab.switchFragment(tutorialList.get(tutorialMap.get(notify.getId())).getToFragmentId());
                return;
            }
            //如果不是指南，则跳转详情页
            //notify即这条通知的内容，把数据转换到 bundle 中，并传送到 DetailActivity
            Bundle bundle = new Bundle();
            bundle.putInt("projectId", notify.getHostId());
            bundle.putString("projectName", notify.getHostName()); //项目名称

            //TASK
            if(notify.getThingType().equals(getResources().getString(R.string.TASK))){
                bundle.putString("taskId", notify.getThing().id);
                bundle.putString("taskName", notify.getThing().name); //任务名称
                bundle.putString("hostType", notify.getThing().hostType); //LPROJECT
                bundle.putInt("creatorId", notify.getThing().creatorId); //任务创建者
                bundle.putString("creatorName", notify.getThing().creatorName); //创始人
                //分配给
                String assignees = "";
                if (notify.getThing().assignees != null) {
                    for(User u : notify.getThing().assignees) {
                        assignees += u.realName + " ";
                    }
                }
                bundle.putString("assignees", assignees); //被分配者
                bundle.putString("priority", notify.getThing().priority.toString()); //优先级
                bundle.putString("description", notify.getThing().description); //描述
                bundle.putString("deadline", notify.getThing().deadline); //截止日期
                bundle.putString("state", notify.getThing().state.toString()); //当前任务状态
                //携带bundle数据，跳转到 DetailActivity
                new ActivityCommand(DetailActivity.class, TaskHome.class, NotifyList.this.getActivity(), bundle, null).Execute();
            }

            //TOPIC
            else if(notify.getThingType().equals(getResources().getString(R.string.TOPIC))){
                bundle.putString("topicId", notify.getThing().id); //讨论id
                bundle.putString("topicName", notify.getThing().getSubject());
                bundle.putString("hostType", notify.getThing().hostType); //LPROJECT
                bundle.putInt("creatorId", notify.getThing().creatorId); //话题创建者
                new ActivityCommand(DetailActivity.class, TopicHome.class, NotifyList.this.getActivity(), bundle, null).Execute();
            }

            //ANNOUNCEMENT
            else if(notify.getThingType().equals(getResources().getString(R.string.ANNOUNCEMENT))){
                bundle.putString("title", notify.getThing().getName()); //标题
                bundle.putString("time", notify.getLast()); //时间
                bundle.putString("content", notify.getThing().content); //内容
                bundle.putString("creatorName", notify.getThing().creatorName); //内容

                //附件
                if (notify.getThing().getAttachments() != null) {
                    //要传递的序列化对象
                    ArrayList<ParcelableAttachment> attachments = new ArrayList<>();
                    //把原始数据放入序列化对象中
                    for (Attachment attachment : notify.getThing().getAttachments()) {
                        ParcelableAttachment a = new ParcelableAttachment(attachment.getId(), attachment.getName());
                        attachments.add(a);
                    }
                    //传递序列化对象
                    bundle.putParcelableArrayList("attachments", attachments);
                }
                new ActivityCommand(DetailActivity.class, AnnouncementHome.class, NotifyList.this.getActivity(), bundle, null).Execute();

            }
            //FILE
            else if (notify.getThingType().equals(getResources().getString(R.string.FILE))) {

                new ActivityCommand(DetailActivity.class, DocumentList.class, NotifyList.this.getActivity(), bundle, null).Execute();
            }

        }

    }


    //点击则将通知的处理状态切换
    private class ClickToSwitchNotify implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            final View clickView = view;
            //弹出删除确认框
            final NiftyDialogBuilder dialogBuilder = Factory.getNiftyDialog(getActivity());
            if (doneFlag)
                dialogBuilder.withMessage("是否要标记为未办事项？\n\n");
            else
                dialogBuilder.withMessage("是否要标记为已办事项？\n\n");

            dialogBuilder.withTitle("提示")
                    .withButton2Text("确定")
                    .withButton1Text("取消")
                    .setButton2Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switchNotifyState(clickView);
                            dialogBuilder.dismiss();
                        }
                    })
                    .setButton1Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogBuilder.dismiss();
                        }
                    })
                    .show();

        }
    }

    //根据doneFlag切换状态
    private void switchNotifyState(View view) {
        final Notify notify = (Notify) view.getTag(R.id.item_object);
        //todo:TUTORIAL 点击无效
        String a = notify.getId();
        if (tutorialMap.containsKey(a)) {
            return;
        }
        //将点击的通知设置为已读
        RequestParams params1 = new RequestParams();
        params1.add("thingId", notify.getThing().id);
        params1.add("thingType", notify.getThingType());
        params1.add("value", doneFlag?"true":"false");

        Factory.getPostHttpClient().post(URL.SET_NOTIFY_READ, params1, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                visibleNotifyList.remove(notify);
                notifyAdapter.notifyDataSetChanged();
                if (doneFlag)
                    Toast.makeText(getActivity(), R.string.notify_set_unread_success, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getActivity(), R.string.notify_set_read_success, Toast.LENGTH_SHORT).show();
                //刷新未读消息数
                InnoXYZApp.getApplication().getGetUnreadNumNow().release();

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(getActivity(), R.string.notify_set_read_fail, Toast.LENGTH_SHORT).show();
            }
        });
    }


    //创建顶部菜单
    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.notify_fragment_actions, menu);
    }

    //点击菜单那按钮
    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        int id = item.getItemId();
        //切换已读/未读,刷新视图
        if (id == R.id.action_switch){
            doneFlag = !doneFlag;
            if(doneFlag) {
                actionBar.setTitle(R.string.title_nofity_done);
                //设置已读图标
                item.setIcon(R.drawable.notify_readed);

                //刷新第1页
                donePage = 0;
                // 启动刷新
                notifyListView.startRefresh();
                updateDoneNotify();

            }
            else{
                actionBar.setTitle(R.string.title_nofity_undone);
                //设置已读图标
                item.setIcon(R.drawable.notify_unread);

                //刷新第1页
                unDonePage = 0;
                // 启动刷新
                notifyListView.startRefresh();
                updateUnDoneNotify();

            }
        }
        return true;
    }

}
