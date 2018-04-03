package com.example.simplegraphics;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

public class PanAndZoomListener implements OnTouchListener {

    private static final String TAG = "PanAndZoomListener";
    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // Remember some things for zooming
    PointF start = new PointF();
    float oldDist = 1f;

    PanZoomCalculator panZoomCalculator;

    public PanAndZoomListener(FrameLayout container, View view) {
        panZoomCalculator = new PanZoomCalculator(container, view);
    }

    public boolean onTouch(View view, MotionEvent event) {
        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                start.set(event.getX(), event.getY());

                mode = DRAG;
                Log.d(TAG, "mode=DRAG");

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM");
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                Log.d(TAG, "mode=NONE");

                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    float dx = event.getX() - start.x;
                    float dy = event.getY() - start.y;

                    Log.d(TAG, "PAN dx= " + dx + ", dy= " + dy);

                    panZoomCalculator.doPan(dx, dy);

                    start.set(event.getX(), event.getY());
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);

                    if (newDist > 10f) {
                        float scale = newDist / oldDist;
                        oldDist = newDist;

                        Log.d(TAG, "ZOOM scale= " + scale);

                        panZoomCalculator.doZoom(scale);
                    }
                }

                break;
        }

        return true;
    }

    // Determine the space between the first two fingers
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);

        return (float) Math.sqrt(x * x + y * y);
    }

    public class PanZoomCalculator {
        private static final String TAG = "PanZoomCalculator";

        // The current pan position
        PointF currentPan;
        // The current zoom scale
        float currentZoom;

        View container;
        View child;

        PanZoomCalculator(View container, View child) {
            this.container = container;
            this.child = child;

            // Initialize class fields
            reset();
        }

        // Call this to reset the Pan/Zoom state machine
        public void reset() {
            // Reset zoom and pan
            currentZoom = 1;
            currentPan = new PointF(0f, 0f);

            onPanZoomChanged();
        }

        public void doZoom(float scale) {
            currentZoom = scale;

            currentPan.x = 0;
            currentPan.y = 0;

            onPanZoomChanged();
        }

        public void doPan(float panDx, float panDy) {
            currentZoom = 1;

            currentPan.x = panDx;
            currentPan.y = panDy;

            onPanZoomChanged();
        }

        public void onPanZoomChanged() {
            //just in case ...
            //float winWidth = container.getWidth();
            //float winHeight = container.getHeight();

            ViewGroup.LayoutParams lp = child.getLayoutParams();

            int newWidth = (int) (lp.width * currentZoom);
            int newHeight = (int) (lp.height * currentZoom);

            if (newWidth >= 10 && newHeight >= 10 && newWidth <= 1000 && newHeight <= 1000) {
                float oldX = child.getX();
                float oldY = child.getY();

                // shift by Zoom

                int dx = (int) (lp.width - newWidth) / 2;
                int dy = (int) (lp.height - newHeight) / 2;

                float newX= oldX + dx;
                float newY = oldY + dy;

                // shift by Pan

                newX = newX + currentPan.x;
                newY = newY + currentPan.y;

                // set new position

                if (newX != oldX || newY != oldY) {
                    child.setX(newX);
                    child.setY(newY);
                }

                // set new dimensions

                if (newWidth != lp.width || newHeight != lp.height) {
                    lp.width = newWidth;
                    lp.height = newHeight;

                    child.setLayoutParams(lp);
                }

                Log.d(TAG, "x = " + newX + ", y = " + newY + ", width = " + lp.width + ", height = " + lp.height);
            }
        }
    }
}

