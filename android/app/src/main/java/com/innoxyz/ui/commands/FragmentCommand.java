package com.innoxyz.ui.commands;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;


import com.innoxyz.ui.fragments.FragmentSwitcher;
import com.innoxyz.ui.fragments.project.ProjectList;
import com.innoxyz.ui.fragments.common.BaseFragment;
import com.innoxyz.ui.fragments.message.MessageList;
import com.innoxyz.ui.fragments.notify.NotifyList;
import com.innoxyz.ui.fragments.user.Home;

import java.util.HashMap;
import java.util.Map;
import com.innoxyz.R;
/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-22
 * Time: 下午1:12
 * To change this template use File | Settings | File Templates.
 */
//切换Fragment
public class FragmentCommand extends Command {

    static Map<Class<? extends BaseFragment>, BaseFragment> fragmentMap ;
    static{
        fragmentMap = new HashMap<Class<? extends BaseFragment>, BaseFragment>();
        fragmentMap.put(NotifyList.class,null);
        fragmentMap.put(ProjectList.class,null);
        fragmentMap.put(MessageList.class,null);
        fragmentMap.put(Home.class,null);
    }

    Class<? extends BaseFragment> fromFragmentClass;
    Class<? extends BaseFragment> toFragmentClass;

    /**
     * 获取fragment切换的基本参数
     * @param fromFragment 当前fragment
     * @param toFragment 目标fragment
     * @param activity 二者所在的Activity
     * @param bundle 传递的数据
     * @param handler 线程间操作
     */
    public FragmentCommand(Class<? extends BaseFragment> fromFragment,Class<? extends BaseFragment> toFragment, FragmentActivity activity, Bundle bundle, Handler handler){
        super(activity, bundle, handler);

        fromFragmentClass = fromFragment;
        toFragmentClass = toFragment;
    }

    @Override
    protected void executeDo() {
        try {
            new FragmentSwitcher(activity, null, toFragmentClass.newInstance(), R.id.frame_content, bundle).replace();
        } catch (Exception e) {  }
    }
}
