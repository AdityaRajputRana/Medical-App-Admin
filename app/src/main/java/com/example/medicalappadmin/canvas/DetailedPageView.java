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
    }
    public void setBackgroundImageUrl(String imageUrl,int width, int height) {
        loadImage(imageUrl);
        pageHeight = height;
        pageWidth = width;

        scaleFactor = Math.min((float)getWidth()/width, (float)getHeight()/height);
        paint.setStrokeWidth(4f/scaleFactor);

        Log.i("ScaleFactor", "width: " + getWidth() + " height: " + getHeight() + " pageWidth: " + pageWidth + " pageHeight: " + pageHeight);
        Log.i("ScaleFactor", "scaleFactor: " + scaleFactor);
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

        if (previousDrawPath != null) {
            Log.i("Drawing", "Drawing previous path");
            canvas.drawPath(previousDrawPath, paint);
        }

        Log.i("Drawing", "Drawn Canvas");

    }






    public void clearDrawing() {
        previousDrawPath = null;
        invalidate();
    }

    Path previousDrawPath;
    public void addCoordinates(ArrayList<Point> points) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            Path path = createPathFromPoints(points);

            mainHandler.post(() -> {
                previousDrawPath = path;
                Log.i("Drawing", "Updating Previous Path, pathLength = ");
                invalidate();
            });

        }).start();
        Log.i("Adding Coordinates", "to Detailed Page View thread start");
    }
    private Path createPathFromPoints(ArrayList<Point> points){
        Log.i("Drawing", "Path Size: " + points.size());
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

