package com.innoxyz.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.innoxyz.R;

import com.innoxyz.data.remote.AddressURIs;
import com.innoxyz.data.runtime.model.login.LoginProtocol;
import com.innoxyz.global.InnoXYZApp;
import com.innoxyz.network.URL;
import com.innoxyz.ui.commands.ActivityCommand;
import com.innoxyz.ui.fragments.notify.NotifyList;
import com.innoxyz.util.Factory;
import com.innoxyz.util.Util;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.apache.http.Header;

/**
 * login activity.
 * User can choose different account
 */
public class LoginActivity extends FragmentActivity {

    public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";
    //控制顶部导航栏颜色
    SystemBarTintManager tintManager;
    ActionBar actionBar;
    private String mEmail;
    private String mPassword;

    private NetworkImageView userAvatarImageView;
    private EditText mEmailView;
    private EditText mPasswordView;
    private Button clearEmailButton;
    private Button clearPwdButton;
    private Button loginButton;
    private Button signinButton;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 推送：以apikey方式登录
        PushManager.startWork(getApplicationContext(),
                PushConstants.LOGIN_TYPE_API_KEY,
                "OFyFGmLh0sBL6Wi0PoFyuweP");
        if(Factory.hasIcCookie()){ //判断是否存在 cookie
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
            return;
        }

        //清空缓存
        //InnoXYZApp.getApplication().getWebCache().clear();

        //为顶部状态栏设置颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Util.setTranslucentStatus(LoginActivity.this, true);
        }
        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.actionbar_blue);
        //隐藏actionbar
        actionBar = getActionBar();
        if (actionBar != null)
            actionBar.hide();

        setContentView(R.layout.activity_login);


        //顶部欢迎视图
        final View welcomeView = findViewById(R.id.logo_welcome_view);
        userAvatarImageView = (NetworkImageView)findViewById(R.id.user_avatar);
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        //清空输入按钮
        clearEmailButton = (Button)findViewById(R.id.clear_email);
        clearPwdButton = (Button)findViewById(R.id.clear_pwd);
        //获取登录表单视图
        mLoginFormView = findViewById(R.id.login_form);
        //登录按钮
        loginButton = (Button)findViewById(R.id.login_button);
        //注册按钮
        signinButton = (Button)findViewById(R.id.signin_button);
        //获取登录状态视图
        mLoginStatusView = findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

        //设置头像
        int userId = InnoXYZApp.getApplication().getCurrentUserId();
        userAvatarImageView.setImageUrl(AddressURIs.getUserAvatarURL(userId), InnoXYZApp.getApplication().getImageLoader());

        //设置用户名密码
        String localEmail = InnoXYZApp.getApplication().getCurrentUserName();
        String localPwd = InnoXYZApp.getApplication().getCurrentUserKey();
        mEmailView.setText(localEmail);
        mPasswordView.setText(localPwd);
        //修改视图
//        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    //显示欢迎试图
//                    userAvatarImageView.setVisibility(View.GONE);
//                    welcomeView.setVisibility(View.VISIBLE);
//                }else{
//                    //隐藏欢迎试图
//                    userAvatarImageView.setVisibility(View.VISIBLE);
//                    welcomeView.setVisibility(View.GONE);
//                }
//            }
//        };

        //绑定清空监听
        clearEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmailView.setText("");
                mEmailView.requestFocus();
            }
        });

        clearPwdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPasswordView.setText("");
                mPasswordView.requestFocus();
            }
        });

        //登录
        loginButton.setOnClickListener(new View.OnClickListener() {//登录按钮监听器
            @Override
            public void onClick(View view) {
                login();
            }
        });

        //注册
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String signInUrl = "http://www.innoxyz.com/user/self_invite.jsp";
                Uri uri = Uri.parse(signInUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //初始化菜单，返回true则显示，false则不显示
        super.onCreateOptionsMenu(menu);
        return true;
    }

    /**
     * 登录
     */
    public void login() {

        // 校验错误内容
        mEmailView.setError(null);
        mPasswordView.setError(null);

        mEmail = mEmailView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;//是否取消登录（未通过校验）
        View focusView = null;//获取焦点的控件

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (mPassword.length() < 4) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mEmail)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!mEmail.contains("@")) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            //显示登录进行中进度条
            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            //隐藏软键盘
            ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(LoginActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            showProgress(true);
            try {
                //发送网络请求-登录
                RequestParams params = new RequestParams();
                params.put("username", mEmail);
                params.put("password", mPassword);
                params.put("channelId", InnoXYZApp.channelId);
                Factory.getHttpClient().post(URL.LOGIN, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        String response = new String(bytes);
                        //隐藏进度并显示表单
                        showProgress(false);
                        LoginProtocol protocol = LoginProtocol.fromJson(response);
                        if (protocol.isOk()) {
                            //登陆成功，使用 Intent 启动 MainActivity
                            int userId = protocol.getUser().getId();
                            String userRealName = protocol.getUser().getName();
                            //手机本地存储账户密码
                            InnoXYZApp.getApplication().setUserInfo(userId, mEmail, mPassword, userRealName);

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            //密码错误
                            mPasswordView.setError(getString(R.string.error_incorrect_password));
                            mPasswordView.requestFocus();
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        Toast.makeText(LoginActivity.this, "网络连接失败" , Toast.LENGTH_SHORT).show();
                        //隐藏进度并显示表单
                        showProgress(false);
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 显示登录进度条
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            //设置动画持续时间
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mLoginStatusView.setVisibility(View.VISIBLE);
            //若show为1则显示登录状态视图
            mLoginStatusView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0) //1为不透明，0为完全透明
                    .setListener(new AnimatorListenerAdapter() {
                        //当动画结束时调用
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

            mLoginFormView.setVisibility(View.VISIBLE);
            //若show为0则显示登录表单视图
            mLoginFormView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
