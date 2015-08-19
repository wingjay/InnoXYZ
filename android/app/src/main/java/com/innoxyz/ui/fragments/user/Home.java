package com.innoxyz.ui.fragments.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.innoxyz.data.remote.AddressURIs;
import com.innoxyz.global.InnoXYZApp;
import com.innoxyz.network.URL;
import com.innoxyz.ui.activities.DetailActivity;
import com.innoxyz.ui.activities.LoginActivity;
import com.innoxyz.ui.activities.NewActivity;
import com.innoxyz.ui.commands.ActivityCommand;
import com.innoxyz.ui.fragments.common.BaseFragment;
import com.innoxyz.R;
import com.innoxyz.util.Factory;

/**
 * User: yj
 * Date: 15-3-3
 * Time: 下午1:59
 */
public class Home extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user, container, false);
        //用户头像
        NetworkImageView userAvatar = (NetworkImageView)rootView.findViewById(R.id.avatar);
        TextView userName = (TextView)rootView.findViewById(R.id.name);
        //获取四个视图，绑定点击函数
        View infoView = rootView.findViewById(R.id.info);
        View inviteView = rootView.findViewById(R.id.invite);
        View settingView = rootView.findViewById(R.id.setting);
        View documentView = rootView.findViewById(R.id.document);
        View logoutView = rootView.findViewById(R.id.logout);

        //用户头像、名字
        int myUserId = InnoXYZApp.getApplication().getCurrentUserId();
        userAvatar.setImageUrl(URL.getUserAvatarURL(myUserId), InnoXYZApp.getApplication().getImageLoader());
        userName.setText(InnoXYZApp.getApplication().getCurrentUserRealName());
        //个人信息
        infoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转
                new ActivityCommand(DetailActivity.class, Profile.class, Home.this.getActivity(), null, null).Execute();
            }
        });
        //邀请
        inviteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转
                new ActivityCommand(NewActivity.class, Invite.class, Home.this.getActivity(), null, null).Execute();
            }
        });
        //设置
        settingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转
                new ActivityCommand(NewActivity.class, Changepass.class, Home.this.getActivity(), null, null).Execute();
            }
        });
        //个人文档
        documentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转
                new ActivityCommand(DetailActivity.class, LocalDocumentList.class, Home.this.getActivity(), null, null).Execute();
            }
        });

        //登出
        logoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //登出-删除cookie
                Factory.deleteLocalCookie();
                //跳到登录界面
                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });

        return rootView;
    }


}
