package com.innoxyz.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.innoxyz.ui.fragments.common.BaseFragment;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-11
 * Time: 上午10:13
 * To change this template use File | Settings | File Templates.
 */
//在Activity中切换Fragment，同时传递数据
public class FragmentSwitcher {
    private FragmentActivity activity;
    private BaseFragment fromFragment,toFragment;
    private int layout;
    Bundle bundle;
    FragmentTransaction ft;


    public FragmentSwitcher(FragmentActivity activity, BaseFragment fromFragment, BaseFragment toFragment, int layout, Bundle bundle) {
        this.activity = activity;
        this.fromFragment = fromFragment;
        this.toFragment = toFragment;
        //目标fragment的布局文件
        this.layout = layout;
        //要传递的数据存放在bundle中
        this.bundle = bundle;

        ft = activity.getSupportFragmentManager().beginTransaction();

    }

    public void add() {
        toFragment.setArguments(bundle);
        ft.add(layout, toFragment);
        ft.commit();
    }

    public void replace() {
        toFragment.setArguments(bundle);
        ft.replace(layout, toFragment);
        //原fragment放入返回栈
        ft.addToBackStack(null);
        ft.commit();
    }

    public void removeAttach() {
        toFragment.setArguments(bundle);
        ft.remove(fromFragment);
        ft.attach(toFragment);
        ft.addToBackStack(null);
        ft.commit();
    }


    public void detachAdd() {
        toFragment.setArguments(bundle);
        ft.detach(fromFragment);
        ft.add(layout, toFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void detachAttach() {
        toFragment.setArguments(bundle);
        ft.detach(fromFragment);
        ft.attach(toFragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}
