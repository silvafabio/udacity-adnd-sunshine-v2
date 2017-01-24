package com.example.android.sunshine.app;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by silva on 07/11/2016.
 */

public class WindSpeedAndDirectionView extends View {

    public WindSpeedAndDirectionView(Context context) {
        super(context);
    }

    public WindSpeedAndDirectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WindSpeedAndDirectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        return super.dispatchPopulateAccessibilityEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int hSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int myHeight = hSpecSize;

        if(hSpecMode == MeasureSpec.EXACTLY){
            myHeight = hSpecSize;
        } else if (hSpecMode == MeasureSpec.AT_MOST){
            // Wrap Content
        }

        int wSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int myWidth = wSpecSize;

        if(wSpecMode == MeasureSpec.EXACTLY){
            myWidth = wSpecSize;
        } else if (wSpecMode == MeasureSpec.AT_MOST){
            // Wrap Content
        }

        setMeasuredDimension(Math.round(myWidth), Math.round(myHeight));
    }
}
