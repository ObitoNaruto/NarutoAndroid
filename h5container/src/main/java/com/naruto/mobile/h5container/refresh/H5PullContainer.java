
package com.naruto.mobile.h5container.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

public class H5PullContainer extends FrameLayout implements OverScrollListener {
    public static final String TAG = "H5PullContainer";

    public static final int DEFALUT_DURATION = 400;

    private enum State {
        STATE_FIT_CONTENT, STATE_OPEN, STATE_OVER, STATE_FIT_EXTRAS
    };

    protected State state = State.STATE_FIT_CONTENT;
    private Flinger flinger = new Flinger();;
    private H5PullAdapter pullAdapter;
    private View contentView;
    private int lastY;
    private boolean overScrolled;

    private View headerView;
    protected int headerHeight;

    public H5PullContainer(Context context) {
        super(context);
    }

    public H5PullContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public H5PullContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent intent) {
        if (handleTouch(intent)) {
            return true;
        }
        return super.dispatchTouchEvent(intent);
    }

    private boolean canPull() {
        if (pullAdapter != null && !pullAdapter.canPull()) {
            return false;
        }

        if (contentView == null) {
            return false;
        }

        return true;
    }

    private boolean canRefresh() {
        if (pullAdapter != null && pullAdapter.canRefresh()) {
            return true;
        }
        return false;
    }

    private boolean handleTouch(MotionEvent intent) {
        if (!canPull()) {
            return false;
        }

        int action = intent.getAction();
        int contentTop = contentView.getTop();
        boolean actionUp = action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_CANCEL;
        if (actionUp) {
            overScrolled = false;
        }

        if (contentTop > 0 && actionUp) {
            if (hasHeader()) {
                if (state == State.STATE_OVER) {
                    fitExtras();
                } else if (state == State.STATE_FIT_EXTRAS) {
                    if (contentTop > headerHeight) {
                        int offset = contentTop - headerHeight;
                        flinger.recover(offset);
                    }
                } else if (state == State.STATE_OPEN) {
                    flinger.recover(contentTop);
                } else {
                    flinger.recover(contentTop);
                }
            } else {
                flinger.recover(contentTop);
            }
            return false;
        }

        if (action == MotionEvent.ACTION_MOVE) {
            boolean handled = false;
            int offset = (int) (intent.getY() - lastY);
            int scrollY = contentView.getScrollY();
            offset = offset / 2;
            if (overScrolled && scrollY <= 0) {
                moveOffset(offset);
                handled = true;
            }
            lastY = (int) intent.getY();
            return handled;
        }
        return false;
    }

    private class Flinger implements Runnable {
        private Scroller scroller;
        private int lastScrollY;
        private boolean finished;

        public Flinger() {
            scroller = new Scroller(getContext());
            finished = true;
        }

        @Override
        public void run() {
            boolean offset = scroller.computeScrollOffset();
            if (offset) {
                moveOffset(lastScrollY - scroller.getCurrY());
                lastScrollY = scroller.getCurrY();
                post(this);
            } else {
                finished = true;
                removeCallbacks(this);
                if (pullAdapter != null) {
                    pullAdapter.onFinish();
                }
            }
        }

        public void recover(int offset) {
            removeCallbacks(this);
            lastScrollY = 0;
            finished = false;
            scroller.startScroll(0, 0, 0, offset, DEFALUT_DURATION);
            post(this);
        }

        public boolean isFinished() {
            return finished;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom) {
        int contentTop = 0;

        if (contentView != null) {
            contentTop = contentView.getTop();
            contentView.layout(0, contentTop, right,
                    contentTop + contentView.getMeasuredHeight());
        }

        int headerTop = contentTop - headerHeight;
        if (hasHeader()) {
            headerView.layout(0, headerTop, right, headerTop + headerHeight);
        }
    }

    private boolean moveOffset(int offset) {
        if (contentView == null) {
            return false;
        }

        if (state != State.STATE_FIT_EXTRAS) {
            int nextTop = contentView.getTop() + offset;
            if (nextTop <= 0) {
                offset = -contentView.getTop();
            } else if (nextTop <= headerHeight) {
                if ((state == State.STATE_OVER || state == State.STATE_FIT_CONTENT)
                        && flinger.isFinished()) {
                    if (pullAdapter != null) {
                        pullAdapter.onOpen();
                    }
                    state = State.STATE_OPEN;
                }
            } else if (nextTop > headerHeight) {
                if (state == State.STATE_OPEN) {
                    if (pullAdapter != null) {
                        pullAdapter.onOver();
                    }
                    state = State.STATE_OVER;
                }
            }
        }

        contentView.offsetTopAndBottom(offset);

        if (hasHeader()) {
            headerView.offsetTopAndBottom(offset);
        }

        invalidate();
        return true;
    }

    private boolean hasHeader() {
        if (headerView == null) {
            return false;
        }

        if (headerView.getVisibility() != View.VISIBLE) {
            return false;
        }

        return true;
    }

    private void fitExtras() {
        if (state == State.STATE_FIT_EXTRAS) {
            return;
        }
        state = State.STATE_FIT_EXTRAS;

        if (hasHeader()) {
            int offset = contentView.getTop() - headerHeight;
            flinger.recover(offset);
        }

        if (pullAdapter != null) {
            pullAdapter.onLoading();
        }
    }

    public void setContentView(View view) {
        this.contentView = view;
        if (contentView instanceof H5PullableView) {
            H5PullableView pv = (H5PullableView) contentView;
            pv.setOverScrollListener(this);
        }
        addView(contentView, 0);
    }

    public void fitContent() {
        if (state != State.STATE_FIT_EXTRAS) {
            return;
        }

        if (contentView == null) {
            return;
        }

        int offset = contentView.getTop();
        if (offset > 0) {
            flinger.recover(offset);
        }
        state = State.STATE_FIT_CONTENT;
    }

    public void setPullAdapter(H5PullAdapter adapter) {
        if (headerView != null) {
            removeView(headerView);
            headerView = null;
        }
        pullAdapter = adapter;

        notifyViewChanged();
    }

    @Override
    public void onOverScrolled(int deltaX, int deltaY, int scrollX, int scrollY) {
        if (contentView == null) {
            return;
        }

        if (contentView.getScrollY() <= 0 && deltaY < 0 && scrollY <= 0) {
            overScrolled = true;
        }
    }

    public void notifyViewChanged() {
        if (headerView == null) {
            updateHeader();
        }

        if (headerView != null) {
            if (!canRefresh()) {
                headerView.setVisibility(View.GONE);
            } else {
                headerView.setVisibility(View.VISIBLE);
            }
        }
    };

    private void updateHeader() {
        if (getChildCount() < 1) {
            throw new IllegalStateException("content view not added yet");
        }

        headerView = pullAdapter.getHeaderView();
        if (headerView == null) {
            return;
        }

        headerView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        headerHeight = headerView.getMeasuredHeight();
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT, headerHeight);
        addView(headerView, 0, params);
    }
}
