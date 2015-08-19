package com.innoxyz.ui.fragments.user;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;

import com.google.gson.reflect.TypeToken;
import com.innoxyz.data.remote.AddressURIs;
import com.innoxyz.data.remote.StringRequestBuilder;
import com.innoxyz.data.remote.response.JsonResponseHandler;
import com.innoxyz.data.runtime.SimpleObservedData;
import com.innoxyz.data.runtime.interfaces.IDataObserver;
import com.innoxyz.data.runtime.model.Protocol;
import com.innoxyz.data.runtime.model.user.*;
import com.innoxyz.global.InnoXYZApp;
import com.innoxyz.network.URL;
import com.innoxyz.ui.customviews.SimpleTable;
import com.innoxyz.ui.customviews.SimpleTableHeaderFactory;
import com.innoxyz.ui.fragments.common.BaseFragment;
import com.innoxyz.ui.utils.DateFunctions;

import java.util.Arrays;
import java.util.HashMap;

import com.innoxyz.R;
import com.innoxyz.util.Factory;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-10-16
 * Time: 上午11:41
 * To change this template use File | Settings | File Templates.
 */
public class Profile extends BaseFragment {

    private static final int SHOW_USER_PROFILE = 1;
    //用户信息
    UserInfo info;
    UserDetail detail;
    UserProfile profile;

    ActionBar actionBar;

    Handler mHandler;
    LinearLayout innerLayout;
    //控件
    private NetworkImageView avatarImage;
    private TextView nameText;
    private TextView institutionText;
    private TextView titleText;

    //获取用户个人信息
    private void getUserProfile() {
        Factory.getHttpClient().get(URL.USER_PROFILE, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Protocol<UserInfo> theProtocol = new Protocol<UserInfo>();
                Protocol<UserInfo> protocol = theProtocol.fromJson(response, new TypeToken<Protocol<UserInfo>>(){}.getType());
                if (protocol.isOk()) {
                    //获取用户信息
                    info = protocol.getData();
                    //用户详情
                    detail = info.getDetail();
                    //用户经历
                    profile = info.getProfile();
                    //发送 Message
                    mHandler.sendEmptyMessage(SHOW_USER_PROFILE);
                } else {
                    Toast.makeText(getActivity(), "用户信息获取失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        actionBar = getActivity().getActionBar();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化
        info = new UserInfo();
        detail = new UserDetail();
        profile = new UserProfile();
        //获取用户信息
        getUserProfile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        actionBar.setTitle(R.string.title_profile);
        //思路：动态添加，顶部的用户信息单独放在一个xml里，整个是ScrollView，里面放一个LinearLayout，然后逐行放置子view到linearLayout中。
        //先放入ScrollView
        ScrollView scrollView = new ScrollView(getActivity());
        //基本线性布局框架
        innerLayout = new LinearLayout(getActivity());
        innerLayout.setOrientation(LinearLayout.VERTICAL);
        //放入基本信息
        View infoView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_user_profile_header, null, false);
        avatarImage = (NetworkImageView)infoView.findViewById(R.id.avatar);
        nameText = (TextView)infoView.findViewById(R.id.name);
        institutionText = (TextView)infoView.findViewById(R.id.institution);
        titleText = (TextView)infoView.findViewById(R.id.title);

        innerLayout.addView(infoView);

        //详细信息-项目经历与教育经历
        final SimpleTableHeaderFactory headerFactory = new SimpleTableHeaderFactory(inflater);
        //使用handler，在从服务器获取到数据后再显示
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == SHOW_USER_PROFILE) {
                    //个人信息
                    avatarImage.setImageUrl(URL.getUserAvatarURL(detail.getId()), InnoXYZApp.getApplication().getImageLoader());
                    nameText.setText(info.getName());
                    institutionText.setText(detail.getInstitution());
                    titleText.setText(detail.getTitle());
                    if (profile.getExperiences() != null) {
                        innerLayout.addView(headerFactory.create("项目"));
                        for (Experience exp : profile.getExperiences()) {
                            final HashMap<String, String> map = new HashMap<String, String>();
                            map.put("名称", exp.title);
                            map.put("类别", exp.classes);
                            map.put("简介", exp.summary);
                            map.put("项目周期", "从 " + DateFunctions.RewriteDate(exp.start, "yyyy年MM月dd日", "unknown") + " 到 " + DateFunctions.RewriteDate(exp.end, "yyyy年MM月dd日", "unknown"));
                            innerLayout.addView(new SimpleTable(getActivity(), Arrays.asList("名称", "类别", "项目周期", "简介"), map));
                        }
                    }
                    if (profile.getEducations() != null) {
                        innerLayout.addView(headerFactory.create("学历"));
                        for (Education edu : profile.getEducations()) {
                            final HashMap<String, String> map = new HashMap<String, String>();
                            map.put("院校", edu.university);
                            map.put("专业", edu.major);
                            map.put("学位", edu.degree);
                            map.put("在校时间", "从 " + DateFunctions.RewriteDate(edu.start, "yyyy年MM月dd日", "unknown") + " 到 " + DateFunctions.RewriteDate(edu.end, "yyyy年MM月dd日", "unknown"));
                            innerLayout.addView(new SimpleTable(getActivity(), Arrays.asList("院校", "专业", "学位", "在校时间"), map));
                        }
                    }
                    if (profile.getPapers() != null) {
                        innerLayout.addView(headerFactory.create("论文"));
                        for (Paper paper : profile.getPapers()) {
                            final HashMap<String, String> map = new HashMap<String, String>();
                            map.put("名称", paper.title);
                            map.put("关键字", paper.keywords);
                            map.put("发布于", paper.publisher);
                            map.put("发布日期", DateFunctions.RewriteDate(paper.date));
                            map.put("摘要", paper.abstracts);
                            innerLayout.addView(new SimpleTable(getActivity(), Arrays.asList("名称", "关键字", "发布于", "发布日期", "摘要"), map));
                        }
                    }
                }
            }

        };

        scrollView.removeAllViews();
        scrollView.addView(innerLayout);

         return scrollView;
    }


}
