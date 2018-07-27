package com.witcher.refreshlayout;

import android.content.Context;
import android.view.VelocityTracker;
import android.widget.OverScroller;
import android.widget.Scroller;

public class TestScroller {

    private Scroller scroller;
    private OverScroller overScroller;
    private VelocityTracker velocityTracker;

    public TestScroller(Context context) {
        this.scroller = new Scroller(context);
        this.overScroller = new OverScroller(context);
        velocityTracker = VelocityTracker.obtain();
    }

    public void test(){
    }
}
