package com.witcher.refreshlayout.scaleimage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.OverScroller;

import com.witcher.refreshlayout.L;

public class ScaleHeaderView extends ViewGroup implements NestedScrollingParent {

    private View mHeaderView;
    private View mContentView;

    private int mMaxDistance = 500; // 最多下拉500 就不让拉了
    private int mScaleDistance = 200;//下拉到200时 header到缩放最大值
    private int mMaxHideDistance = 200;//最多划到 200 头部隐藏

    private int mBackTime = 200;//松手后回弹耗时
    private float mMaxScale = 0.2f;//头部缩放增量

    private OverScroller mScroller;
    private int offsetY;
    private int scrollProgress;

    private ProgressListener mProgressListener;

    public ScaleHeaderView(Context context) {
        super(context);
        init();
    }

    public ScaleHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScaleHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mScroller = new OverScroller(getContext());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 2) {
            throw new RuntimeException("一个header 一个content");
        }
        mHeaderView = getChildAt(0);
        mContentView = getChildAt(1);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, @ViewCompat.ScrollAxis int axes) {
        whenDown();
        return true;
    }

    private void whenDown() {
        mScroller.forceFinished(true);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target) {
        whenUp();
    }

    private void whenUp(){
        mScroller.startScroll(0,offsetY,0,-offsetY,mBackTime);
        invalidate();
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        /*
        下拉手势中 头部缩放 正文位移
        上拉手势中 给出进度回调
         */
        if (whenMove(dy)) {
            consumed[1] = dy;
        }else{
            scrollProgress = scrollProgress + dy;
            scrollProgress = Math.max(0,scrollProgress);
            scrollProgress = Math.min(scrollProgress,mMaxHideDistance);
            if(mProgressListener !=null){
                float progress = (float) scrollProgress/mMaxHideDistance;
                mProgressListener.onScroll(progress);
            }
        }
    }

    private boolean whenMove(int dy) {
        boolean down = dy < 0 && !mContentView.canScrollVertically(-1);
        boolean up = dy > 0 && offsetY > 0;
        if (down || up) {
            int finalY = offsetY - dy;
            finalY = Math.max(finalY, 0);
            finalY = Math.min(mMaxDistance, finalY);
            offsetY = finalY;
            mContentView.setTranslationY(finalY);
            scaleHeader();
        }
        return down || up;
    }

    private void scaleHeader(){
        int progress = offsetY;
        progress = Math.max(0,progress);
        progress = Math.min(mScaleDistance,progress);

        float scale =(float)progress/mScaleDistance * mMaxScale  + 1.0f;

        mHeaderView.setScaleY(scale);
        mHeaderView.setScaleX(scale);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            int currentY = mScroller.getCurrY();
            offsetY = currentY;
            mContentView.setTranslationY(currentY);
            scaleHeader();
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mHeaderView.layout(l, t, r, t + mHeaderView.getMeasuredHeight());
        mContentView.layout(l, t, r, b);
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public View getContentView() {
        return mContentView;
    }

    public void setProgressListener(ProgressListener progressListener){
        this.mProgressListener = progressListener;
    }

    public interface ProgressListener{
        void onScroll(float progress);
    }
}
