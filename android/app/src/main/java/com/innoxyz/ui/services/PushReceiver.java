package com.innoxyz.ui.services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.innoxyz.global.InnoXYZApp;
import com.innoxyz.ui.activities.MainActivity;

import java.util.List;

/**
 * Created by yj on 2015/4/12.
 * 接收推送
 */
public class PushReceiver extends FrontiaPushMessageReceiver{
    @Override
    public void onBind(Context context, int errorCode, String appid, String userId, String channelId, String requestId) {
        InnoXYZApp.channelId = channelId;
    }

    @Override
    public void onUnbind(Context context, int i, String s) {
        System.out.print("onUnbind");
    }

    @Override
    public void onSetTags(Context context, int i, List<String> strings, List<String> strings2, String s) {
        System.out.print("onSetTags");
    }

    @Override
    public void onDelTags(Context context, int i, List<String> strings, List<String> strings2, String s) {
        System.out.print("onDelTags");
    }

    @Override
    public void onListTags(Context context, int i, List<String> strings, String s) {
        System.out.print("onListTags");
    }

    @Override
    public void onMessage(Context context, String s, String s2) {
        System.out.print("onMessage");
    }

    @Override
    public void onNotificationClicked(Context context, String title, String description, String customContentString) {
        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        //System.out.print("onNotificationClicked"); // s:标题 s2:内容
    }
}
