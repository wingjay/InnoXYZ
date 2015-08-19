package com.innoxyz.ui.activities;

import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;


import com.innoxyz.ui.commands.ActivityCommand;
import com.innoxyz.ui.fragments.common.BaseFragment;
import com.innoxyz.ui.fragments.common.ReplyAction;
import com.innoxyz.R;
import com.innoxyz.util.Util;
import com.readystatesoftware.systembartint.SystemBarTintManager;

//新建回复等
public class NewActivity extends FragmentActivity {

    //控制顶部导航栏颜色
    SystemBarTintManager tintManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //该Activity本身为空白，需填入fragment
        setContentView(R.layout.activity_detail);


        //为顶部状态栏设置颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Util.setTranslucentStatus(NewActivity.this, true);
        }
        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.actionbar_blue);


        ActionBar actionBar = getActionBar();
        //actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.background_black));
        //actionbar不用返回，后面会自动返回
        actionBar.setDisplayHomeAsUpEnabled(false);

        //获取传递过来的Intent里的打包数据
        Bundle bundle = getIntent().getExtras();
        //目标类名
        Class<? extends BaseFragment> toFragmentClass = (Class<? extends BaseFragment>)bundle.getSerializable(ActivityCommand.defaultFragment);

        if(toFragmentClass != null){
            try{
                //实例化目标fragment并添加到framelayout中
                //利用多态性来接收这个 baseFragment
                BaseFragment baseFragment = toFragmentClass.newInstance();
                baseFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.frame_content, baseFragment, "CURRENT_FRAGMENT") //给要添加的fragment设置Tag
                        .commit();
            }
            catch (Exception e){

            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //渲染菜单项
        getMenuInflater().inflate(R.menu.new_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //点击右上角完成按钮
        if (id == R.id.action_done) {
            //获取当前显示的回复Fragment，当点击完成按钮时，执行各自fragment里的 reply 处理回复
            ReplyAction replyAction = (ReplyAction)getSupportFragmentManager().findFragmentByTag("CURRENT_FRAGMENT");
            if(replyAction != null){
                replyAction.reply();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
