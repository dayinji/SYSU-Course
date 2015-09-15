package com.badprinter.sysu_course.customview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.badprinter.sysu_course.Common.GlobalData;
import com.badprinter.sysu_course.R;
import com.badprinter.sysu_course.util.DisplayUtil;

/**
 * Created by root on 15-9-14.
 */
public class LogView extends View {
    private final String TAG = "LogView";

    private Paint paint;
    private float textSize;
    private int textColor;
    private String[] textCache;
    private int textNum = 4;
    private int durartion = 100;
    private ValueAnimator anim;

    public LogView(Context context) {
        this(context, null);
    }

    public LogView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LogView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        textCache = new String[textNum];
        for (int i = 0 ; i < textNum ; i++)
            textCache[i] = "";

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.LogView);

        //获取自定义属性和默认值
        textColor = mTypedArray.getInt(R.styleable.LogView_logTextColor, R.color.lanse);
        textSize = mTypedArray.getInt(R.styleable.LogView_logTextSize, 12);
        Log.e(TAG, "before textsize = " + textSize);
        textSize =  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, getResources().getDisplayMetrics());
        Log.e(TAG, "after textsize = " + textSize);

        paint = new Paint();
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);  //消除锯齿

        mTypedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        for (int i = 0 ; i < textNum ; i++) {
            canvas.drawText(textCache[i], 0, height*i/4 + textSize, paint);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    public void updateLog() {
        if (anim != null && anim.isRunning())
            anim.cancel();
        anim = ValueAnimator.ofInt(0, GlobalData.logInfo.size() - 1);
        anim.setDuration(300*GlobalData.logInfo.size());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private int recordLast = -1;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if ((int) animation.getAnimatedValue() > recordLast) {
                    recordLast = (int) animation.getAnimatedValue();
                    for (int i = 0; i < textNum - 1; i++)
                        textCache[i] = textCache[i + 1];
                    textCache[textNum - 1] = GlobalData.logInfo.get(recordLast);
                    //GlobalData.logInfo.remove(0);
                    invalidate();
                }
            }
        });
        anim.start();
    }
}
