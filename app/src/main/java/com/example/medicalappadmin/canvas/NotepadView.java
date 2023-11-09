package com.example.medicalappadmin.canvas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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
    float scaleFactor = 15f;
    ScaleGestureDetector scaleGestureDetector;
    GestureDetector gestureDetector;
    private float translateX = 0;
    private float translateY = 0;

    ArrayList<Path> strokesList;
    Path prePath;


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
        strokesList = new ArrayList<>();
        scaleGestureDetector = new ScaleGestureDetector(getContext(),new ScaleListener());
        gestureDetector = new GestureDetector(getContext(), new ScrollListener());
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Path path : strokesList) {
            canvas.drawPath(path,paint);
        }
        if(prePath != null){

            canvas.drawPath(prePath,paint);
        }
        canvas.scale((float)scaleFactor,(float)scaleFactor);
        canvas.translate(translateX, translateY);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return true;
    }

    public void addCoordinate(float x, float y, int actionType) {
        x = x*scaleFactor;
        y = y*scaleFactor;
        if(actionType == 1){
          if(prePath != null){
              strokesList.add(prePath);
          }
          prePath = new Path();
          prePath.moveTo(x,y);
        } else if(actionType == 2) {
            if(prePath != null){
                strokesList.add(prePath);
                prePath = null;
            }
        } else if(actionType == 3){
            if(prePath == null){
                prePath = new Path();
                prePath.moveTo(x,y);
            }
            prePath.lineTo(x,y);
        }
        invalidate();
    }



    public void clearDrawing() {
        strokesList.clear();
        prePath = null;
        invalidate();
    }

    public void addCoordinates(ArrayList<Point> points) {
        for(Point p:points){
            float x = p.getX();
            float y = p.getY();
            int actionType = p.getActionType();
            x = x*scaleFactor;
            y = y*scaleFactor;
            if(actionType == 1){
                if(prePath != null){
                    strokesList.add(prePath);
                }
                prePath = new Path();
                prePath.moveTo(x,y);
            } else if(actionType == 2) {
                if(prePath != null){
                    strokesList.add(prePath);
                    prePath = null;
                }
            } else if(actionType == 3){
                if(prePath == null){
                    prePath = new Path();
                    prePath.moveTo(x,y);
                }
                prePath.lineTo(x,y);
            }
        }
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

