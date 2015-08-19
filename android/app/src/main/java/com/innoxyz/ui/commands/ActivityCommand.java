package com.innoxyz.ui.commands;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;

import com.innoxyz.R;
import com.innoxyz.ui.fragments.common.BaseFragment;

import java.util.List;

/**
 * Created by laborish on 14-1-4.
 */
//在主线程中实现Activity的跳转
public class ActivityCommand extends Command{

    public static final String defaultFragment = "com.innoxyz.InnoXYZAndroid.detailFragmentClazz";

    Class<? extends FragmentActivity> toActivityClass;
    Class<? extends BaseFragment> toFragmentClass;

    /**
     * 跳转初始化
     * @param toActivity 目标Activity的类名
     * @param toFragment 目标Fragment的类名
     * @param activity 当前Activity
     * @param bundle 参数
     * @param handler 控制UI线程来执行跳转
     *
     */
    public ActivityCommand(Class<? extends FragmentActivity> toActivity, Class<? extends BaseFragment> toFragment,
                           FragmentActivity activity, Bundle bundle, Handler handler){
        super(activity, bundle, handler);

        toActivityClass = toActivity;
        toFragmentClass = toFragment;
    }
    //跳转实现
    @Override
    protected void executeDo() {
        Intent i = new Intent(activity, toActivityClass);
        //跳转时携带数据
        if(bundle != null){
            i.putExtras(bundle);
        }
        i.putExtra(ActivityCommand.defaultFragment, toFragmentClass);

        activity.startActivity(i);
    }

}
