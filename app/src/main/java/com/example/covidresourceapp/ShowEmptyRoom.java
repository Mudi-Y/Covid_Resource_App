package com.example.covidresourceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class ShowEmptyRoom extends AppCompatActivity {

    public static int length = VisualPlannerActivity.getLength();
    public static int width = VisualPlannerActivity.getWidth();

    int length_factor = (int) 1750 / length;
    int width_factor = (int) 1000 / width;

    int factor = Math.min(length_factor, width_factor);
    int length_scaled = length * factor;
    int width_scaled = width * factor;

    int radius = 6 * (factor/2);
    int count = 0;
    private List<Point> circlePoints;
    boolean social_dist = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new DrawingView(this));
        circlePoints = new ArrayList<Point>();
    }

    class DrawingView extends SurfaceView {

        private final SurfaceHolder surfaceHolder;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint not_dist = new Paint(Paint.ANTI_ALIAS_FLAG);

        public DrawingView(Context context) {
            super(context);
            surfaceHolder = getHolder();
            paint.setColor(Color.GREEN);
            paint.setStyle(Paint.Style.FILL);

            paint1.setColor(Color.BLACK);
            paint1.setStrokeWidth(3);
            paint1.setStyle(Paint.Style.STROKE);

        }

        //returns cartesian distance between points (x1, y1) and (x2, y2)
        int dist_formula (int x1, int y1, int x2, int y2){
            return (int) Math.round(Math.pow(Math.pow((x1 - x2),2) + Math.pow((y1 - y2),2), 0.5));
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            Canvas canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.WHITE);

            int canvasW = getWidth();
            int canvasH = getHeight();
            Point centerOfCanvas = new Point(canvasW / 2, canvasH / 2);
            int rectW = width_scaled;
            int rectH = length_scaled;
            int left = centerOfCanvas.x - (rectW / 2);
            int top = centerOfCanvas.y - (rectH / 2);
            int right = centerOfCanvas.x + (rectW / 2);
            int bottom = centerOfCanvas.y + (rectH / 2);
            Rect rect = new Rect(left, top, right, bottom);
            canvas.drawRect(rect, paint1);


            if (count > 2) {
                //get coordinates for the new circle
                int touchX = Math.round(event.getX());
                int touchY = Math.round(event.getY());

                //draw all previous circles, and calculate distance to the current circle
                //if a point is not 6 feet away, change bool to false
                social_dist = true;

                for (Point p : circlePoints) {
                    canvas.drawCircle(p.x, p.y, radius, paint);

                    int dist = dist_formula(touchX, touchY, p.x, p.y);

                    if (dist < radius){
                        social_dist = false;
                    }
                }

                //draw circle and add new circle center to list of circle centers if it is socially distant
                if (social_dist) {
                    canvas.drawCircle(touchX, touchY, radius, paint);
                    circlePoints.add(new Point(touchX, touchY));
                }

            }

            surfaceHolder.unlockCanvasAndPost(canvas);
            count++;
            return true;
        }
    }
}