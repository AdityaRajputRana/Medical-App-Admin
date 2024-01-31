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
import com.example.medicalappadmin.Models.PageDetails;
import com.example.medicalappadmin.Models.Point;
import com.example.medicalappadmin.R;
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.response.ConfigurePageRP;

import java.util.ArrayList;

public class PageViewRV extends View {
    private Paint paint;
    float scaleFactor = 1f;
    private Bitmap prescriptionBg;
    private float pageHeight, pageWidth;



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

        startDynamicConfigFetch();
    }

    private void startDynamicConfigFetch() {
        APIMethods.configurePageForceCache(getContext(), new APIResponseListener<ConfigurePageRP>() {
            @Override
            public void success(ConfigurePageRP response) {
                if (response.getPageDetails() != null) {
                    configurePageBG(response.getPageDetails());
                }
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {

            }
        });
    }

    private void configurePageBG(PageDetails pageDetails) {

        this.post(()->{
            loadImage(pageDetails.getPageBackground());
            pageWidth = pageDetails.getPageWidth();
            pageHeight = pageDetails.getPageHeight();
            scaleFactor = Math.min((float) getWidth()/pageDetails.getPageWidth(), (float) getHeight()/pageDetails.getPageHeight());
            paint.setStrokeWidth(2f/scaleFactor);
            invalidate();
        });
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


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.scale(scaleFactor, scaleFactor);

        Log.i("ScaleFactor", "Called On Draw, scalefactor = " + scaleFactor);

        Paint bgPaint = new Paint();
        bgPaint.setColor(Color.LTGRAY);
        bgPaint.setStyle(Paint.Style.FILL);

        Rect dst = new Rect(getLeft(), getRight(), (int)(pageWidth), (int)(pageHeight));
        if (prescriptionBg != null) {
            Log.i("ScaleFactor", "Drawing BG" + getLeft() + " " + getRight() + " " + pageWidth + " " + pageHeight);
            canvas.drawBitmap(prescriptionBg, null,dst, new Paint());
        } else {
            canvas.drawRect(dst, bgPaint);
        }

        if (previousDrawPath != null) {
            Log.i("ScaleFactor", "Called On Draw previousDrawPath: ");
            canvas.drawPath(previousDrawPath, paint);
        }

    }




    public void clearDrawing() {
        previousDrawPath = null;
        invalidate();
    }

    public void addCoordinates(ArrayList<Point> points) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            Path path = createPathFromPoints(points);
            mainHandler.post(() -> {
                previousDrawPath = path;
                invalidate();
            });

        }).start();
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
    private Path previousDrawPath;

}

