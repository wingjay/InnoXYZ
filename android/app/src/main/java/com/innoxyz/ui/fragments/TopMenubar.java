package com.innoxyz.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.innoxyz.ui.activities.MainActivity;
import com.innoxyz.ui.commands.FragmentCommand;
import com.innoxyz.ui.customviews.ImageTextButton;
import com.innoxyz.ui.fragments.announcement.AnnouncementHome;
import com.innoxyz.ui.fragments.announcement.ProjectAnnouncementList;
import com.innoxyz.ui.fragments.common.BaseFragment;
import com.innoxyz.ui.fragments.document.DocumentList;
import com.innoxyz.ui.fragments.message.MessageHome;
import com.innoxyz.ui.fragments.message.MessageList;
import com.innoxyz.ui.fragments.notify.NotifyList;
import com.innoxyz.ui.fragments.project.ProjectHome;
import com.innoxyz.ui.fragments.project.ProjectList;
import com.innoxyz.ui.fragments.task.ProjectTaskList;
import com.innoxyz.ui.fragments.task.TaskHome;
import com.innoxyz.ui.fragments.topic.ProjectTopicList;
import com.innoxyz.ui.fragments.topic.TopicHome;
import com.innoxyz.ui.fragments.user.Home;
import com.innoxyz.ui.fragments.user.Profile;

import java.util.HashMap;
import java.util.Map;
import com.innoxyz.R;
/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-7-31
 * Time: 下午4:54
 * To change this template use File | Settings | File Templates.
 */
public class TopMenubar extends BaseFragment {
    Class<? extends BaseFragment> currentFragment;

    Map<Class<? extends BaseFragment>,ImageTextButton> fragmentMap = new HashMap<Class<? extends BaseFragment>, ImageTextButton>();

    ImageTextButton notifies,projects,mails,user;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        notifies = new ImageTextButton(getActivity(), R.drawable.icon_event, getResources().getString(R.string.menu_tab_todolist));
        projects = new ImageTextButton(getActivity(), R.drawable.icon_project, getResources().getString(R.string.menu_tab_projects));
        mails = new ImageTextButton(getActivity(), R.drawable.icon_mail, getResources().getString(R.string.menu_tab_mails));
        user = new ImageTextButton(getActivity(), R.drawable.icon_user, getResources().getString(R.string.menu_actionbar_user));

        notifies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FragmentCommand(currentFragment, NotifyList.class, getActivity(), null, null).Execute();
            }
        });

        projects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FragmentCommand(currentFragment, ProjectList.class, getActivity(), null, null).Execute();
            }
        });
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FragmentCommand(currentFragment, Home.class, getActivity(), null, null).Execute();
            }
        });
        mails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FragmentCommand(currentFragment, MessageList.class, getActivity(), null, null).Execute();
            }
        });

        //notifiesFragment
        fragmentMap.put(NotifyList.class, notifies);

        //projectsFragment
        fragmentMap.put(ProjectList.class, projects);
        fragmentMap.put(ProjectHome.class, projects);

        fragmentMap.put(ProjectAnnouncementList.class, projects);
        fragmentMap.put(AnnouncementHome.class, projects);

        fragmentMap.put(DocumentList.class, projects);

        fragmentMap.put(ProjectTaskList.class, projects);
        fragmentMap.put(TaskHome.class, projects);

        fragmentMap.put(ProjectTopicList.class, projects);
        fragmentMap.put(TopicHome.class, projects);

        //mailsFragment
        fragmentMap.put(MessageList.class, mails);
        fragmentMap.put(MessageHome.class, mails);

        //userFragment
        fragmentMap.put(Home.class, user);
        fragmentMap.put(Profile.class, user);
    }

    public void onFragmentChanged(Class<? extends BaseFragment> toFragment){
        //recover
        ImageTextButton imageTextButtonOld = fragmentMap.get(currentFragment);
        if( imageTextButtonOld !=null ){
            imageTextButtonOld.setBackgroundResource(R.drawable.clickable_background_blue);
        }

        currentFragment = toFragment;
        ImageTextButton imageTextButtonNew = fragmentMap.get(currentFragment);

        if( imageTextButtonNew !=null ){
            imageTextButtonNew.setBackgroundResource(R.drawable.clickable_background_selectedblue);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        LinearLayout root = new LinearLayout(getActivity());
        root.addView(notifies);
        root.addView(projects);
        root.addView(mails);
        root.addView(user);
        ((MainActivity)getActivity()).setTopMenubar(this);
        return root;
    }
}
