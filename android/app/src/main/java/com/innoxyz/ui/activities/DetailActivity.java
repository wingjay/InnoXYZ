package com.innoxyz.ui.activities;

import android.app.ActionBar;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.innoxyz.R;

import com.innoxyz.ui.commands.ActivityCommand;
import com.innoxyz.ui.fragments.common.BaseFragment;
import com.innoxyz.util.Util;
import com.readystatesoftware.systembartint.SystemBarTintManager;

//通知详情页面，根据通知类型来选取fragment显示，并把bundle数据传递进去
public class DetailActivity extends FragmentActivity {

    //控制顶部导航栏颜色
    SystemBarTintManager tintManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //为顶部状态栏设置颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Util.setTranslucentStatus(DetailActivity.this, true);
        }
        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.actionbar_blue);


        //程序顶部的bar
        ActionBar actionBar = getActionBar();
        //actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.background_black));
        //向上导航，点击该 actionBar 会回到上一个Activity，出现一个返回箭头
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //获取传递过来的Intent中的bundle数据
        Bundle bundle = getIntent().getExtras();
        //todo：？ 获取到传递过来的目标fragment类名
        Class<? extends BaseFragment> toFragmentClass = (Class<? extends BaseFragment>)bundle.getSerializable(ActivityCommand.defaultFragment);

        if(toFragmentClass != null){
            try{
                BaseFragment baseFragment = toFragmentClass.newInstance();
                baseFragment.setArguments(bundle);
                //将目标fragment放入到该 DetailActivity 中
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.frame_content, baseFragment)
                        .commit();
            }catch (Exception e){

            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    //在 AndroidManifest 中设置 parentActivityName，当返回时如果不是点击右上角的 action_settings 就会返回到父Activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
