package com.witcher.refreshlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.OverScroller;

public class RefreshLayout2 extends ViewGroup {

    public static final int NORMAL = 1;//正常
    public static final int REFRESHING = 2;//刷新中
    public static final int FINIFSHING = 3;//刷新完成回退中

    private View mContentView;
    private View mHeaderView;

    private OverScroller mScroller;
    private int mState = NORMAL;

    private VelocityTracker mVelocityTracker;

    private float mDownY;
    private float mLastY;
    private int mHeaderHeight;//头部高度
    private int mMoreDistance = 300;//超过头部后还可以下拉的距离 px
    private int mMaxDistance;//总可下拉距离
    private int mMaximumVelocity;
    private int mMinimumVelocity;

    private int mAutoBackTime = 200;//下拉一点不足触发刷新时回滚动画时间
    private int mFinishRefreshTime = 2000;//完成刷新自动回滚动画时间
    private int mBackToHeaderTime = 200;//下拉刷新时自动回退到头部刚好露出的时间

    private RefreshListener mRefreshListener;

    public RefreshLayout2(Context context) {
        super(context);
        init();
    }

    public RefreshLayout2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RefreshLayout2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mScroller = new OverScroller(getContext()
//                , new LinearInterpolator()
        );
        mVelocityTracker = VelocityTracker.obtain();
//        requestDisallowInterceptTouchEvent(true);
        mMaximumVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
        mMinimumVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();
        L.i("mMaximumVelocity:"+mMaximumVelocity);
        L.i("mMinimumVelocity:"+mMinimumVelocity);
//        requestDisallowInterceptTouchEvent(false);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 1) {
            L.i("必须得有一个子view");
            throw new RuntimeException("必须得有一个子view");
        }
        mContentView = getChildAt(0);
        /*
        添加刷新布局
        处理下拉手势
        如果子view处于顶部状态 那么再向下滑动就触发下拉刷新  下拉的话整体下移 最多下移头部再加上一段距离
        如果松手时 下拉距离超过头部高度 就滚回正好头部露出的位置 然后触发刷新事件
        暂时刷新时不让做手势 然后调用刷新完成后 UI整体滚上去
         */
        mHeaderView = LayoutInflater.from(getContext()).inflate(R.layout.refresh_head, this, false);
        addViewInLayout(mHeaderView, -1, mHeaderView.getLayoutParams());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        L.i("dispatchTouchEvent");
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mDownY = event.getY();
                mLastY = event.getY();
                if (!mScroller.isFinished()) {
                    mScroller.forceFinished(true);
//                    mScroller.abortAnimation();这俩都会把finish置为true 就不走computeScrollOffset了
                }
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                //正常状态 上划手势并且子view可以划 就给
                //刷新中状态 上滑到头部消失并且子view可以划 就给
                if (mState == NORMAL) {
                    if (!mContentView.canScrollVertically(-1) //子view可以滑动的检测
                            && event.getY() > mDownY                   //手势方向检测
                            ) {
                        return true;
                    }
                } else if (mState == REFRESHING || mState == FINIFSHING) {
                    return true;
                }

            }
            break;
            case MotionEvent.ACTION_UP: {

            }
            break;
        }
        return false;
    }

    /*
    刷新中的时候 上滑是整体滚动 下滑是2次刷新
                //根据手势滚动view  如果处于刷新中状态
                // 上拉给子view  下拉的话并且子view不可滑动 则整体再次下移 松手的话
                // 如果位置满足刷新条件则再次触发下拉刷新 如果整体被拉回去了则????? 整体再下移到刷新状态吗???

     */
//    @Override
//    public void requestDisallowInterceptTouchEvent(boolean b) {
//        // Nope.
//    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int action = event.getAction();
//        mVelocityTracker.addMovement(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                L.i("onTouch   down");
//                mVelocityTracker.clear();
//                mVelocityTracker.addMovement(event);
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                float y = event.getY();
                boolean up = mLastY >y;
                if ((mState == REFRESHING || mState == FINIFSHING) &&
                        (getScrollY() == 0 && mContentView.canScrollVertically(up?1:-1))//这里改成根据方向来判断正负1
                        ) {
                    mContentView.onTouchEvent(event);
//                            requestDisallowInterceptTouchEvent(true);
                    L.i("下放给子view了");
                } else {
//                    requestDisallowInterceptTouchEvent(false);
                    if (getScrollY() >= -mMaxDistance) {
                        int offset = (int) (mLastY - y);
                        int finalScrollY = getScrollY() + offset;
                        finalScrollY = Math.min(finalScrollY, 0);
                        finalScrollY = Math.max(-mMaxDistance, finalScrollY);
                        scrollTo(0, finalScrollY);
                        if (getScrollY() < -mHeaderHeight) {
//                        L.i("松手可以刷新了");
                        }
                    } else {
//                    L.i("到量了");
                    }
                }
                mLastY = y;
            }
            break;
            case MotionEvent.ACTION_UP: {
                //如果超过了头部 回滚超过头部的距离 然后触发刷新
                //如果没超过头部 回滚到0
                if (getScrollY() != 0) {
                    if (getScrollY() > -mHeaderHeight) {
                        if (mState == NORMAL || mState == FINIFSHING) {
//                            L.i("滚回去"); //正常状态回退回去  刷新中状态不做处理
                            mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), mAutoBackTime);
                            invalidate();
                        }
                    } else {
//                        L.i("开始刷新");
                        mState = REFRESHING;
                        mScroller.startScroll(0, getScrollY(), 0, -(getScrollY() + mHeaderHeight), mBackToHeaderTime);
                        invalidate();
                    }
                }

            }
            break;
        }
        return true;
    }

    public void stopRefresh() {
        L.i("结束刷新");
        mState = FINIFSHING;
        mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), mFinishRefreshTime);
        invalidate();
    }

    public void setRefreshListener(RefreshListener refreshListener) {
        this.mRefreshListener = refreshListener;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            invalidate();
            if (mScroller.isFinished()) {
                if (mState == REFRESHING) {
//                    L.i("给出开始刷新回调");
                    if (mRefreshListener != null) {
                        mRefreshListener.onRefresh();
                    }
                } else if (mState == FINIFSHING) {
                    mState = NORMAL;
//                    L.i("刷新完成 回到普通状态");
                    if (mRefreshListener != null) {
                        mRefreshListener.onFinish();
                    }
                }
            }
        }
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


    public interface RefreshListener {
        void onRefresh();

        void onFinish();
    }
}
