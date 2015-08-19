package com.innoxyz.ui.customviews;

import android.content.Context;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.innoxyz.R;
import com.innoxyz.data.runtime.model.Pager;
import com.innoxyz.ui.interfaces.OnRefreshListener;

import java.text.SimpleDateFormat;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class RefreshListView extends ListView implements AbsListView.OnScrollListener ,View.OnClickListener{

    private Context mContext;
    private static final String TAG = "RefreshListView";
    private int firstVisibleItemPosition; // 屏幕显示在第一个的item的索引
    private int downY; // 按下时y轴的偏移量
    private int headerViewHeight; // 头布局的高度
    private View headerView; // 头布局的对象

    private final int DOWN_PULL_REFRESH = 0; // 下拉刷新状态
    private final int RELEASE_REFRESH = 1; // 松开刷新
    private final int REFRESHING = 2; // 正在刷新中
    private final int HIDE_REFRESH_HEADER = 3; // 完全隐藏刷新头状态
    public int currentState = DOWN_PULL_REFRESH; // 头布局的状态: 默认为下拉刷新状态

    private Animation upAnimation; // 向上旋转的动画
    private Animation downAnimation; // 向下旋转的动画

    private ImageView ivArrow; // 头布局的剪头
    private ProgressBar mProgressBar; // 头布局的进度条
    private TextView tvState; // 头布局的状态
    private TextView tvLastUpdateTime; // 头布局的最后更新时间
    int maxTopPadding = 40;// 头布局的最大padding

    private OnRefreshListener mOnRefershListener;
    private boolean isScrollToBottom; // 是否滑动到底部
    private View footerView; // 脚布局的对象
    private int footerViewHeight; // 脚布局的高度
    private boolean isLoadingMore = false; // 是否正在加载更多中

    public boolean isHeaderHiden = true;//判断头布局是否完全隐藏，响应click事件
    //刷新提示
    /**
     * hasMore total共同管理
     * 1、设置tip内容：重新刷新、没有数据、上拉以加载更多
     * 2、下滑到底部时自动判断是否还有更多来决定是否加载
     */
    public boolean hasMore = false;//根据此来设置Tip的文本内容
    private int total = 1; //是否有数据，以区分数据加载失败和确实没有数据两种情况.默认为有数据
    private View tipView;
    private int tipViewHeight;
    private int tipImageHeight;
    private ImageView refresh_tip_imageView;
    private TextView refresh_tip_TextView;

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        initHeaderView();
        initTipView();
        initFooterView();
        this.setOnScrollListener(this);
    }
    /**
     * 初始化头布局
     */
    private void initHeaderView() {
        headerView = View.inflate(getContext(), R.layout.listview_header, null);
        ivArrow = (ImageView) headerView.findViewById(R.id.iv_listview_header_arrow);
        mProgressBar = (ProgressBar) headerView.findViewById(R.id.pb_listview_header);
        tvState = (TextView) headerView.findViewById(R.id.tv_listview_header_state);
        tvLastUpdateTime = (TextView) headerView.findViewById(R.id.tv_listview_header_last_update_time);

        // 设置最后刷新时间
        tvLastUpdateTime.setText("最后刷新时间: " + getLastUpdateTime());
        //下面两句一定要加上，不然android4.3无法measure测量长度
        LayoutParams params1 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        headerView.setLayoutParams(params1);
        headerView.measure(0, 0); // 系统会帮我们测量出headerView的大小

        headerViewHeight = headerView.getMeasuredHeight();
        headerView.setPadding(0, -headerViewHeight, 0, 0);

        this.addHeaderView(headerView); // 向ListView的顶部添加一个view对象
        initAnimation();
    }

    /**
     * 用来显示刷新提示：无内容/点击刷新。默认隐藏
     */
    private void initTipView() {
        tipView = View.inflate(getContext(), R.layout.customviews_refresh_tip, null);
        //隐藏tipView
        //下面两句一定要加上，不然android4.3无法measure测量长度
        LayoutParams params1 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tipView.setLayoutParams(params1);
        tipView.measure(0, 0);

        tipViewHeight = tipView.getMeasuredHeight();
        tipView.setPadding(0, -tipViewHeight, 0, 0);
        //提示内容
        refresh_tip_imageView = (ImageView)tipView.findViewById(R.id.refresh_tip_imageView);
        refresh_tip_TextView = (TextView)tipView.findViewById(R.id.refresh_tip_TextView);
        tipImageHeight = refresh_tip_imageView.getMeasuredHeight();
        //点击刷新
        tipView.setOnClickListener(this);
        this.addFooterView(tipView);
    }
    /**
     * 初始化脚布局
     */
    private void initFooterView() {
        footerView = View.inflate(getContext(), R.layout.listview_footer, null);
        //下面两句一定要加上，不然android4.3无法measure测量长度
        LayoutParams params1 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        footerView.setLayoutParams(params1);
        footerView.measure(0, 0);

        footerViewHeight = footerView.getMeasuredHeight();
        footerView.setPadding(0, -footerViewHeight, 0, 0);
        this.addFooterView(footerView);
    }


    /**
     * 获得系统的最新时间
     *
     * @return
     */
    private String getLastUpdateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(System.currentTimeMillis());
    }

    /**
     * 初始化动画
     */
    private void initAnimation() {
        upAnimation = new RotateAnimation(0f, -180f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        upAnimation.setDuration(500);
        upAnimation.setFillAfter(true); // 动画结束后, 停留在结束的位置上

        downAnimation = new RotateAnimation(-180f, -360f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        downAnimation.setDuration(500);
        downAnimation.setFillAfter(true); // 动画结束后, 停留在结束的位置上
    }
    //格外注意！！所触摸的控件如果绑定了Click事件，那么下面的ACTION_DOWN就会被拦截
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN :
                downY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE :
                //判断头布局是否完全隐藏
                if (headerView.getPaddingTop() > -headerViewHeight){
                    isHeaderHiden = false;
                }else{
                    isHeaderHiden = true;
                }
                int moveY = (int) ev.getY();
                // 移动中的y - 按下的y = 间距.
                int diff = (moveY - downY) / 2;
                // -头布局的高度 + 间距 = paddingTop
                int paddingTop = -headerViewHeight + diff;
                // 如果: -头布局的高度 > paddingTop的值 执行super.onTouchEvent(ev);
                if (firstVisibleItemPosition == 0
                        && -headerViewHeight < paddingTop) {
                    if (paddingTop > 0 && currentState == DOWN_PULL_REFRESH) { // 完全显示了.
                        Log.i(TAG, "松开刷新");
                        currentState = RELEASE_REFRESH;
                        refreshHeaderView();
                    } else if (paddingTop < 0
                            && currentState == RELEASE_REFRESH) { // 没有显示完全
                        Log.i(TAG, "下拉刷新");
                        currentState = DOWN_PULL_REFRESH;
                        refreshHeaderView();
                    }
                    // 下拉头布局
                    headerView.setPadding(0, (paddingTop > maxTopPadding) ? maxTopPadding : paddingTop, 0, 0);
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP :
                // 判断当前的状态是松开刷新还是下拉刷新
                if (currentState == RELEASE_REFRESH) {
                    Log.i(TAG, "刷新数据.");
                    // 把头布局设置为完全显示状态
                    headerView.setPadding(0, 0, 0, 0);
                    // 进入到正在刷新中状态
                    currentState = REFRESHING;
                    refreshHeaderView();
                    if (mOnRefershListener != null) {
                        mOnRefershListener.onDownPullRefresh(); // 调用使用者的监听方法
                    }
                } else if (currentState == DOWN_PULL_REFRESH) {
                    // 隐藏头布局
                    headerView.setPadding(0, -headerViewHeight, 0, 0);
                }
                break;
            default :
                break;
        }
        return super.onTouchEvent(ev);
    }


    /**
     * 根据currentState刷新头布局的状态
     */
    private void refreshHeaderView() {
        switch (currentState) {
            case DOWN_PULL_REFRESH : // 下拉刷新状态
                tvState.setText("下拉刷新");
                ivArrow.startAnimation(downAnimation); // 执行向下旋转
                break;
            case RELEASE_REFRESH : // 松开刷新状态
                tvState.setText("松开刷新");
                ivArrow.startAnimation(upAnimation); // 执行向上旋转
                break;
            case REFRESHING : // 正在刷新中状态
                ivArrow.clearAnimation();
                ivArrow.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                tvState.setText("正在刷新中...");
                break;
            default :
                break;
        }
    }

    /**
     * 当滚动状态改变时回调
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        if (scrollState == SCROLL_STATE_IDLE
                || scrollState == SCROLL_STATE_FLING) {
            // 进入加载有三个条件：1、滑动到底部；2、不处于正在加载中；3、还有更多可加载
            if (isScrollToBottom && !isLoadingMore && hasMore) {
                isLoadingMore = true;
                // 当前到底部
                Log.i(TAG, "加载更多数据");
                footerView.setPadding(0, 0, 0, 0);
                //设置显示位置
                this.setSelection(this.getCount());
                if (mOnRefershListener != null) {
                    mOnRefershListener.onLoadingMore();
                }
            }
        }
    }

    /**
     * 当滚动时调用
     *
     * @param firstVisibleItem
     *            当前屏幕显示在顶部的item的position
     * @param visibleItemCount
     *            当前屏幕显示了多少个条目的总数
     * @param totalItemCount
     *            ListView的总条目的总数
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        firstVisibleItemPosition = firstVisibleItem;

        if (getLastVisiblePosition() == (totalItemCount - 1)) {
            isScrollToBottom = true;
        } else {
            isScrollToBottom = false;
        }
    }

    /**
     * 设置刷新监听事件
     *
     * @param listener
     */
    public void setOnRefreshListener(OnRefreshListener listener) {
        mOnRefershListener = listener;
    }

    /**
     * 核心！！
     * 把分页的pager对象传递给listView，从而获取hasMore和total数据
     * 如果total=0，则显示没有更多信息
     * 如果total!=0，hasMore=false，则已经全部显示，隐藏tip
     * 如果total!=0，hasMore=true，则显示还有更多提示
     * 而且，当下滑到底部时，会自动判断hasMore，若为false，则不加载
     */
    public void setDataPager(Pager pager) {
        if (pager == null) {
            hasMore = false;
            total = 0;
        }else {
            hasMore = pager.hasMore();
            total = pager.getTotal();
        }
        autoSetTip();
    }

    /**
     * 是否处于刷新中。在使用中，如果是刷新，则在获取数据时要清空原有数据，并把新获取的数据存入。
     * 如果不是刷新，则不用清空。
     */
    public boolean isRefreshing() {
        return currentState == REFRESHING;
    }

    /**
     * 当获取数据结束后，隐藏刷新头和加载头
     * 同时无论是否刷新或加载，都要自动设置tip
     */
    public void hideHeaderFooter() {
        if (currentState == REFRESHING) {
            hideHeaderView();
        }else if (isLoadingMore) {
            hideFooterView();
        }
        autoSetTip();
    }


    /**
     * 进入刷新中
     */
    public void startRefresh() {
        headerView.setPadding(0, 10, 0, 0);
        ivArrow.clearAnimation();
        ivArrow.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        tvState.setText("正在刷新中...");
        currentState = REFRESHING;
        //设置header显示
        isHeaderHiden = false;
    }
    /**
     * 进入刷新中但是仅仅设置标志位，而不显示刷新头
     */
    public void startRefreshWithoutHeader() {
        currentState = REFRESHING;
        //设置header显示
        isHeaderHiden = false;
    }

    /**
     * 检查header是否完全隐藏
     */
    public boolean isHeaderHidden() {
        return isHeaderHiden;
    }
    /**
     * 隐藏头布局，设置刷新提示
     */
    public void hideHeaderView() {
        headerView.setPadding(0, -headerViewHeight, 0, 0);
        //设置header隐藏
        isHeaderHiden = true;
        ivArrow.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        tvState.setText("下拉刷新");
        tvLastUpdateTime.setText("最后刷新时间: " + getLastUpdateTime());
        currentState = DOWN_PULL_REFRESH;
        //自动设置提示内容
        autoSetTip();
    }

    /**
     * 隐藏脚布局
     */
    public void hideFooterView() {
        //如果有内容，而且还可以加载，则显示“上拉可继续加载”
        if (!hasMore) {
            hideTip();
        }else {
            showLoadTip();
        }
        footerView.setPadding(0, -footerViewHeight, 0, 0);
        isLoadingMore = false;
    }

    //自动根据hasmore和total设置tip
    public void autoSetTip() {
        //先判断是否为空
        if (isListViewEmpty()) {
            //若为空，是否确实没有数据
            if (total == 0) {
                showNoDataTip();
            }else {
                showRefreshTip();
            }
            return;
        }
        //如果有内容，而且还可以加载，则显示“上拉可继续加载”
        if (hasMore) {
            showLoadTip();
        }else {
            hideTip();
        }
    }

    //判断listView是否有内容（除去header footer）
    private boolean isListViewEmpty() {
        int headerViewCount = getHeaderViewsCount();
        int footerViewCount = getFooterViewsCount();
        ListAdapter adapter = getAdapter();
        int itemCount = adapter.getCount();
        return (itemCount == (headerViewCount + footerViewCount));
    }

    //隐藏tip
    private void hideTip() {
        //没有更多了，完全隐藏
        tipView.setPadding(0, -tipViewHeight, 0, 0);
    }

    //显示没有数据tip
    private void showNoDataTip() {
        refresh_tip_imageView.setVisibility(GONE);
        refresh_tip_TextView.setText(R.string.no_data_tip);
        tipView.setPadding(0, 0, 0, 0);
    }
    //显示点击刷新tip
    private void showRefreshTip() {
        refresh_tip_imageView.setVisibility(VISIBLE);
        refresh_tip_TextView.setText(R.string.click_to_refresh);
        tipView.setPadding(0, 0, 0, 0);
    }

    //显示点击加载更多tip
    private void showLoadTip() {
        refresh_tip_imageView.setVisibility(GONE);
        refresh_tip_TextView.setText(R.string.scrollup_to_load);
        tipView.setPadding(0, 0, 0, 0);
    }


    /**
     * 点击刷新提示，执行刷新
     * @param v
     */
    @Override
    public void onClick(View v) {
        //隐藏tipView
        hideTip();
        // 把头布局设置为完全显示状态
        headerView.setPadding(0, 0, 0, 0);
        //设置header显示
        isHeaderHiden = false;
        // 进入到正在刷新中状态
        currentState = REFRESHING;
        refreshHeaderView();
        if (mOnRefershListener != null) {
            mOnRefershListener.onDownPullRefresh(); // 调用使用者的监听方法
        }
    }

}