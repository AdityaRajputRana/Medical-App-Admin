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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.medicalappadmin.Models.Page;
import com.example.medicalappadmin.Models.Point;
import com.example.medicalappadmin.PenDriver.LiveData.DrawLiveDataBuffer;
import com.example.medicalappadmin.R;
import com.example.medicalappadmin.Tools.BitmapUtils;
import com.example.medicalappadmin.Tools.CacheUtils;
import com.example.medicalappadmin.rest.api.API;
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.response.ConfigurePageRP;
import com.google.gson.Gson;

import java.util.ArrayList;

public class NotepadView extends View {
    private int currentPageNumber = -1;
    private Bitmap cachedBmp;
    private static final long DEBOUNCE_DELAY = 1000;
    private Handler debounceHandler = new Handler();
    private Runnable saveRunnable;

    private Paint paint;
    float scaleFactor = 1f;
    ScaleGestureDetector scaleGestureDetector;
    GestureDetector gestureDetector;
    private float translateX = 0;
    private float translateY = 0;
    private Bitmap prescriptionBg;

    private int pageHeight ;
    private int pageWidth;


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
        this.setDrawingCacheEnabled(true);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
    }
    public void setBackgroundImageUrl(String imageUrl,int width, int height) {
        loadImage(imageUrl);
        pageHeight = height;
        pageWidth = width;
        this.post(new Runnable() {
            @Override
            public void run() {
                scaleFactor = Math.min((float)getWidth()/width, (float)getHeight()/height);
                Log.i("Scale factor is set to:", String.valueOf(scaleFactor ));
                paint.setStrokeWidth(4f/scaleFactor);
                redraw();
            }
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
                        redraw(); // Redraw the view
                    }
                });
    }


    private Bitmap getPrescriptionBMP(){
        Drawable d = getContext().getDrawable(R.drawable.bg_prescription);
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        return bitmap;
    }

    public Bitmap getCurrentBitmap(){
        int width = getWidth();
        int height = getHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        draw(canvas);

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
        } else if (cachedBmp != null){
            Rect cacheDst = new Rect(getLeft(), getTop(), (int) (getWidth()/scaleFactor), (int) (getHeight()/scaleFactor));
            canvas.drawBitmap(cachedBmp, null,cacheDst, new Paint());
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
            redraw();
        });
    }

    public void clearDrawing(int pageNumber){
        clearDrawing(pageNumber, false, false);
    }

    public void clearDrawing(int pageNumber, boolean loadPoints, boolean forceRefreshPoints) {
        if (saveRunnable != null){
            debounceHandler.removeCallbacks(saveRunnable);
            saveRunnable = null;
            saveBitmapToStorage(getCurrentBitmap());
        }
        previousDrawPath = null;
        currentLivePath = null;
        cachedBmp = null;
        currentPageNumber = pageNumber;
        configurePage();
        loadCacheImage(loadPoints, forceRefreshPoints);
        invalidate();
    }

    private void configurePage() {
        APIMethods.configurePageForceCache(getContext(), new APIResponseListener<ConfigurePageRP>() {
            @Override
            public void success(ConfigurePageRP response) {
                setBackgroundImageUrl(response.getPageDetails().getPageBackground(), response.getPageDetails().getPageWidth(), response.getPageDetails().getPageHeight());
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                Toast.makeText(getContext(), "Error while configuring page", Toast.LENGTH_SHORT).show();
            }
        });
    }

    boolean loadCacheImage(boolean loadPoints, boolean forceRefreshPoints) {
        if (previousDrawPath != null || cachedBmp != null || currentPageNumber == -1) return false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap mBitmap = CacheUtils.loadCanvasBitmapFromStorage(getContext(), currentPageNumber);
                cachedBmp = mBitmap;
                if (cachedBmp != null){
                    invalidate();
                }
                if (loadPoints && (forceRefreshPoints || cachedBmp == null)){
                    downloadPoints();
                }
            }
        }).start();

        return true;
    }

    private void downloadPoints() {
        final int tempPage = currentPageNumber;
        APIMethods.getPage(getContext(), tempPage, new APIResponseListener<Page>() {
            @Override
            public void success(Page response) {
                if (currentPageNumber == tempPage){
                    addCoordinates(response.getPoints());
                }
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                Toast.makeText(getContext(), "Some error occurred while loading page: " + tempPage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveBitmapToStorage(Bitmap bitmap) {
        Log.i("Cache: ", "Save Called");
        if (currentPageNumber == -1) return;
        Log.i("Cache: ", "Page is not -1");
        CacheUtils.saveCanvasBitmap(getContext(), bitmap, currentPageNumber);
    }

    //Todo: Shift Both Functions to BG Thread to generate a bmp and send that back to our thread
    public void addCoordinates(ArrayList<Point> points) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            Path path = createPathFromPoints(points);

            mainHandler.post(() -> {
                previousDrawPath = path;
                redraw();
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


    private void redraw(){
        //It invalidates as well as caches the image.
        invalidate();
        if (previousDrawPath != null || currentLivePath != null) {
            if (saveRunnable != null) {
                debounceHandler.removeCallbacks(saveRunnable);
            }
            saveRunnable = new Runnable() {
                @Override
                public void run() {
                    saveRunnable = null;
                    saveBitmapToStorage(getCurrentBitmap());
                }
            };
            debounceHandler.postDelayed(saveRunnable, DEBOUNCE_DELAY);
        }
    }


}

