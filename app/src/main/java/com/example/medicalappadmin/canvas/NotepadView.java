package com.example.medicalappadmin.canvas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.medicalappadmin.Models.Point;

import java.util.ArrayList;
import java.util.List;


public class NotepadView extends View {
    private Paint paint;
    double scaleFactor = 15.0;
    ScaleGestureDetector scaleGestureDetector;
    GestureDetector gestureDetector;
    private float translateX = 0;
    private float translateY = 0;

    private List<Point> coordinates;

    public NotepadView(Context context) {
        super(context);
        init();
    }

    public NotepadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        coordinates = new ArrayList<>();
        scaleGestureDetector = new ScaleGestureDetector(getContext(),new ScaleListener());
        gestureDetector = new GestureDetector(getContext(), new ScrollListener());
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Point point : coordinates) {
            float x = (float) (point.getX() * scaleFactor);
            float y = (float) (point.getY() * scaleFactor);

            Log.d("adi", "Draw Point: x" + x + ", y=" + y);
            canvas.drawPoint(x, y, paint);
        }

        canvas.scale((float)scaleFactor,(float)scaleFactor);
        canvas.translate(translateX, translateY);

//        for (int i = 0; i < coordinates.size() - 1; i++) {
//            Point startPoint = coordinates.get(i);
//            Point endPoint = coordinates.get(i + 1);
//
//            float startX = (float) startPoint.getX();
//            float startY = (float) startPoint.getY();
//            float endX = (float) endPoint.getX();
//            float endY = (float) endPoint.getY();
//
//            canvas.drawLine(startX, startY, endX, endY, paint);
//        }

    }

//    public void drawLineBetweenPoints(Point startPoint, Point endPoint) {
//        coordinates.add(startPoint);
//        coordinates.add(endPoint);
//        invalidate();
//    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return true;
    }

    public void addCoordinate(double x, double y) {
        coordinates.add(new Point(x, y));
        invalidate();
    }



    public void clearDrawing() {
        coordinates.clear();
        invalidate();
    }

    //scaling of screen
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(1f, Math.min(scaleFactor, 10.0f));
            invalidate();
            return true;
        }
    }

    //scrolling the screen
    private class ScrollListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            translateX -= distanceX / scaleFactor;
            translateY -= distanceY / scaleFactor;

            invalidate();
            return true;
        }
    }
}

