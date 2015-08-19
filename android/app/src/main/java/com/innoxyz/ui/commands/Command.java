package com.innoxyz.ui.commands;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;

import com.innoxyz.global.InnoXYZApp;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-11
 * Time: 上午10:27
 * To change this template use File | Settings | File Templates.
 */
//在主线程中执行指令
public abstract class Command {
    protected FragmentActivity activity;
    protected Bundle bundle;
    protected Handler handler;

    protected Command(FragmentActivity activity, Bundle bundle, Handler handler) {
        this.activity = activity;
        this.bundle = bundle;
        this.handler = handler;
    }
    //执行
    public void Execute() {
        if ( handler==null ) {
            handler = InnoXYZApp.getApplication().getMainThreadHandler();
        }
        //把runnable对象压入消息队列供UI线程调用
        handler.post(new Runnable() {
                @Override
                public void run() {
                    executeDo();
                }
            });
    }

    protected abstract void executeDo();
}
