package com.example.practica2_starfox;

import android.os.Bundle;
import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.view.MotionEvent;

public class MainActivity extends Activity {

    private static final int IDLE_TIMEOUT = 300;
    private Handler idle_handler = new Handler();
    private Runnable idle_runnable = new Runnable() {
        @Override
        public void run() {
            System.out.println("Pipero master");
            myGLRenderer.setStarIdle(true);
        }
    };

    MyOpenGLRenderer myGLRenderer;
    float previous_x, previous_y;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GLSurfaceView view = new GLSurfaceView(this);
        view.setRenderer(myGLRenderer = new MyOpenGLRenderer(this));
        setContentView(view);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();

        idle_handler.removeCallbacks(idle_runnable);
        if(myGLRenderer.getStarIdle())
            myGLRenderer.setStarIdle(false);

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - previous_x;
                float dy = y - previous_y;

                //System.out.println("Pipero " + myGLRenderer.getStarY() + " - " + myGLRenderer.getStarX());
                System.out.println("Pipero " + dx);

                //myGLRenderer.setStarY(myGLRenderer.getStarY() + ((dy>0)?-0.1f:0.1f));
                myGLRenderer.setStarY(myGLRenderer.getStarY() + dy/130);
                myGLRenderer.setStarX(myGLRenderer.getStarX() + dx/130);

                myGLRenderer.setInclination(dx/3);
        }

        previous_x = x;
        previous_y = y;

        idle_handler.postDelayed(idle_runnable, IDLE_TIMEOUT);

        return true;
    }
}