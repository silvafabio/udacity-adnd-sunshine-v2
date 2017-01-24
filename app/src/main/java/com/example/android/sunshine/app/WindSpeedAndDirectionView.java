package com.example.android.sunshine.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.v4.view.accessibility.AccessibilityManagerCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

/**
 * Created by silva on 07/11/2016.
 */

public class WindSpeedAndDirectionView extends View {

    float width = 200;
    float height = 200;
    float padding = 4; // padding to edge of view bounds
    private int between_circles = 18;

    private Paint circleOutlinePaint;
    private Paint cardinalTextPaint;
    private int cardinalTextHeight;
    private int cardinalTextWidth;

    private Paint windSpeedTextPaint;

    private static final String LOG_TAG = "TT";
    private Paint linePaint;
    private int view_centre_x;
    private int view_centre_y;
    private int windSpeedTextWidth;

    private String units = "km/h";
    private Paint arrowPaint;

    private int windSpeed;
    private float windDirection;
    private int windAngle = 0;

    private Paint circleInsideSolidPaint;
    private Paint circleBlueSolidPaint;

    public WindSpeedAndDirectionView(Context context) {
        super(context);
        //setWillNotDraw(false);
    }

    public WindSpeedAndDirectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //setWillNotDraw(false);
        initView();
    }

    public WindSpeedAndDirectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //setWillNotDraw(false);
        initView();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // can be expensive

        // outer circle
        float padded_width = width - padding;
        float padded_height = height - padding;
        int outer_radius = Math.round(Math.min(padded_width, padded_height) / 2);
        float center_point = Math.min(width, height) / 2;
        // [x,y] of centre point, circle radius and paint
        canvas.drawCircle(center_point, center_point, outer_radius, circleOutlinePaint);
        canvas.drawCircle(center_point, center_point, outer_radius, circleBlueSolidPaint);

        // inner circle
        int inner_radius = Math.round(outer_radius) - between_circles;
        canvas.drawCircle(center_point, center_point, inner_radius, circleInsideSolidPaint);

        // draw cardinal points
//        Log.v(LOG_TAG, "outer_radius " + outer_radius);

        int point_zero = Math.round(padding / 2);
        int point_middle = Math.round((width - (padding / 2)) / 2);
        int point_end = Math.round(width - (padding / 2));

        // draw text that is not rotated
        // North
        canvas.drawText("N", point_middle - (cardinalTextWidth / 2), point_zero + (cardinalTextHeight), cardinalTextPaint);
        // East
        canvas.drawText("E", point_end - (cardinalTextWidth) - (cardinalTextWidth / 2), point_middle + (cardinalTextHeight / 2), cardinalTextPaint);
        // South
        canvas.drawText("S", point_middle - (cardinalTextWidth / 2), point_end - (cardinalTextHeight / 2), cardinalTextPaint);
//        // West
        canvas.drawText("W", point_zero + (cardinalTextWidth / 2), point_middle + (cardinalTextHeight / 2), cardinalTextPaint);


        // draw lines as I rotate the circle
        int line_X = outer_radius;
        int line_Y = between_circles;
        for (int i = 0; i < 8; i++) {
            // draw a line
            // start [x,y] stop [x,y] and paint
            canvas.drawLine(line_X, line_Y, line_X, line_Y + 10, linePaint);

            // not drawing the cardinal points here as the text is rotated
            // here would be great if view is going to turn
            // text,  [x,y] of text origin and paint
//            canvas.drawText(cardinalPoints[i], cardinalText_X, cardinalText_Y, textPaint);
//            Log.v(LOG_TAG, "drawText "+cardinalPoints[i]+" [" + cardinalText_X + "," + cardinalText_Y + "]");
            // dregrees, [x,y] or pivot point
            canvas.rotate(45, center_point, center_point);
            canvas.save();
        }
        canvas.restore();
        String windSpeed = String.valueOf(getWindSpeed());


        // draw the wind speed
        windSpeedTextPaint.setTextSize(44);
        windSpeedTextWidth = (int) windSpeedTextPaint.measureText(windSpeed);
        canvas.drawText(windSpeed, center_point - (windSpeedTextWidth / 2), center_point, windSpeedTextPaint);

        // draw the units km/h
        windSpeedTextPaint.setTextSize(22);
        windSpeedTextWidth = (int) windSpeedTextPaint.measureText(units);
        canvas.drawText(units, center_point - (windSpeedTextWidth / 2), center_point + 20, windSpeedTextPaint);

        // the arrow
        int triangle_height = 30;
        int triangle_base = 10;
        int triangle_inset = 5;
        int center_point_int = Math.round(center_point);
        int triangle_point_y = center_point_int - 70;
        //    a
        //
        //    c
        // b     d
        Point a = new Point(center_point_int, triangle_point_y);
        Point b = new Point(center_point_int - triangle_base, triangle_point_y + triangle_height);
        Point c = new Point(center_point_int, triangle_point_y + triangle_height - triangle_inset);
        Point d = new Point(triangle_base + center_point_int, triangle_point_y + triangle_height);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(a.x, a.y);
        path.lineTo(b.x, b.y);
        path.lineTo(c.x, c.y);
        path.lineTo(d.x, d.y);
        path.lineTo(a.x, a.y);
        path.close();


        canvas.save();     //Save current canvas matrix state
        canvas.rotate(getWindDirection(), center_point, center_point);
        canvas.drawPath(path, arrowPaint); //Draw first arrow
        canvas.restore();  //Restore canvas matrix to saved state
//        canvas.DrawPath(); //Draw second arrow without the rotation

//        canvas.drawPath(path, arrowPaint);
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        super.dispatchPopulateAccessibilityEvent(event);
        event.getText().add(Utility.getFormattedWind(getContext(), windSpeed, windDirection));
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
/*
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
*/

        setMeasuredDimension(Math.round(width), Math.round(height));
    }

    private void initView() {

        // paint for the circle outlines
        circleOutlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleOutlinePaint.setColor(getResources().getColor(R.color.sunshine_dark_blue));
        circleOutlinePaint.setStrokeWidth(2);
        circleOutlinePaint.setStyle(Paint.Style.STROKE);

        // paint for solid blue
        circleBlueSolidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleBlueSolidPaint.setColor(getResources().getColor(R.color.sunshine_blue));
        circleBlueSolidPaint.setStyle(Paint.Style.FILL);

        // paint for solid white
        circleInsideSolidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleInsideSolidPaint.setColor(Color.WHITE);
        circleInsideSolidPaint.setStyle(Paint.Style.FILL);

        // paint for the cardinal texts
        cardinalTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cardinalTextPaint.setColor(Color.WHITE);
        cardinalTextPaint.setFakeBoldText(true);
        cardinalTextHeight = (int) cardinalTextPaint.measureText("yY");
        cardinalTextWidth = (int) cardinalTextPaint.measureText("N");

        // paint for the wind speed text
        windSpeedTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        windSpeedTextPaint.setFakeBoldText(true);
        windSpeedTextPaint.setColor(getResources().getColor(R.color.sunshine_dark_blue));

        // paint for the lines
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(getResources().getColor(R.color.sunshine_dark_blue));
        linePaint.setStrokeWidth(2);
        linePaint.setStyle(Paint.Style.STROKE);

        // paint for the arrow
        arrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arrowPaint.setColor(getResources().getColor(R.color.sunshine_blue));
        arrowPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        view_centre_x = Math.round(width) / 2;
        view_centre_y = Math.round(height) / 2;
    }


    public float getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(float windDirection) {
        this.windDirection = windDirection;

        AccessibilityManager accessibilityManager =
                (AccessibilityManager) getContext().getSystemService(
                        Context.ACCESSIBILITY_SERVICE);
        if (accessibilityManager.isEnabled()) {
            sendAccessibilityEvent(
                    AccessibilityEvent.TYPE_VIEW_FOCUSED);
        }
    }

    public int getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(int windSpeed) {
        this.windSpeed = windSpeed;
    }
}
