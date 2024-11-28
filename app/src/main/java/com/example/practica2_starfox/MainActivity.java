package com.example.practica2_starfox;

import android.os.Bundle;
import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MainActivity extends Activity {

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

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - previous_x;
                float dy = y - previous_y;

                System.out.println("Pipero " + myGLRenderer.getStarY() + " - " + myGLRenderer.getStarX());

                //myGLRenderer.setStarY(myGLRenderer.getStarY() + ((dy>0)?-0.1f:0.1f));
                myGLRenderer.setStarY(myGLRenderer.getStarY() + dy/130);
                myGLRenderer.setStarX(myGLRenderer.getStarX() + dx/130);
        }

        previous_x = x;
        previous_y = y;

        return true;
    }
}