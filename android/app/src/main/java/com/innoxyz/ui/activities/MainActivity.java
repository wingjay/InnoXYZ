package com.innoxyz.ui.activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.innoxyz.network.URL;
import com.innoxyz.ui.fragments.project.ProjectList;
import com.innoxyz.ui.fragments.TopMenubar;
import com.innoxyz.ui.fragments.message.MessageList;
import com.innoxyz.ui.fragments.notify.NotifyList;
import com.innoxyz.ui.fragments.user.Home;
import com.innoxyz.ui.interfaces.OnSwitchMainTabListener;
import com.innoxyz.ui.services.GetUnreadNumService;
import com.innoxyz.util.Factory;
import com.innoxyz.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.innoxyz.R;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.apache.http.Header;

import lombok.Data;

/**
 * Created by yj.
 * Date: 14-12-19
 * Time: 下午3:56
 */
public class MainActivity extends FragmentActivity implements OnSwitchMainTabListener {

    @Data
    public class TabView{
        //底部的tab栏
        TextView textView;
        ImageView imageView;

        public TabView(TextView text, ImageView image){
            this.textView = text;
            this.imageView = image;
        }
    }
    //存储被切换的actionBar状态
    @Data
    public class ActionBarState {
        String title;
        Integer iconId;

        ActionBarState(String mTitle, Integer mIconID) {
            this.title = mTitle;
            this.iconId = mIconID;
        }
    }

    //控制顶部导航栏颜色
    SystemBarTintManager tintManager;
    //图片集合
    public static final Integer[] lightImageId = {R.drawable.notify_tab_icon_light, R.drawable.project_tab_icon_light, R.drawable.mail_tab_icon_light, R.drawable.user_tab_icon_light};
    public static final Integer[] imageId = {R.drawable.notify_tab_icon, R.drawable.project_tab_icon, R.drawable.mail_tab_icon, R.drawable.user_tab_icon};
    public static final Map<String, Integer> fragmentID = new HashMap<>();
    private static final String TAG = "MainActivity";
    public ViewPager viewPager;
    private List<Fragment> fragmentsList;
    private List<TabView> Tabs;
    //顶部ActionBar
    private ActionBar actionBar;
    private Map<Class<? extends Fragment>, ActionBarState> actionBarContent;
    //底栏
    private ImageView bottomLine;
    private int position_one, position_two, position_three;
    private int offset = 0;

    //当前显示fragment标志
    private int currentIndex = 0;
    Fragment NotifyFragment;
    Fragment ProjectFragment;
    Fragment MessageFragment;
    Fragment HomeFragment;

    //获取未读消息数
    private TextView unReadTextView;
    private ImageView unReadImageView;
    private int unReadNum;
    private BroadcastReceiver unReadNumReceiver;
    private Intent unReadNumIntent;

    private Resources resources;

    //开机启动画面
    private static final int STOPSPLASH = 0;
    //time in milliseconds
    private static final long SPLASHTIME = 3000;

    private LinearLayout splash;
    private Animation myAnimation_Alpha;
    private Animation animatinoGone ;

    private long exitTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //为顶部标题栏设置颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Util.setTranslucentStatus(MainActivity.this, true);
        }
        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.actionbar_blue);

        actionBar = getActionBar();

        //设置开机启动动画-显示
        Handler splashHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case STOPSPLASH:
                        splash.startAnimation(animatinoGone);
                        splash.setVisibility(View.GONE);
                        //actionBar.show();
                        break;
                }
                super.handleMessage(msg);
            }
        };

//        actionBar.hide();//隐藏actionbar
        animatinoGone = AnimationUtils.loadAnimation(this, R.anim.abc_fade_out); //动画效果
        myAnimation_Alpha = AnimationUtils.loadAnimation(this,R.anim.alpha_action); //动画效果

        //视图
        setContentView(R.layout.activity_main);

        splash = (LinearLayout) findViewById(R.id.splashscreen);
        splash.startAnimation(myAnimation_Alpha); //1s 逐渐显示
        //隐藏
        Message msg = new Message();
        msg.what = STOPSPLASH;
        splashHandler.sendMessageDelayed(msg, SPLASHTIME);

        //存储fragment列表
        fragmentID.put(NotifyList.class.getName(), 0);
        fragmentID.put(ProjectList.class.getName(), 1);
        fragmentID.put(MessageList.class.getName(), 2);
        fragmentID.put(Home.class.getName(), 3);

        unReadTextView = (TextView)findViewById(R.id.unread_notifies_num);
        unReadImageView = (ImageView)findViewById(R.id.unread_tag);

        resources = getResources();
        //根据当前显示的Fragment显示默认内容

        actionBarContent = new HashMap<>();
        actionBarContent.put(NotifyList.class, new ActionBarState(resources.getString(R.string.title_nofity_undone), 0));
        actionBarContent.put(ProjectList.class, new ActionBarState(resources.getString(R.string.title_project_list_all), 0));
        actionBarContent.put(MessageList.class, new ActionBarState(resources.getString(R.string.title_message_inbox), 0));
        actionBarContent.put(Home.class, new ActionBarState(resources.getString(R.string.title_user_home), 0));
        InitWidth();
        InitTextView();
        InitViewPager();
        // 创建获取未读消息数服务，后面会启动
        unReadNumIntent = new Intent(MainActivity.this, GetUnreadNumService.class);
        // 注册广播接收器来接收未读消息数,不断的接收。若当前处于Resume状态，则去实时修改。
        IntentFilter intentFilter = new IntentFilter(GetUnreadNumService.UNREAD_COUNT);
        unReadNumReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                unReadNum = intent.getIntExtra(GetUnreadNumService.UNREAD_NOTIFIES, -1);
                if(unReadNum <= 0){
                    unReadImageView.setVisibility(View.GONE);
                    unReadTextView.setText("");
                }
                else {
                    unReadImageView.setVisibility(View.VISIBLE);
                    if( unReadNum > 99){
                        unReadTextView.setText("N+");
                    } else {
                        unReadTextView.setText(String.valueOf(unReadNum));
                    }
                }
            }
        };
        registerReceiver(unReadNumReceiver, intentFilter);


    }

    /**
     * 运行中开启服务，获取未读消息数
     */
    @Override
    protected void onResume() {
        super.onResume();

        startService(unReadNumIntent);
    }

    /**
     * 停止时关闭服务
     */
    @Override
    protected void onPause() {
        super.onPause();

        stopService(unReadNumIntent);
    }


    /**
     * 销毁时注销接收器
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(unReadNumReceiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 初始化底栏，获取相应宽度信息
     */
    private void InitWidth() {
        //bottomLine = (ImageView) findViewById(R.id.bottom_line);
        //获取屏幕宽度
        int screenWidth = Util.getScreenWidth(MainActivity.this);
        //底栏位置
        position_one = (int) (screenWidth / 4.0 );
        position_two = position_one * 2;
        position_three = position_one * 3;
    }

    /**
     * 获取底栏四个Tab，监听点击事件
     */
    private void InitTextView() {
        // 四个底部Tab indexTab, projectTab, mailTab, userTab;
        TextView notify_tab_text, project_tab_text, mail_tab_text, user_tab_text;
        ImageView notify_tab_icon, project_tab_icon, mail_tab_icon, user_tab_icon;
        notify_tab_text = (TextView)findViewById(R.id.notify_tab_text);
        project_tab_text = (TextView)findViewById(R.id.project_tab_text);
        mail_tab_text = (TextView)findViewById(R.id.mail_tab_text);
        user_tab_text = (TextView)findViewById(R.id.user_tab_text);

        notify_tab_icon = (ImageView)findViewById(R.id.notify_tab_icon);
        project_tab_icon = (ImageView)findViewById(R.id.project_tab_icon);
        mail_tab_icon = (ImageView)findViewById(R.id.mail_tab_icon);
        user_tab_icon = (ImageView)findViewById(R.id.user_tab_icon);


        TabView nofityTab = new TabView(notify_tab_text, notify_tab_icon);
        TabView projectTab = new TabView(project_tab_text, project_tab_icon);
        TabView mailTab = new TabView(mail_tab_text, mail_tab_icon);
        TabView userTab = new TabView(user_tab_text, user_tab_icon);

        //存入Tabs中
        Tabs = new ArrayList<>();
        Tabs.add(0, nofityTab);
        Tabs.add(1, projectTab);
        Tabs.add(2, mailTab);
        Tabs.add(3, userTab);
        //点击监听器
        findViewById(R.id.notify_tab).setOnClickListener(new TabClickListener(0));
        findViewById(R.id.project_tab).setOnClickListener(new TabClickListener(1));
        findViewById(R.id.mail_tab).setOnClickListener(new TabClickListener(2));
        findViewById(R.id.user_tab).setOnClickListener(new TabClickListener(3));
//        int index = 0;
//        for (TabView tabView : Tabs) {
//            tabView.getImageView().setOnClickListener(new TabClickListener(index));
//            tabView.getTextView().setOnClickListener(new TabClickListener(index));
//            index ++;
//        }
    }


    /**
     * 根据点击来设置当前tab
     */
    public class TabClickListener implements View.OnClickListener{
        private int index = 0;

        public TabClickListener(int i){
            this.index = i;
        }

        @Override
        public void onClick(View view) {
            viewPager.setCurrentItem(index);
        }
    }

    /**
     * 初始化ViewPager
     */
    private void InitViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        //fragment容器
        fragmentsList = new ArrayList<>(4);

        //创建四个Fragment实例
        NotifyFragment = Fragment.instantiate(this, NotifyList.class.getName());
        ProjectFragment = Fragment.instantiate(this, ProjectList.class.getName());
        MessageFragment = Fragment.instantiate(this, MessageList.class.getName());
        HomeFragment = Fragment.instantiate(this, Home.class.getName());

        //添加到Fragment容器
        fragmentsList.add(NotifyFragment);
        fragmentsList.add(ProjectFragment);
        fragmentsList.add(MessageFragment);
        fragmentsList.add(HomeFragment);

        //给ViewPager设置适配器
        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager(), fragmentsList));

        //设置默认视图
        viewPager.setCurrentItem(0);

        //给ViewPager设置点击监听器
        viewPager.setOnPageChangeListener(new PageChangeListener());
        //设置ViewPager的缓存界面数，防止在频繁切换时闪退
        viewPager.setOffscreenPageLimit(3);

    }
    /**
     * 供内部fragment切换
     */
    @Override
    public void switchFragment(int index) {
        viewPager.setCurrentItem(index);
    }

    /**
     * ViewPager的Adapter
     */
    public class MyViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mViewList;

        public MyViewPagerAdapter(android.support.v4.app.FragmentManager fm, List<Fragment> viewList) {
            super(fm);
            mViewList = new ArrayList<>();
            this.mViewList = viewList;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int i) {

            return mViewList.get(i);
        }

        @Override
        public int getCount() {
            return mViewList.size();
        }

    }

    /**
     * 页面滑动监听
     */
    public class PageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int i) {
            //保存当前fragment的title
            actionBarContent.put(fragmentsList.get(currentIndex).getClass(), new ActionBarState(actionBar.getTitle().toString(), 0));
            //为即将显示的fragment设置title
            String actionBarTitle = actionBarContent.get(fragmentsList.get(i).getClass()).title;
            actionBar.setTitle(actionBarTitle);
            //动画
            Animation animation = null;
            switch (i) {
                case 0:
                    if (currentIndex == 1) {
                        //代码生成滑动动画
                        animation = new TranslateAnimation(position_one, 0, 0, 0);
                    } else if (currentIndex == 2) {
                        animation = new TranslateAnimation(position_two, 0, 0, 0);
                    } else if (currentIndex == 3) {
                        animation = new TranslateAnimation(position_three, 0, 0, 0);
                    }
                    break;
                case 1:
                    if (currentIndex == 0) {
                        animation = new TranslateAnimation(0, position_one, 0, 0);
                    } else if (currentIndex == 2) {
                        animation = new TranslateAnimation(position_two, position_one, 0, 0);
                    } else if (currentIndex == 3) {
                        animation = new TranslateAnimation(position_three, position_one, 0, 0);
                    }
                    break;
                case 2:
                    if (currentIndex == 0) {
                        animation = new TranslateAnimation(0, position_two, 0, 0);
                    } else if (currentIndex == 1) {
                        animation = new TranslateAnimation(position_one, position_two, 0, 0);
                    } else if (currentIndex == 3) {
                        animation = new TranslateAnimation(position_three, position_two, 0, 0);
                    }
                    break;
                case 3:
                    if (currentIndex == 0) {
                        animation = new TranslateAnimation(0, position_three, 0, 0);
                    } else if (currentIndex == 1) {
                        animation = new TranslateAnimation(position_one, position_three, 0, 0);
                    } else if (currentIndex == 2) {
                        animation = new TranslateAnimation(position_two, position_three, 0, 0);
                    }
                    break;
            }
            //改变item为currentIndex的文字颜色为白色,图片变暗
            Tabs.get(currentIndex).getTextView().setTextColor(resources.getColor(R.color.text_gray));
            Tabs.get(currentIndex).getImageView().setImageResource(imageId[currentIndex]);
            //改变item为i的文字颜色为黑色,图片点亮
            Tabs.get(i).getTextView().setTextColor(resources.getColor(R.color.innoxyz_blue));
            Tabs.get(i).getImageView().setImageResource(lightImageId[i]);
            //记录当前index
            currentIndex = i;
            //动画播放完，保持结束时的状态
            if (animation!=null){
                animation.setFillAfter(true);
                //持续时间
                animation.setDuration(300);
                //底栏滑动白线开始动画
                //bottomLine.startAnimation(animation);
            }
        }

        @Override
        public void onPageScrolled(int i, float v, int i2) {

        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    }

    //双向链表
    private List<Runnable> runnables = new LinkedList<Runnable>();
    public void addOnFocusChangeOperation(Runnable runnable) {
        synchronized (runnables) {
            runnables.add(runnable);
        }
    }
    TopMenubar topMenubar = null;
    public void setTopMenubar(TopMenubar topMenubar){
        this.topMenubar = topMenubar;
    }
    public TopMenubar getTopMenubar(){
        return this.topMenubar;
    }
}