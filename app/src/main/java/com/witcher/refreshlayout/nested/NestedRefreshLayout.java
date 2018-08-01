package com.witcher.refreshlayout.nested;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.OverScroller;

import com.witcher.refreshlayout.L;
import com.witcher.refreshlayout.R;

public class NestedRefreshLayout extends ViewGroup implements NestedScrollingParent {

    public static final int NORMAL = 1;//正常
    public static final int REFRESHING = 2;//刷新中
    public static final int FINIFSHING = 3;//刷新完成回退中

    private RefreshListener mRefreshListener;
    private int mState = NORMAL;
    private View mContentView;
    private View mHeaderView;
    private int mHeaderHeight;//头部高度
    private int mMoreDistance = 300;//超过头部后还可以下拉的距离 px
    private int mMaxDistance;//总可下拉距离
    private float mLastY;
    private OverScroller mOverScroller;
    private int mAutoBackTime = 200;//下拉一点不足触发刷新时回滚动画时间
    private int mFinishRefreshTime = 200;//完成刷新自动回滚动画时间
    private int mBackToHeaderTime = 200;//下拉刷新时自动回退到头部刚好露出的时间

    public NestedRefreshLayout(Context context) {
        super(context);
        init();
    }

    public NestedRefreshLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NestedRefreshLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mOverScroller = new OverScroller(getContext());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 1) {
            L.i("必须得有一个子view");
            throw new RuntimeException("必须得有一个子view");
        }
        mContentView = getChildAt(0);
        mHeaderView = LayoutInflater.from(getContext()).inflate(R.layout.refresh_head, this, false);
        addViewInLayout(mHeaderView, -1, mHeaderView.getLayoutParams());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        L.i("onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        L.i("onLayout");
        //给头view布局 把它摆在view的上边 top给负的头高度,
        mHeaderHeight = mHeaderView.getMeasuredHeight();
        mMaxDistance = mHeaderHeight + mMoreDistance;
        L.i("mMaxDistance:" + mMaxDistance);
        int headerTop = -mHeaderHeight;
        mHeaderView.layout(left, headerTop, right, 0);

        mContentView.layout(left, 0, right, mContentView.getMeasuredHeight());
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, @ViewCompat.ScrollAxis int axes) {
        L.i("onStartNestedScroll");
        whenDown();
        return true;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, @ViewCompat.ScrollAxis int axes) {

    }

    @Override
    public void onStopNestedScroll(@NonNull View target) {
        L.i("onStopNestedScroll");
        //如果超过头部 就回滚到头部刚好露出
        whenUp();
    }

    private void whenDown() {
        mOverScroller.forceFinished(true);
    }

    private void whenUp() {
        if (getScrollY() >= -mHeaderHeight && getScrollY() <= 0) {
            //如果没超过头部 就回滚到头部隐藏
            mOverScroller.startScroll(0, getScrollY(), 0, -getScrollY(), mAutoBackTime);
            invalidate();
        } else if (getScrollY() < -mHeaderHeight) {
            mState = REFRESHING;
            mOverScroller.startScroll(0, getScrollY(), 0, -mHeaderHeight - getScrollY(), mBackToHeaderTime);
            invalidate();
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mOverScroller.computeScrollOffset()) {
            scrollTo(0, mOverScroller.getCurrY());
            invalidate();
            if (mOverScroller.isFinished()) {
                if (mState == REFRESHING) {
                    if (mRefreshListener != null) {
                        mRefreshListener.onRefresh();
                    }
                } else if (mState == FINIFSHING) {
                    mState = NORMAL;
                    if (mRefreshListener != null) {
                        mRefreshListener.onFinish();
                    }
                }
            }
        }
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed) {

    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
//        L.i("onNestedPreScroll dy:" + dy + "  consumed:" + consumed[1]);
        //消耗头部 0 - -maxDistance
        if (whenMove(dy)) {
            consumed[1] = dy;
        }
    }

    private boolean whenMove(int dy) {
        boolean isShowHeader = dy < 0 && !mContentView.canScrollVertically(-1);
        boolean isHideHeader = dy > 0 && getScrollY() < 0;

        if (isHideHeader || isShowHeader) {
            scrollBy(0, dy);
        }
        return isHideHeader || isShowHeader;
    }

    @Override
    public void scrollTo(int x, int y) {
        y = Math.max(y, -mMaxDistance);//-mMaxDistance到0
        y = Math.min(y, 0);
        super.scrollTo(x, y);
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        L.i("onNestedPreFling velocityY:" + velocityY + "   consumed:" + consumed);
        return false;
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        //这里跟着一起抛动
        //如果头部露出 就滚到头部隐藏位置 然后不给子view  如果没露出 就给子view
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mContentView instanceof NestedScrollingChild) {
            return super.dispatchTouchEvent(event);
        }
        int action = event.getAction();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                whenDown();
                mLastY = y;
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                whenMove((int) (mLastY-y));
                mLastY = event.getY();
            }
            break;
            case MotionEvent.ACTION_UP: {
                whenUp();
            }
            break;
            case MotionEvent.ACTION_CANCEL: {

            }
            break;
        }
        return super.dispatchTouchEvent(event);
    }

    public void stopRefresh() {
        L.i("结束刷新");
        mState = FINIFSHING;
        mOverScroller.startScroll(0, getScrollY(), 0, -getScrollY(), mFinishRefreshTime);
        invalidate();
    }

    public void setRefreshListener(RefreshListener refreshListener) {
        this.mRefreshListener = refreshListener;
    }

    public interface RefreshListener {
        void onRefresh();

        void onFinish();
    }
}
