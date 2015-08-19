package com.innoxyz.ui.interfaces;

/**
 * Created by yj on 2014/11/5.
 * 给要刷新的控件设置监听器才可使用;
 * 刷新/加载完毕后要隐藏刷新/加载头;
 */
public interface OnRefreshListener {
    /**
     * 下拉刷新
     * 在完成刷新逻辑后要隐藏header： refreshListView.hideHeaderView();
     */
    void onDownPullRefresh();

    /**
     * 上拉加载更多
     * 在完成加载逻辑后要隐藏footer： refreshListView.hideFooterView();
     */
    void onLoadingMore();
}
