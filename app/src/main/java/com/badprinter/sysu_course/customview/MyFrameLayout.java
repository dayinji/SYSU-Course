package com.badprinter.sysu_course.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by root on 15-9-12.
 */
public class MyFrameLayout extends FrameLayout {
    private final String TAG="MyFrameLayout";
    public MyFrameLayout(Context context) {
        this(context, null);
    }

    public MyFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Pass the ev to DragView
        this.getChildAt(0).onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }
}
