package com.example.medicalappadmin.canvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.medicalappadmin.Models.Point;
import com.example.medicalappadmin.PenDriver.LiveData.DrawLiveDataBuffer;
import com.example.medicalappadmin.R;

import java.util.ArrayList;

public class DetailedPageView extends View {
    private Paint paint;
    float scaleFactor = 14f;
    ScaleGestureDetector scaleGestureDetector;
    GestureDetector gestureDetector;
    private float translateX = 0;
    private float translateY = 0;
    private Bitmap prescriptionBg;

    private int pageHeight ;
    private int pageWidth;

    ArrayList<ArrayList<Point>> mStrokes;

    public DetailedPageView(Context context) {
        super(context);
        init();
    }

    public DetailedPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        scaleGestureDetector = new ScaleGestureDetector(getContext(),new ScaleListener());
        gestureDetector = new GestureDetector(getContext(), new ScrollListener());
        mStrokes = new ArrayList<>();

    }
    public void setBackgroundImageUrl(String imageUrl,int width, int height) {
        loadImage(imageUrl);
        pageHeight = height;
        pageWidth = width;
    }

    private void loadImage(String imageUrl) {
        Glide.with(getContext())
                .asBitmap()
                .load(imageUrl)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        // Set the loaded bitmap as the background image
                        prescriptionBg = resource;
                        invalidate(); // Redraw the view
                    }
                });
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

//        bgPaint.setColor(Color.LTGRAY);
//        bgPaint.setStyle(Paint.Style.FILL);
//        canvas.drawRect(getLeft() + 25, getTop() + 5, 65*scaleFactor, 100* scaleFactor, bgPaint);


        Rect dst = new Rect(getLeft(), getTop(), (int)(pageWidth*(scaleFactor-2)) - 32, (int)(pageHeight*(scaleFactor+2))-32);

        if (prescriptionBg != null) {
            canvas.drawBitmap(prescriptionBg, null,dst, new Paint());
        }

        for (ArrayList<Point> points: mStrokes) {

            boolean first = true;
            for (int i = 0; i < points.size(); i++) {
                Point point = points.get(i);
                if (first) {
                    first = false;
                    path.moveTo(point.x, point.y);
                } else {
                    Point prev = points.get(i - 1);
                    path.quadTo(prev.x, prev.y, (prev.x + point.x)/2, (prev.y + point.y)/2);
                }
            }
            canvas.drawPath(path, paint);
            path.reset();
        }

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

    //Todo: Shift Both Functions to BG Thread to generate a bmp and send that back to our thread
    public void addCoordinates(ArrayList<Point> points) {
        for(Point p:points){
            float x = p.getX();
            float y = p.getY();
            int actionType = p.getActionType();
            x = x*scaleFactor + getLeft();
            y = y*scaleFactor + getTop();
            if(actionType == 1){
                mStrokes.add(new ArrayList<>());
            } else if(actionType == 3){

                mStrokes.get(mStrokes.size()-1).add(new Point(x, y));
            }
        }
        invalidate();
    }
    public void addActions(ArrayList<DrawLiveDataBuffer.DrawAction> points){
        for(DrawLiveDataBuffer.DrawAction p:points){
            float x = p.x;
            float y = p.y;
            int actionType = p.actionType;
            x = x*scaleFactor + getLeft();
            y = y*scaleFactor + getTop();
            if(actionType == 1){
                mStrokes.add(new ArrayList<>());
            } else if(actionType == 3){

                mStrokes.get(mStrokes.size()-1).add(new Point(x, y));
            }
        }
        invalidate();
    }

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

