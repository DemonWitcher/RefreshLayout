package com.witcher.refreshlayout.nested;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.OverScroller;

import com.witcher.refreshlayout.L;

public class NestedParentView extends LinearLayout implements NestedScrollingParent {

    private View mHeaderView;
    private View mContentView;
    private int mHeaderHeight;
    private OverScroller mOverScroller;

    public NestedParentView(Context context) {
        super(context);
        init();
    }

    public NestedParentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NestedParentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        /*
               1.先来个简单的 头部露出就消耗  头部没露出就不消耗
               2.把抛事件也实现了 里外一起抛
         */
        mOverScroller = new OverScroller(getContext());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 2) {
            throw new RuntimeException("得有一个头部和一个正文");
        }
        mHeaderView = getChildAt(0);
        mContentView = getChildAt(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeaderView.measure(widthMeasureSpec,MeasureSpec.makeMeasureSpec(mHeaderView.getLayoutParams().height,MeasureSpec.EXACTLY));
        mContentView.measure(widthMeasureSpec,heightMeasureSpec);
        mHeaderHeight = mHeaderView.getMeasuredHeight();
        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight() + mHeaderHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, @ViewCompat.ScrollAxis int axes) {
        /*
        child  当前view的包含target的直接子view
        target 发起嵌套滚动的子View，此子view必须实现NestedScrollingChild接口。上面提到过，此子view并不需要是当前view的直接子view
        nestedScrollAxes 嵌套滚动的方向，可能是SCROLL_AXIS_HORIZONTAL 或 SCROLL_AXIS_VERTICAL 或 二者都有
        当调用target的startNestedScroll(int axes)时，此方法就会被调用。
        在此方法中我们要做的就是根据target和nestedScrollAxes决定此view是否要与target配合进行嵌套滚动，
        并返回true(要与target配合进行嵌套滚动)或false(不与target配合进行嵌套滚动)。
         */
        return true;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, @ViewCompat.ScrollAxis int axes) {

    }

    @Override
    public void onStopNestedScroll(@NonNull View target) {

    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed) {

    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
//        L.i("onNestedPreScroll dy:" + dy + "  consumed:" + consumed[1]);
        //这里可以消耗一部分滑动距离  为什么有的时候给出的dy数值特别大 而且变化特别大 正200然后突然给出负200
        mOverScroller.forceFinished(true);
        boolean showHeader = dy < 0 && !mContentView.canScrollVertically(-1);
        boolean hide = dy > 0 && getScrollY() < mHeaderHeight;
        if (hide || showHeader) {
            //加边界检测   38  50  93 100 74 抛动8533
            L.i("滚动一次 dy:"+dy);
            scrollBy(0, dy);
            consumed[1] = dy;
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        y = Math.max(y, 0);
        y = Math.min(y, mHeaderHeight);
//        L.i("scroll to:"+y);
        super.scrollTo(x, y);
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        L.i("onNestedPreFling velocityY:" + velocityY+"   consumed:"+consumed);
        //几次move后给出抛动事件 因为里外一起抛动了 所以头还露出一点的情况下rv就滚动了不少
        //理想情况是 如果速度不足以让头部消失 那就不让子view滚
//        mOverScroller.fling(0,getScrollY(),0, (int) velocityY,0,0,
//                mHeaderHeight-getScrollY(),mHeaderHeight);
        int time = computeDuration(velocityY);
        L.i("time:"+time);
        mOverScroller.startScroll(0,getScrollY(),0,mHeaderHeight-getScrollY(),time);
        return true;
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        //这里跟着一起抛动
        //如果头部露出 就滚到头部隐藏位置 然后不给子view  如果没露出 就给子view
        return false;
    }
    private int computeDuration(float velocityY) {
        final int distance;
//        if (velocityY > 0) {
            distance = Math.abs(mHeaderHeight - getScrollY());
//        } else {
//            distance = Math.abs(mHeaderHeight - (mHeaderHeight - getScrollY()));
//        }


        final int duration;
        velocityY = Math.abs(velocityY);
        if (velocityY > 0) {
            duration = 3 * Math.round(1000 * (distance / velocityY));
        } else {
            final float distanceRatio = (float) distance / getHeight();
            duration = (int) ((distanceRatio + 1) * 150);
        }

        return duration;

    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mOverScroller.computeScrollOffset()){
            scrollTo(0,mOverScroller.getCurrY());
        }
    }

    @Override
    @ViewCompat.ScrollAxis
    public int getNestedScrollAxes() {
        return 0;
    }

    public void test1() {
        L.i("mHeaderHeight:" + mHeaderHeight);
        L.i("getScrollY:"+getScrollY());
    }
}
