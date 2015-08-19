package com.innoxyz.ui.fragments.project;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.innoxyz.data.runtime.model.ParcelableUser;
import com.innoxyz.data.runtime.model.project.Member;
import com.innoxyz.data.runtime.model.project.MembersProtocol;
import com.innoxyz.global.InnoXYZApp;
import com.innoxyz.network.URL;
import com.innoxyz.ui.fragments.announcement.AnnouncementNew;
import com.innoxyz.ui.fragments.task.TaskNew;
import com.innoxyz.ui.fragments.topic.TopicNew;
import com.innoxyz.ui.activities.NewActivity;
import com.innoxyz.ui.commands.ActivityCommand;
import com.innoxyz.ui.commands.FragmentCommand;
import com.innoxyz.ui.fragments.announcement.ProjectAnnouncementList;
import com.innoxyz.ui.fragments.common.BaseFragment;
import com.innoxyz.ui.fragments.task.ProjectTaskList;
import com.innoxyz.ui.fragments.topic.ProjectTopicList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.innoxyz.R;
import com.innoxyz.util.Factory;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-1
 * Time: 上午11:59
 * To change this template use File | Settings | File Templates.
 */
public class ProjectHome extends BaseFragment {

    private final static String CREATOR = "CREATOR";
    //视图
    View createNewTask, createNewTopic, createNewAnnouncement;
    View showTaskList, showTopicList, showAnnouncementList;
    //创建、查看  视图与目标fragment映射
    Map<View, Class<? extends BaseFragment>> createMap, showMap;
    //项目id
    private int projectId;
    String projectName;
    //创建者ID要在获取项目成员信息中找到“创建者”
    private int projectCreatorId;

    //参与者：修该参与者、前往所在项目
    MembersProtocol membersProtocol;
    //序列化数据
    ArrayList<ParcelableUser> parcelableMembers;
    //实际数据
    List<Member> membersList;

    ActionBar actionBar;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        projectId = getArguments().getInt("projectId");
        projectName = getArguments().getString("projectName");
        actionBar = getActivity().getActionBar();

        //初始化两个映射表
        createMap = new HashMap<>();
        showMap = new HashMap<>();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获取项目成员信息
        parcelableMembers = getArguments().getParcelableArrayList("memberList");
        if (parcelableMembers != null) {
            return;
        }
        //从服务器获取项目成员信息并序列化供后面使用
        parcelableMembers = new ArrayList<>();
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
                    //序列化存入parcelableMembers,同时获取项目创建者ID
                    if (membersList != null) {
                        boolean findCreatorId = false;
                        for (Member m : membersList) {
                            //序列化
                            ParcelableUser parcelableUser = new ParcelableUser(m.getUser().getName(), m.getUser().getId());
                            parcelableMembers.add(parcelableUser);
                            //获取项目创建者ID
                            if (!findCreatorId && m.getRole().getName().equalsIgnoreCase(CREATOR)) {
                                projectCreatorId = m.getUser().getId();
                                findCreatorId = true;
                            }
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
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_project_home, container, false);
        if (actionBar != null) {
            actionBar.setTitle(projectName);
        }
        //获取'创建'视图
        createNewTask = rootView.findViewById(R.id.create_new_task);
        createNewTopic = rootView.findViewById(R.id.create_new_topic);
        createNewAnnouncement = rootView.findViewById(R.id.create_new_announcement);
        //获取'查看'视图
        showTaskList = rootView.findViewById(R.id.show_task_list);
        showTopicList = rootView.findViewById(R.id.show_topic_list);
        showAnnouncementList = rootView.findViewById(R.id.show_announcement_list);

        //初始化映射表
        initMap();
        //'创建' 如果是创建通知，要检查ID
        View.OnClickListener createListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查表，获取要跳转的fragment
                Class<? extends BaseFragment> toFragment = createMap.get(v);
                //如果是发布通知，则检查ID
                if (toFragment == AnnouncementNew.class && projectCreatorId != InnoXYZApp.getApplication().getCurrentUserId()) {
                    Toast.makeText(getActivity(), "只有项目创建者才可以发布新通知", Toast.LENGTH_SHORT).show();
                    return;
                }
                //跳转
                Bundle bundle = new Bundle();
                bundle.putAll(getArguments());
                //把获取的序列化项目成员信息存入bundle中
                if (parcelableMembers != null) {
                    bundle.putParcelableArrayList("memberList", parcelableMembers);
                }
                new ActivityCommand(NewActivity.class, toFragment, ProjectHome.this.getActivity(), bundle, null).Execute();

            }
        };

        //'查看'
        View.OnClickListener showListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查表，获取要跳转的fragment
                Class<? extends BaseFragment> toFragment = showMap.get(v);
                Bundle bundle = new Bundle();
                bundle.putAll(getArguments());
                //把获取的序列化项目成员信息存入bundle中
                if (parcelableMembers != null) {
                    bundle.putParcelableArrayList("memberList", parcelableMembers);
                }
                new FragmentCommand(ProjectHome.class, toFragment, ProjectHome.this.getActivity(), bundle, null).Execute();
            }
        };

        //绑定 监听
        createNewTask.setOnClickListener(createListener);
        createNewTopic.setOnClickListener(createListener);
        createNewAnnouncement.setOnClickListener(createListener);
        //查看
        showTaskList.setOnClickListener(showListener);
        showTopicList.setOnClickListener(showListener);
        showAnnouncementList.setOnClickListener(showListener);

        return rootView;

    }

    //初始化映射表
    private void initMap() {
        createMap.put(createNewTask, TaskNew.class);
        createMap.put(createNewTopic, TopicNew.class);
        createMap.put(createNewAnnouncement, AnnouncementNew.class);

        showMap.put(showTaskList, ProjectTaskList.class);
        showMap.put(showTopicList, ProjectTopicList.class);
        showMap.put(showAnnouncementList, ProjectAnnouncementList.class);
    }

}
