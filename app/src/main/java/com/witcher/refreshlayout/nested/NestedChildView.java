package com.witcher.refreshlayout.nested;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class NestedChildView extends LinearLayout implements NestedScrollingChild{

    private View mHeaderView;
    private View mContentView;

    public NestedChildView(Context context) {
        super(context);
        init();
    }

    public NestedChildView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NestedChildView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

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
    public void setNestedScrollingEnabled(boolean enabled){

    }
    @Override
    public boolean isNestedScrollingEnabled(){
        return true;
    }
    @Override
    public boolean startNestedScroll(@ViewCompat.ScrollAxis int axes){
        return true;
    }
    @Override
    public void stopNestedScroll(){

    }
    @Override
    public boolean hasNestedScrollingParent(){
        return true;
    }
    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed,
                                 int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow){
        return true;
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed,
                                    @Nullable int[] offsetInWindow){
        return true;
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed){
        return true;
    }
    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY){
        return true;
    }

}
