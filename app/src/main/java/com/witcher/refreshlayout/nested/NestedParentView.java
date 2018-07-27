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
        L.i("onNestedPreScroll dy:" + dy + "  consumed:" + consumed[1]);
        //这里可以消耗一部分滑动距离
        boolean showHeader = dy < 0 && !mContentView.canScrollVertically(-1);
        boolean hide = dy > 0 && getScrollY() < mHeaderHeight;
        if (hide || showHeader) {
            //加边界检测
            scrollBy(0, dy);
            consumed[1] = dy;
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        y = Math.max(y, 0);
        y = Math.min(y, mHeaderHeight);
        super.scrollTo(x, y);
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        L.i("onNestedPreFling velocityY:" + velocityY);
        //这里跟着一起抛动

        return false;
    }

    @Override
    @ViewCompat.ScrollAxis
    public int getNestedScrollAxes() {
        return 0;
    }

    public void test1() {
        L.i("rv height:" + mContentView.getHeight());
        L.i("al height:" + (getHeight() - mHeaderHeight));
    }
}
