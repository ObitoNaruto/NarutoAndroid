/**
 *
 *	created by Mr.Simple, Sep 30, 20142:48:17 PM.
 *	Copyright (c) 2014, hehonghui@umeng.com All Rights Reserved.
 *
 *                #####################################################
 *                #                                                   #
 *                #                       _oo0oo_                     #   
 *                #                      o8888888o                    #
 *                #                      88" . "88                    #
 *                #                      (| -_- |)                    #
 *                #                      0\  =  /0                    #   
 *                #                    ___/`---'\___                  #
 *                #                  .' \\|     |# '.                 #
 *                #                 / \\|||  :  |||# \                #
 *                #                / _||||| -:- |||||- \              #
 *                #               |   | \\\  -  #/ |   |              #
 *                #               | \_|  ''\---/''  |_/ |             #
 *                #               \  .-\__  '-'  ___/-. /             #
 *                #             ___'. .'  /--.--\  `. .'___           #
 *                #          ."" '<  `.___\_<|>_/___.' >' "".         #
 *                #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 *                #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 *                #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 *                #                       `=---='                     #
 *                #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 *                #                                                   #
 *                #               佛祖保佑         永无BUG              #
 *                #                                                   #
 *                #####################################################
 */

package com.naruto.mobile.pullrefresh.scroller;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.naruto.mobile.R;
import com.naruto.mobile.pullrefresh.listener.OnLoadListener;
import com.naruto.mobile.pullrefresh.listener.OnRefreshListener;

/**
 * RefreshLayoutBase是下拉刷新、上拉加载更多的抽象类。该组件含有Header、Content
 * View、Footer三部分，并且从上到下布局。Header、Footer一般大小固定，Content
 * View则为与屏幕一样大。在初始化时通过Scroller将Header滚出屏幕的可见区域
 * ,当下拉时将Header慢慢移入用户的视野,用户抬起手指时判断Header的可见高度
 * ，如果可见的高度大于Header总高度的一半，那么则触发下拉刷新操作。加载更多则是当Content
 * View到了底部，用户还继续上拉，那么触发加载更多操作。
 * 
 * @author mrsimple
 * @param <T> Content View的类型,可以为任何View的子类,例如ListView、GridView等
 */
public abstract class RefreshLayoutBase<T extends View> extends ViewGroup implements
        OnScrollListener {
    public static final String TAG = "RefreshLayoutBase";
    /**
     * 
     */
    protected Scroller mScroller;

    /**
     * 下拉刷新时显示的header view
     */
    protected View mHeaderView;

    /**
     * 上拉加载更多时显示的footer view
     */
    protected View mFooterView;

    /**
     * 本次触摸滑动y坐标上的偏移量
     */
    protected int mYOffset;

    /**
     * 内容视图, 即用户触摸导致下拉刷新、上拉加载的主视图. 比如ListView, GridView等.
     */
    protected T mContentView;

    /**
     * 最初的滚动位置.第一次布局时滚动header的高度的距离
     */
    protected int mInitScrollY = 0;
    /**
     * 最后一次触摸事件的y轴坐标
     */
    protected int mLastY = 0;

    /**
     * 空闲状态
     */
    public static final int STATUS_IDLE = 0;

    /**
     * 下拉或者上拉状态, 还没有到达可刷新的状态
     */
    public static final int STATUS_PULL_TO_REFRESH = 1;

    /**
     * 下拉或者上拉状态(达到可刷新状态，但手指还没松开)
     */
    public static final int STATUS_RELEASE_TO_REFRESH = 2;
    /**
     * 刷新中
     */
    public static final int STATUS_REFRESHING = 3;

    /**
     * LOADING中
     */
    public static final int STATUS_LOADING = 4;

    /**
     * 当前状态
     */
    protected int mCurrentStatus = STATUS_IDLE;

    /**
     * header中的箭头图标
     */
    private ImageView mArrowImageView;
    /**
     * 箭头是否向上
     */
    private boolean isArrowUp;
    /**
     * header 中的文本标签
     */
    private TextView mTipsTextView;
    /**
     * header中的时间标签
     */
    private TextView mTimeTextView;
    /**
     * header中的进度条
     */
    private ProgressBar mProgressBar;
    /**
     * 屏幕高度
     */
    private int mScreenHeight;
    /**
     * Header 高度
     */
    private int mHeaderHeight;
    /**
     * 下拉刷新监听器
     */
    protected OnRefreshListener mOnRefreshListener;
    /**
     * 加载更多回调
     */
    protected OnLoadListener mLoadListener;

    /**
     * @param context
     */
    public RefreshLayoutBase(Context context) {
        this(context, null);
    }

    /**
     * @param context
     * @param attrs
     */
    public RefreshLayoutBase(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public RefreshLayoutBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);

        // 初始化Scroller对象
        mScroller = new Scroller(context);

        // 获取屏幕高度
        mScreenHeight = context.getResources().getDisplayMetrics().heightPixels;
        // header 的高度为屏幕高度的 1/4
        mHeaderHeight = mScreenHeight / 4;

        // 初始化整个布局
        initLayout(context);
    }

    /**
     * 初始化整个布局,从上到下分别为header,内容视图和footer
     * 
     * @param context
     */
    private final void initLayout(Context context) {
        // header view
        setupHeaderView(context);
        // 设置内容视图
        setupContentView(context);
        // 设置布局参数
        setDefaultContentLayoutParams();
        // 添加mContentView
        addView(mContentView);
        // footer view
        setupFooterView(context);

    }

    /**
     * 初始化 header view
     */
    protected void setupHeaderView(Context context) {
        mHeaderView = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header, this, false);
        mHeaderView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, mHeaderHeight));//高度为屏幕的1/4
        mHeaderView.setBackgroundColor(Color.GREEN);//背景色
        //header高度为1/4屏幕高度，但是，它只有200px是有效的显示区域
        Log.d("RefreshLayoutBase", "HeaderHeight:" + mHeaderHeight);
        mHeaderView.setPadding(0, mHeaderHeight - 200, 0, 0);
        addView(mHeaderView);//添加到布局中

        // HEADER VIEWS
        mArrowImageView = (ImageView) mHeaderView.findViewById(R.id.pull_to_arrow_image);
        mTipsTextView = (TextView) mHeaderView.findViewById(R.id.pull_to_refresh_text);
        mTimeTextView = (TextView) mHeaderView.findViewById(R.id.pull_to_refresh_updated_at);
        mProgressBar = (ProgressBar) mHeaderView.findViewById(R.id.pull_to_refresh_progress);
    }

    /*
     * 丈量视图的宽、高。宽度为用户设置的宽度，高度则为header, content view, footer这三个子控件的高度之和
     * @see android.view.View#onMeasure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);//获取用户设置的宽度
        int childCount = getChildCount();//子视图的个数
        int finalHeight = 0;//最终的高度
        for (int i = 0; i < childCount; i++) {//遍历
            View child = getChildAt(i);
            // measure
            measureChild(child, widthMeasureSpec, heightMeasureSpec);//测量
            // 该view所需要的总高度
            finalHeight += child.getMeasuredHeight();//累加
        }
        //设置该下拉刷新组件的尺寸
        setMeasuredDimension(width, finalHeight);//设置宽高
    }

    /*
     * 布局函数，将header, content view,
     * footer这三个view从上到下布局。布局完成后通过Scroller滚动到header的底部，即滚动距离为header的高度 +
     * 本视图的paddingTop，从而达到隐藏header的效果.
     * @see android.view.ViewGroup#onLayout(boolean, int, int, int, int)
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int childCount = getChildCount();//子视图的个数
        int top = getPaddingTop();//
        Log.d(TAG, "onLayout called! paddingTop:" + top);
        for (int i = 0; i < childCount; i++) {//遍历
            View child = getChildAt(i);
            child.layout(0, top, child.getMeasuredWidth(), child.getMeasuredHeight() + top);//为子视图设置布局
            top += child.getMeasuredHeight();//下一个子视图的top
        }

        // 计算初始化滑动的y轴距离
        mInitScrollY = mHeaderView.getMeasuredHeight() + getPaddingTop();//等于头view高度+paddingTop，初始化后滑动这个距离后，正好隐藏掉头view
        Log.d(TAG, "onLayout called! InitScrollY:" + mInitScrollY);
        // 滑动到header view高度的位置, 从而达到隐藏header view的效果
        scrollTo(0, mInitScrollY);
    }

    /**
     * 初始化Content View, 子类覆写.
     */
    protected abstract void setupContentView(Context context);

    /**
     * 是否已经到了最顶部,子类需覆写该方法,使得mContentView滑动到最顶端时返回true, 如果到达最顶端用户继续下拉则拦截事件;
     * 
     * @return
     */
    protected abstract boolean isTop();

    /**
     * 是否已经到了最底部,子类需覆写该方法,使得mContentView滑动到最底端时返回true;从而触发自动加载更多的操作
     * 
     * @return
     */
    protected abstract boolean isBottom();

    /**
     * 初始化footer view
     */
    protected void setupFooterView(Context context) {
        mFooterView = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_footer, this, false);
        addView(mFooterView);
    }

    /**
     * 设置Content View的默认布局参数
     */
    protected void setDefaultContentLayoutParams() {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mContentView.setLayoutParams(params);
    }

    /**
     * 与Scroller合作,实现平滑滚动。在该方法中调用Scroller的computeScrollOffset来判断滚动是否结束。如果没有结束，
     * 那么滚动到相应的位置，并且调用postInvalidate方法重绘界面，从而再次进入到这个computeScroll流程，直到滚动结束。
     */
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            Log.d(TAG, "computeScroll called! currX:" + mScroller.getCurrX() + ", currY:" + mScroller.getCurrY());
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    /*
     * 在适当的时候拦截触摸事件，这里指的适当的时候是当mContentView滑动到顶部，并且是下拉时拦截触摸事件，否则不拦截，交给其child
     * view 来处理。
     * @see
     * android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onTouchEvent will be called and we do the actual
         * scrolling there.
         */
        final int action = MotionEventCompat.getActionMasked(ev);//获取触摸事件的类型
        // Always handle the case of the touch gesture being complete.
        //取消事件和抬起事件则直接返回false
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            // Do not intercept touch event, let the child handle it
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = (int) ev.getRawY();//最后一次触摸事件的y轴坐标
                break;

            case MotionEvent.ACTION_MOVE:
                // int yDistance = (int) ev.getRawY() - mYDown;
                mYOffset = (int) ev.getRawY() - mLastY;
                // 如果拉到了顶部, 并且是下拉,则拦截触摸事件,从而转到onTouchEvent来处理下拉刷新事件
                if (isTop() && mYOffset > 0) {
                    Log.d(TAG, "onInterceptTouchEvent called! current view intercept. ");
                    return true;
                }
                break;

        }
        // Do not intercept touch event, let the child handle it
        return false;//默认不拦截触摸事件，使得该控件的子视能够得到处理机会
    }

    /*
     * 在这里处理触摸事件以达到下拉刷新
     * @see android.view.View#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(VIEW_LOG_TAG, "@@@ onTouchEvent : action = " + event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                //获取手指触摸的当前y坐标
                int currentY = (int) event.getRawY();
                //当前坐标减去按下时的y坐标得到y轴上的偏移量
                mYOffset = currentY - mLastY;
                if (mCurrentStatus != STATUS_LOADING) {//非LOADING中状态
                    //在y轴方向上滚动该控件
                    changeScrollY(mYOffset);
                }
                //旋转Header中的箭头图标
                rotateHeaderArrow();
                //修改Header中的文本信息
                changeTips();
                //将mLastY设置为这次的y轴坐标
                mLastY = currentY;
                break;

            case MotionEvent.ACTION_UP:
                // 下拉刷新的具体操作
                doRefresh();
                break;
            default:
                break;
        }
        return true;//返回true，消费该事件，不再传递
    }

    /**
     * 设置滚动的参数
     * 
     * @param yOffset
     */
    private void startScroll(int yOffset) {
        mScroller.startScroll(getScrollX(), getScrollY(), 0, yOffset);
        invalidate();
    }

    /**
     * 修改y轴上的滚动值，从而实现header被下拉的效果
     * @param distance 这次触摸事件的y轴和上一次的y轴的差值
     * @return
     */
    protected void changeScrollY(int distance) {
        // 最大值为 scrollY(header 隐藏), 最小值为0 ( header 完全显示).
        int curY = getScrollY();
        Log.d(TAG, "distance:" + distance);
        Log.d(TAG, "getScrollY:" + getScrollY());
        // 下拉
        if (distance > 0 && curY - distance > getPaddingTop()) {
            Log.d(TAG, "上拉");
            Log.d(TAG, "getPaddingTop:" + getPaddingTop());
            Log.d(TAG, "distance>0:" + (distance > 0) + "curY - distance=" + (curY - distance));
            scrollBy(0, -distance);
        } else if (distance < 0 && curY - distance <= mInitScrollY) {
            Log.d(TAG, "下拉");
            Log.d(TAG, "mInitScrollY:" + mInitScrollY);
            Log.d(TAG, "distance<0:" + (distance < 0) + "curY - distance=" + (curY - distance));
            // 上拉过程
            scrollBy(0, -distance);
        }

        curY = getScrollY();
        int slop = mInitScrollY / 2;
        //
        if (curY > 0 && curY < slop) {
            mCurrentStatus = STATUS_RELEASE_TO_REFRESH;//下拉或者上拉状(释放可刷新)
        } else if (curY > 0 && curY > slop) {
            mCurrentStatus = STATUS_PULL_TO_REFRESH;//下拉或者上拉状态, 还没有到达可刷新的状态(释放不可刷新)
        }
    }

    /**
     * 旋转箭头图标
     */
    protected void rotateHeaderArrow() {

        if (mCurrentStatus == STATUS_REFRESHING) {
            return;
        } else if (mCurrentStatus == STATUS_PULL_TO_REFRESH && !isArrowUp) {//箭头向下
            return;
        } else if (mCurrentStatus == STATUS_RELEASE_TO_REFRESH && isArrowUp) {//箭头向上
            return;
        }

        mProgressBar.setVisibility(View.GONE);
        mArrowImageView.setVisibility(View.VISIBLE);
        float pivotX = mArrowImageView.getWidth() / 2f;
        float pivotY = mArrowImageView.getHeight() / 2f;
        float fromDegrees = 0f;
        float toDegrees = 0f;
        if (mCurrentStatus == STATUS_PULL_TO_REFRESH) {//箭头向下状态
            fromDegrees = 180f;
            toDegrees = 360f;
        } else if (mCurrentStatus == STATUS_RELEASE_TO_REFRESH) {//箭头向上
            fromDegrees = 0f;
            toDegrees = 180f;
        }

        RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees, pivotX, pivotY);
        animation.setDuration(100);
        animation.setFillAfter(true);
        mArrowImageView.startAnimation(animation);

        if (mCurrentStatus == STATUS_RELEASE_TO_REFRESH) {
            isArrowUp = true;
        } else {
            isArrowUp = false;
        }
    }

    /**
     * 根据当前状态修改header view中的文本标签
     */
    protected void changeTips() {
        if (mCurrentStatus == STATUS_PULL_TO_REFRESH) {
            mTipsTextView.setText(R.string.pull_to_refresh_pull_label);
        } else if (mCurrentStatus == STATUS_RELEASE_TO_REFRESH) {
            mTipsTextView.setText(R.string.pull_to_refresh_release_label);
        }
    }

    /**
     * 设置下拉刷新监听器
     * 
     * @param listener
     */
    public void setOnRefreshListener(OnRefreshListener listener) {
        mOnRefreshListener = listener;
    }

    /**
     * 设置滑动到底部时自动加载更多的监听器
     * 
     * @param listener
     */
    public void setOnLoadListener(OnLoadListener listener) {
        mLoadListener = listener;
    }

    /**
     * 刷新结束，恢复状态
     */
    public void refreshComplete() {
        //隐藏HeaderView
        mScroller.startScroll(getScrollX(), getScrollY(), 0, mInitScrollY - getScrollY());
        mCurrentStatus = STATUS_IDLE;
        invalidate();
        updateHeaderTimeStamp();

        // 200毫秒后处理arrow和progressbar,免得太突兀
        this.postDelayed(new Runnable() {

            @Override
            public void run() {
                mArrowImageView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }
        }, 100);

    }

    /**
     * 加载结束，恢复状态
     */
    public void loadCompelte() {
        // 隐藏footer
        startScroll(mInitScrollY - getScrollY());
        mCurrentStatus = STATUS_IDLE;
    }

    /**
     * 手指抬起时,根据用户下拉的高度来判断是否是有效的下拉刷新操作。如果下拉的距离超过header view有效区域高度的
     * 1/2那么则认为是有效的下拉刷新操作，否则恢复原来的视图状态.
     */
    private void changeHeaderViewStaus() {
        //getScrollX = 手机屏幕显示区域左上角x坐标减去MultiViewGroup视图左上角x坐标
        //getScrollY = 手机屏幕显示区域左上角y坐标减去MultiViewGroup视图左上角y坐标=0(因为子视图的高度和手机屏幕高度一样)
        // 最大值为 scrollY(header 隐藏), 最小值为0 ( header 完全显示).
        int curScrollY = getScrollY();
        Log.d("changeHeaderViewStaus", "curScrollY=" + curScrollY);
        Log.d("changeHeaderViewStaus", "mInitScrollY / 2=" + mInitScrollY / 2);
        // 超过1/2则认为是有效的下拉刷新, 否则还原
        if (curScrollY < mInitScrollY / 2) {//
            //滚动到能够正常显示header的位置
            mScroller.startScroll(getScrollX(), curScrollY, 0, mHeaderView.getPaddingTop() - curScrollY);
            mCurrentStatus = STATUS_REFRESHING;
            mTipsTextView.setText(R.string.pull_to_refresh_refreshing_label);
            mArrowImageView.clearAnimation();
            mArrowImageView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mScroller.startScroll(getScrollX(), curScrollY, 0, mInitScrollY - curScrollY);
            mCurrentStatus = STATUS_IDLE;
        }

        invalidate();
    }

    /**
     * 执行下拉刷新
     */
    protected void doRefresh() {
        changeHeaderViewStaus();
        // 执行刷新操作
        if (mCurrentStatus == STATUS_REFRESHING && mOnRefreshListener != null) {
            mOnRefreshListener.onRefresh();//回调
        }
    }

    /**
     * 修改header上的最近更新时间
     */
    private void updateHeaderTimeStamp() {
        // 设置更新时间
        mTimeTextView.setText(R.string.pull_to_refresh_update_time_label);
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getInstance();
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
        mTimeTextView.append(sdf.format(new Date()));
    }

    /**
     * 返回Content View
     * 
     * @return
     */
    public T getContentView() {
        return mContentView;
    }

    /**
     * @return
     */
    public View getHeaderView() {
        return mHeaderView;
    }

    /**
     * @return
     */
    public View getFooterView() {
        return mFooterView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    /*
     * 滚动监听，当滚动到最底部，且用户设置了加载更多的监听器时触发加载更多操作.
     * @see android.widget.AbsListView.OnScrollListener#onScroll(android.widget.
     * AbsListView, int, int, int)
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
        // 用户设置了加载更多监听器，且到了最底部，并且是上拉操作，那么执行加载更多.
        if (mLoadListener != null && isBottom() && mScroller.getCurrY() <= mInitScrollY
                && mYOffset <= 0
                && mCurrentStatus == STATUS_IDLE) {
            showFooterView();
            doLoadMore();
        }
    }

    /**
     * 执行下拉(自动)加载更多的操作
     */
    protected void doLoadMore() {
        if (mLoadListener != null) {
            mLoadListener.onLoadMore();
        }
    }

    /**
     * 显示footer view
     */
    private void showFooterView() {
        startScroll(mFooterView.getMeasuredHeight());
        mCurrentStatus = STATUS_LOADING;
    }

}
