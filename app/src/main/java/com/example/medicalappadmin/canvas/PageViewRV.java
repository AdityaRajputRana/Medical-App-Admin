package com.example.medicalappadmin.canvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.example.medicalappadmin.Models.Point;
import com.example.medicalappadmin.R;

import java.util.ArrayList;

public class PageViewRV extends View {
    private Paint paint;
    float scaleFactor = 7.5f;
    ScaleGestureDetector scaleGestureDetector;
    GestureDetector gestureDetector;
    private float translateX = 0;
    private float translateY = 0;
    private Bitmap prescriptionBg;


    ArrayList<ArrayList<Point>> mStrokes;

    public PageViewRV(Context context) {
        super(context);
        init();
    }

    public PageViewRV(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        scaleGestureDetector = new ScaleGestureDetector(getContext(),new ScaleListener());
        gestureDetector = new GestureDetector(getContext(), new ScrollListener());

        mStrokes = new ArrayList<>();
        prescriptionBg = getPrescriptionBMP();
    }

    private Bitmap getPrescriptionBMP(){
        Drawable d = getContext().getDrawable(R.drawable.bg_prescription);
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        return bitmap;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Path path = new Path();
        Paint bgPaint = new Paint();
        bgPaint.setColor(Color.LTGRAY);
        bgPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(getLeft() + 25, getTop() + 5, 100*scaleFactor, 100* scaleFactor, bgPaint);
        if (previousDrawPath != null)
            canvas.drawPath(previousDrawPath, paint);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return true;
    }

    public void addCoordinate(float x, float y, int actionType) {
        x = x*scaleFactor + getLeft();
        y = y*scaleFactor + getTop();
        if(actionType == 1){
          mStrokes.add(new ArrayList<>());

        } else if(actionType == 3){
            mStrokes.get(mStrokes.size()-1).add(new Point(x,y));
        }
        invalidate();
    }



    public void clearDrawing() {
        mStrokes.clear();
        invalidate();
    }

    public void addCoordinates(ArrayList<Point> points) {
        Log.i("Optimiz", "Add Co-ordinates executing");
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Log.i("Optimiz", "starting thread");
        new Thread(() -> {
            Path path = createPathFromPoints(points);

            mainHandler.post(() -> {
                previousDrawPath = path;
                invalidate();
            });

        }).start();
        Log.i("Optimiz", "post thread start");
    }


    private Path createPathFromPoints(ArrayList<Point> points){
        if (points == null || points.size() == 0)
            return null;
        Path path = new Path();
        float prevX, prevY;
        prevX = points.get(0).getX();
        prevY = points.get(0).getY();

        path.moveTo(prevX, prevY);
        for(Point p:points){
            float x = p.getX();
            float y = p.getY();
            int actionType = p.getActionType();
            x = x*scaleFactor + getLeft();
            y = y*scaleFactor + getTop();
            if(actionType == 1){
                path.moveTo(x, y);
            } else if(actionType == 3){
                path.quadTo(prevX, prevY, (prevX + x)/2, (prevY + y)/2);
            }
            prevX = x;
            prevY = y;

        }
        return path;
    }
    private Path previousDrawPath;


    //scaling of screen
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
//            scaleFactor *= detector.getScaleFactor();
//            scaleFactor = Math.max(1f, Math.min(scaleFactor, 10.0f));
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

