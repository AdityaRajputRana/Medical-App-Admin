package com.example.medicalappadmin.canvas;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.medicalappadmin.Models.Point;
import com.example.medicalappadmin.PenDriver.LiveData.DrawLiveDataBuffer;
import com.example.medicalappadmin.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NotepadView extends View {
    private Paint paint;
    float scaleFactor = 12.25f;
    ScaleGestureDetector scaleGestureDetector;
    GestureDetector gestureDetector;
    private float translateX = 0;
    private float translateY = 0;
    private Bitmap prescriptionBg;

    private int pageHeight ;
    private int pageWidth;

    private Bitmap cachedBitmap;

    ArrayList<ArrayList<Point>> mStrokes;

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

    Path previousDrawPath;
    Path currentLivePath;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.i("Optimiz", "onDraw() called");
        Paint bgPaint = new Paint();
        bgPaint.setColor(Color.LTGRAY);
        bgPaint.setStyle(Paint.Style.FILL);

        Rect dst = new Rect(getLeft(), getTop(), (int)(pageWidth*scaleFactor) + getLeft(), (int)(pageHeight*scaleFactor)+ getTop());
        if (prescriptionBg != null) {
            canvas.drawBitmap(prescriptionBg, null,dst, new Paint());
        }


        if (previousDrawPath != null){
            canvas.drawPath(previousDrawPath, paint);
        }
        if (currentLivePath != null){
            canvas.drawPath(currentLivePath, paint);
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return true;
    }

    float currentLiveX  =0f;
    float currentLiveY = 0f;
    public void addCoordinate(float x, float y, int actionType) {
        Log.i("Optimiz", "Add Co-ordinate called");
        if (currentLivePath == null)
            currentLivePath = new Path();
        x = x*scaleFactor + getLeft();
        y = y*scaleFactor + getTop();
        if(actionType == 1){
            currentLivePath.moveTo(x, y);
        } else {
            currentLivePath.quadTo(currentLiveX, currentLiveY, (currentLiveX + x)/2, (currentLiveY + y)/2);
        }

        currentLiveX = x;
        currentLiveY = y;
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(()->{
            Log.i("Optimi", "Calling invalidate");
            invalidate();
        });
    }



    public void clearDrawing() {
        mStrokes.clear();
        previousDrawPath = null;
        currentLivePath = null;
        invalidate();
    }

    //Todo: Shift Both Functions to BG Thread to generate a bmp and send that back to our thread
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

