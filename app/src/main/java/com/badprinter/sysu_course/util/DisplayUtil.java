package com.badprinter.sysu_course.util;

import android.util.Log;

import com.badprinter.sysu_course.Common.AppContext;

/**
 * Created by root on 15-9-13.
 */
public class DisplayUtil {
    static public int dp2px(int dp) {
        float d = AppContext.getInstance().getResources().getDisplayMetrics().density;
        return (int)(dp*d);
    }
    /*static public float sp2px(float sp) {
        float sd = AppContext.getInstance().getResources().getDisplayMetrics().scaledDensity;
        Log.e("display", sp + "*" + sd + " = " + (int)(sp*sd));
        return (int)(sp*sd);
    }*/
}
