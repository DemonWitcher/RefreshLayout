package com.witcher.refreshlayout;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.TextView;

public class ScrollerView extends LinearLayout {

    private OverScroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mMaximumVelocity;
    private int mMinimumVelocity;
    private float mDownY;
    private float mLastY;

    public ScrollerView(Context context) {
        super(context);
        init();
    }

    public ScrollerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrollerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        L.i("mMaximumVelocity:" + mMaximumVelocity);//22000
        L.i("mMinimumVelocity:" + mMinimumVelocity);//138
        setOrientation(LinearLayout.VERTICAL);

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        for (int i = 0; i < 100; ++i) {
            View view = layoutInflater.inflate(R.layout.item, this, false);
            TextView tvContent = view.findViewById(R.id.tv_content);
            tvContent.setText(String.valueOf(i));
            addView(view);
        }

    }

    GestureDetector simpleOnGestureListener = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            L.i("onFling");
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            L.i("onScroll");
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }

        @Override
        public void onShowPress(MotionEvent e) {
            super.onShowPress(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }

    });

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        simpleOnGestureListener.onTouchEvent(event);
        int action = event.getAction();
        mVelocityTracker.addMovement(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mVelocityTracker.clear();
                mVelocityTracker.addMovement(event);
                float y = event.getY();
                mDownY = y;
                mLastY = y;
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                float y = event.getY();
                int offset = (int) (mLastY - y);
                int finalY = getScrollY() + offset;
                finalY = Math.max(0, finalY);
                finalY = Math.min(finalY, getChildAt(getChildCount() - 1).getBottom() - getHeight());
                scrollTo(0, finalY);
                mLastY = y;
            }
            break;
            case MotionEvent.ACTION_UP: {
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityY = (int) mVelocityTracker.getYVelocity();//-1855
                L.i("velocityY :" + velocityY);
                if (Math.abs(velocityY) > mMinimumVelocity) {
                    L.i("fling");
                    mScroller.fling(0, getScrollY(), 0, -velocityY, 0, 0, 0, getChildAt(getChildCount() - 1).getBottom() - getHeight());
                    invalidate();
                }
                mVelocityTracker.clear();
            }
            break;
            case MotionEvent.ACTION_CANCEL: {

            }
            break;
        }

        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
//            L.i("mScroller.getCurrY() :" + mScroller.getCurrY());
            scrollTo(0, mScroller.getCurrY());
            invalidate();
        }
    }
}
