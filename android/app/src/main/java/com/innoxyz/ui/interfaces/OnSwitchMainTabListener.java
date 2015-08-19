package com.innoxyz.ui.interfaces;

import com.innoxyz.ui.fragments.common.BaseFragment;

/**
 * Created by yj on 2014/12/23.
 * 给MainActivity实现这个接口，方便内部Fragment自由切换其他Tab
 */
public interface OnSwitchMainTabListener {
    public void switchFragment(int toFragmentId);
}
