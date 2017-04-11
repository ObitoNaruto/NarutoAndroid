package com.naruto.mobile.swipeback;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.lang.reflect.Field;

import com.naruto.mobile.R;

/**
 * 原理是在DecorView与LinearLayout之间再加上一层DragLayout，然后使用ViewDragHelper处理这个DragLayout
 */
public class SwipeBackLayout extends FrameLayout {

    private static final int MIN_FLING_VELOCITY = 50;//dp/s

    private static final int MAX_FLING_VELOCITY = 100;//dp/s

    private static final int DEFAULT_SCRIM_COLOR = 0x99000000;

    private static final int FULL_ALPHA = 255;
    /**
     * 滑动的百分比,当达到这个值则finish当前Activity
     */
    private static final float DEFAULT_SCROLL_RATIO = 0.35f;

    public static final int DEFAULT_FLING_EDGE_SIZE = 18;//dp
    /**
     * Default fling distance
     */
    private int mEdgeFlingDistance;

    private Activity mActivity;

    /**
     * 是否支持右滑
     */
    private boolean mEnable = true;

    private View mContentView;

    private ViewDragHelper mViewDragHelper;

    /**
     * 当前滑动的百分比
     */
    private float mScrollPercent;

    private int mContentViewLeftDx;

    private Drawable mShadowLeft;

    private float mScrimFactor;
    /**
     * 是否正在调用onLayout方法
     */
    private boolean isOnLayout;

    private Rect mRect;

    private SwipeLayoutListener mSwipeLayoutListener;

    private boolean isEdgeTouched = false;

    public SwipeBackLayout(Context context) {
        this(context, null);
    }

    public SwipeBackLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeBackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SwipeBackLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        //滑动距离
        mEdgeFlingDistance = ViewConfiguration.get(context).getScaledEdgeSlop();
        mRect = new Rect();
        /**
         * 1.创建实例
         * 第一个参数是当前的ViewGroup，
         * 第二个sensitivity，主要用于设置touchSlop：
         * helper.mTouchSlop = (int) (helper.mTouchSlop * (1 / sensitivity));
         * 可见传入越大，mTouchSlop的值就会越小。
         * 第三个参数就是Callback，在用户的触摸过程中会回调相关方法
         */
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragCallback());//初始化
        //分为滑动左边缘还是右边缘：EDGE_LEFT和EDGE_RIGHT，下面的代码设置了可以处理滑动左边缘
        mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);//滑动处理只处理父view被选中的边缘开始
        setLeftEdgeShadow(ContextCompat.getDrawable(context, R.drawable.swipe_back_shadow));//初始化滑动时左边边缘的阴影
        final float density = getResources().getDisplayMetrics().density;//密度
        //设置最小的有效滑动距离
        final float minVelocity = MIN_FLING_VELOCITY * density;
        mViewDragHelper.setMinVelocity(minVelocity);

        try {
            Field fieldMaxVel = ViewDragHelper.class.getDeclaredField("mMaxVelocity");
            fieldMaxVel.setAccessible(true);
            fieldMaxVel.setInt(mViewDragHelper, (int) (MAX_FLING_VELOCITY * density));

            Field fieldEdgeSize = ViewDragHelper.class.getDeclaredField("mEdgeSize");
            fieldEdgeSize.setAccessible(true);
            fieldEdgeSize.setInt(mViewDragHelper, (int) (DEFAULT_FLING_EDGE_SIZE * density));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void setSwipeLayoutListener(SwipeLayoutListener swipeLayoutListener) {
        this.mSwipeLayoutListener = swipeLayoutListener;
    }

    /**
     * Set up contentView which will be moved by user gesture
     *
     * @param view
     */
    private void setSwipeView(View view) {
        mContentView = view;
    }

    public void setEnableGesture(boolean enable) {
        mEnable = enable;
    }


    private void setLeftEdgeShadow(Drawable drawable) {
        mShadowLeft = drawable == null ? new ColorDrawable(Color.TRANSPARENT) : drawable;
        invalidate();
    }

    /**
     * Scroll out contentView and finish the activity
     */
    public void scrollToFinishActivity() {
        final int childWidth = mContentView.getWidth();
        int left = childWidth + mShadowLeft.getIntrinsicWidth() + mEdgeFlingDistance;
        mViewDragHelper.smoothSlideViewTo(mContentView, left, 0);
        invalidate();
    }

    /**
     * 2.触摸相关方法
     * onInterceptTouchEvent中通过使用mDragger.shouldInterceptTouchEvent(event)来决定我们是否应该拦截当前的事件
     * @param event
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!mEnable) {//不支持右滑不拦截
            return false;
        }
        if (isEdgeTouched) {//触摸边缘拦截滑动事件
            return true;
        }
        final int action = MotionEventCompat.getActionMasked(event);//当前动作
        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mViewDragHelper.cancel();//手指离开屏幕
                return false;
        }
        try {
            //here
            return mViewDragHelper.shouldInterceptTouchEvent(event);//将事件传递给ViewDragHelper处理
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    /**
     * 2.触摸相关方法
     * onTouchEvent中通过mDragger.processTouchEvent(event)处理事件
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mEnable) {//不支持右滑不拦截
            return false;
        }
        final int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isEdgeTouched = false;
                mViewDragHelper.cancel();
                break;
        }
        if (mViewDragHelper != null)
            //here
            mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        isOnLayout = true;//正在调用onLayout方法
        //mContentView在attachToActivity时被设置的
        if (mContentView != null)
            mContentView.layout(mContentViewLeftDx, 0,
                    mContentViewLeftDx + mContentView.getMeasuredWidth(),
                    mContentView.getMeasuredHeight());
        isOnLayout = false;//没有正在调用onLayout方法
    }

    @Override
    public void requestLayout() {
        if (!isOnLayout) {
            super.requestLayout();
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        final boolean drawContent = child == mContentView;
        boolean isDrawChild = super.drawChild(canvas, child, drawingTime);
        //未滑动百分比大于0且滑动的是当前view，滑动状态也满足条件
        if (mScrimFactor > 0 && drawContent
                && mViewDragHelper.getViewDragState() != ViewDragHelper.STATE_IDLE) {
            //绘制滑动的阴影
            drawShadow(canvas, child);
            //绘制未滑动的部分
            drawScrim(canvas, child);
        }
        return isDrawChild;
    }

    private void drawScrim(Canvas canvas, View child) {
        final int baseAlpha = (DEFAULT_SCRIM_COLOR & 0xff000000) >>> 24;
        final int alpha = (int) (baseAlpha * mScrimFactor);
        final int color = alpha << 24;
        canvas.clipRect(0, 0, child.getLeft(), getHeight());
        canvas.drawColor(color);
    }

    private void drawShadow(Canvas canvas, View child) {
        final Rect childRect = mRect;
        child.getHitRect(childRect);//获取view的Rect

        mShadowLeft.setBounds(childRect.left - mShadowLeft.getIntrinsicWidth(), childRect.top,
                childRect.left, childRect.bottom);
        mShadowLeft.setAlpha((int) (mScrimFactor * FULL_ALPHA));//设置透明度
        mShadowLeft.draw(canvas);//绘制
    }

    /**
     * activity的viewcontent加载完毕后被调用
     * 在DecorView与LinearLayout之间再加一层DragLayout
     * @param activity
     */
    public void attachToActivity(Activity activity) {
        if (activity == null) {
            throw new NullPointerException("activity can not be null!");
        }
        mActivity = activity;
        int background = 0;
        TypedArray a = activity.getTheme().obtainStyledAttributes(new int[]{android.R.attr.windowBackground});
        if (a != null) {
            if (a.hasValue(0)) {
                background = a.getResourceId(0, 0);
            }
            try {
                a.recycle();
            } catch (RuntimeException ex) {

            }
        }
        //获取DecorView
        ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
        //DecorView的第一个子view就是LinearLayout
        ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
        //将LinearLayout设置成window的背景色
        decorChild.setBackgroundResource(background);
        //从DecorView将LinearLayout移除
        decor.removeView(decorChild);
        //将LinearLayout设置到当前ViewGroup中
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(decorChild, params);
        //设置当前滑动的view
        setSwipeView(decorChild);
        //将当前ViewGroup设置到DecorView替换了LinearLayout
        decor.addView(this);
    }

    @Override
    public void computeScroll() {
        mScrimFactor = 1 - mScrollPercent;
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * 3.实现ViewDragHelper.CallCack相关方法
     */
    private class ViewDragCallback extends ViewDragHelper.Callback {
        private boolean isInvoke = false;

        /**
         * tryCaptureView返回true则表示可以捕获该view，你可以根据传入的第一个view参数决定哪些可以捕获
         * 通过DragHelperCallback的tryCaptureView方法的返回值可以决定一个parentview中哪个子view可以拖动
         * // 何时开始检测触摸事件
         * @param child
         * @param pointerId
         * @return
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            //滑动是否时左边缘
            boolean isEdgeLeft = mViewDragHelper.isEdgeTouched(ViewDragHelper.EDGE_LEFT, pointerId);
            boolean directionCheck = false;
            if (isEdgeLeft) {
                //是否时水平滑动
                directionCheck = mViewDragHelper.checkTouchSlop(ViewDragHelper.DIRECTION_HORIZONTAL, pointerId);
            }
            return isEdgeLeft & directionCheck;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return mContentView.getWidth() <= 0 ? 1 : mContentView.getWidth();
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return -1;
        }

        /**
         *  当位置改变的时候调用,常用与滑动时更改scale等
         * @param changedView
         * @param left
         * @param top
         * @param dx
         * @param dy
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            mScrollPercent = Math.abs((float) left
                    / (mContentView.getWidth() + mShadowLeft.getIntrinsicWidth()));
            mContentViewLeftDx = left;
            invalidate();
            if (mSwipeLayoutListener != null) {
                mSwipeLayoutListener.onViewFlingPercent(mScrollPercent);
            }

            if (mScrollPercent < DEFAULT_SCROLL_RATIO) {
                isInvoke = false;
            }
            if (mSwipeLayoutListener != null && mScrollPercent >= DEFAULT_SCROLL_RATIO &&
                    mViewDragHelper.getViewDragState() == ViewDragHelper.STATE_DRAGGING
                    && !isInvoke) {
                isInvoke = true;
                mSwipeLayoutListener.onViewFlingOver();
            }

            if (mScrollPercent >= 1) {
                if (mActivity != null && !mActivity.isFinishing()) {
                    mActivity.finish();
                }
            }
        }

        /**
         * 拖动结束后调用
         * @param releasedChild
         * @param xVel
         * @param yVel
         */
        @Override
        public void onViewReleased(View releasedChild, float xVel, float yVel) {
            if (mSwipeLayoutListener != null) {
                mSwipeLayoutListener.onViewReleased(releasedChild);
            }
            final int childWidth = releasedChild.getWidth();
            int left = xVel >= 0 && mScrollPercent >= DEFAULT_SCROLL_RATIO ? childWidth
                    + mShadowLeft.getIntrinsicWidth() + mEdgeFlingDistance : 0;
            //判断当前是否达到临界点来决定View的位置
            mViewDragHelper.settleCapturedViewAt(left, 0);
            invalidate();
        }

        /**
         * 处理水平滑动
         * 以在该方法中对child移动的边界进行控制,left分别为即将移动到的位置
         *
         * 在DragHelperCallback中实现clampViewPositionHorizontal方法，
         * 并且返回一个适当的数值就能实现横向拖动效果，clampViewPositionHorizontal的第二个参数是指当前拖动子view应该到达的x坐标。
         * 所以按照常理这个方法原封返回第二个参数就可以了，但为了让被拖动的view遇到边界之后就不在拖动，对返回的值做了更多的考虑。
         * @param child
         * @param left
         * @param dx
         * @return
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            Log.d("SwipeBackLayout", "clampViewPositionHorizontal " + left + "," + dx);
            return Math.min(Math.max(left, 0), child.getWidth());
        }

        /**
         * 处理垂直滑动，默认返回0，即不发生滑动
         * 以在该方法中对child移动的边界进行控制,top 分别为即将移动到的位置
         * @param child
         * @param top
         * @param dy
         * @return
         */
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            Log.d("SwipeBackLayout", "clampViewPositionVertical " + top + "," + dy);
            return 0;
        }

        /**
         * 当拖拽状态改变，比如idle，dragging
         * @param state
         */
        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
        }

        /**
         * 如果你想在边缘滑动的时候根据滑动距离移动一个子view，可以通过实现onEdgeDragStarted方法，
         * 并在onEdgeDragStarted方法中手动指定要移动的子View
         * @param edgeFlags
         * @param pointerId
         */
        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            super.onEdgeDragStarted(edgeFlags, pointerId);
            if (mSwipeLayoutListener == null)
                return;
            if ((edgeFlags & ViewDragHelper.EDGE_LEFT) != 0 && mViewDragHelper.isPointerDown(pointerId)) {
                mSwipeLayoutListener.onEdgeDragStarted();
            }
        }

        /**
         * onEdgeTouched方法会在左边缘滑动的时候被调用，这种情况下一般都是没有和子view接触的情况
         * @param edgeFlags
         * @param pointerId
         */
        @Override
        public void onEdgeTouched(int edgeFlags, int pointerId) {
            super.onEdgeTouched(edgeFlags, pointerId);
            Log.d("SwipeBackLayout", "onEdgeTouched called! edgeTouched:" + edgeFlags + ", pointerId" + pointerId);
            isEdgeTouched = true;
            if (mSwipeLayoutListener == null)
                return;
            if ((edgeFlags & ViewDragHelper.EDGE_LEFT) != 0 && mViewDragHelper.isPointerDown(pointerId)) {
                mSwipeLayoutListener.onEdgeTouched();
            }
        }

        /**
         * // 触摸到View后回调
         * @param capturedChild
         * @param activePointerId
         */
        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }
    }
}
