package com.innoxyz.ui.fragments.message;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.innoxyz.ui.activities.NewActivity;
import com.innoxyz.ui.commands.ActivityCommand;
import com.innoxyz.ui.fragments.common.BaseFragment;
import com.innoxyz.R;
import com.innoxyz.ui.utils.DateFunctions;
import com.innoxyz.util.Util;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-11-29
 * Time: 下午8:26
 * To change this template use File | Settings | File Templates.
 */
public class MessageHome extends BaseFragment {

    private String senderName,recivers,subject,content,sendTime;
    private boolean inBoxFlag;

    private ActionBar actionBar;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        inBoxFlag = getArguments().getBoolean("inBox");
        senderName = getArguments().getString("senderName");
        subject = getArguments().getString("subject");
        content = getArguments().getString("content");
        recivers = getArguments().getString("recivers");
        sendTime = getArguments().getString("sendTime");
        actionBar = getActivity().getActionBar();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar.setTitle(R.string.menu_tab_mails);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        View ret = inflater.inflate(R.layout.fragment_message_home, container, false);
        //主题
        ((TextView)ret.findViewById(R.id.message_home_subject)).setText(subject);

        //名字
        if(inBoxFlag){
            ((TextView)ret.findViewById(R.id.message_home_nameText)).setText(senderName);
        }
        else{
            ((TextView)ret.findViewById(R.id.message_home_nameText)).setText(recivers);
        }

        //时间
        ((TextView)ret.findViewById(R.id.message_home_time)).setText(DateFunctions.RewriteDate(sendTime, "yyyy年MM月dd日 HH:mm ", "unknown"));

        //显示内容：转换\t \n为html标签；文字超链接和网址可点击；文本可复制
        TextView contentTextView = ((TextView)ret.findViewById(R.id.message_home_content));
        content =  Util.unescape(content);
        contentTextView.setText(Html.fromHtml(content));
        contentTextView.setMovementMethod(LinkMovementMethod.getInstance());

        return ret;

    }


    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
        if(inBoxFlag){
            inflater.inflate(R.menu.message_reply_action, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        int id = item.getItemId();
        if (id == R.id.action_reply){
            //回信
            Bundle bundle = new Bundle();
            bundle.putAll(getArguments());
            new ActivityCommand(NewActivity.class, MessageNew.class, MessageHome.this.getActivity(), bundle, null).Execute();
            return true;
        }

        return false;
    }
}
