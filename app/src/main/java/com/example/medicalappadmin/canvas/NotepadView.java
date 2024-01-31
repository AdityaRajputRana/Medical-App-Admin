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
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
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

public class NotepadView extends View {
    private Paint paint;
    float scaleFactor = 1f;
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

    }
    public void setBackgroundImageUrl(String imageUrl,int width, int height) {
        loadImage(imageUrl);
        pageHeight = height;
        pageWidth = width;
        scaleFactor = Math.min((float)getWidth()/width, (float)getHeight()/height);
        paint.setStrokeWidth(4f/scaleFactor);
        invalidate();
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

        canvas.scale(scaleFactor, scaleFactor);

        Paint bgPaint = new Paint();
        bgPaint.setColor(Color.LTGRAY);
        bgPaint.setStyle(Paint.Style.FILL);

        Rect dst = new Rect(getLeft(), getTop(), (int)(pageWidth), (int)(pageHeight));
        if (prescriptionBg != null) {
            canvas.drawBitmap(prescriptionBg, null,dst, new Paint());
        }


        if (previousDrawPath != null){
            canvas.drawPath(previousDrawPath, paint);
        }
        if (currentLivePath != null){
            canvas.drawPath(currentLivePath, paint);
        }

        Log.i("Canvas", "Scaled to " + scaleFactor);

    }


    float currentLiveX  =0f;
    float currentLiveY = 0f;
    public void addCoordinate(float x, float y, int actionType) {
        Log.i("Optimiz", "Add Co-ordinate called");
        if (currentLivePath == null)
            currentLivePath = new Path();

        Log.i("drawingCoordinate", "x: " + x + " y: " + y + " actionType: " + actionType);
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
        previousDrawPath = null;
        currentLivePath = null;
        invalidate();
    }

    //Todo: Shift Both Functions to BG Thread to generate a bmp and send that back to our thread
    public void addCoordinates(ArrayList<Point> points) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
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


}

