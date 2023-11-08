package com.example.medicalappadmin.canvas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.medicalappadmin.Models.Point;

import java.util.ArrayList;
import java.util.List;


public class NotepadView extends View {
    private Paint paint;
    double scaleFactor = 15.0;

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


    }

    public void addCoordinate(double x, double y) {
        coordinates.add(new Point(x, y));
        invalidate(); // Request a redraw of the View
    }

    public void clearDrawing() {
        coordinates.clear();
        invalidate();
    }
}

