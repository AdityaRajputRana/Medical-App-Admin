package com.example.medicalappadmin.canvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class NotepadView extends View {
    private Paint paint;

    private List<PointF> coordinates;

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
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (PointF point : coordinates) {
            canvas.drawPoint(point.x, point.y, paint);
        }
    }

    public void addCoordinate(float x, float y) {
        PointF point = new PointF(x,y);
        coordinates.add(point);
        invalidate(); // Request a redraw of the View
    }

    public void clearDrawing() {
        coordinates.clear();
        invalidate();
    }
}

