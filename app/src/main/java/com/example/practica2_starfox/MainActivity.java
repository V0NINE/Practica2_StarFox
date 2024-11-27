package com.example.practica2_starfox;

import android.os.Bundle;
import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MainActivity extends Activity {

    MyOpenGLRenderer myGLRenderer;
    float previousX, previousY;

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

                float dx = x - previousX;
                float dy = y - previousY;

                /*// reverse direction of rotation above the mid-line
                if (y >  myGLRenderer.getHeight() / 2) {
                    dx = dx * -1;
                }

                // reverse direction of rotation to left of the mid-line
                if (x < myGLRenderer.getWidth() / 2) {
                    dy = dy * -1;
                }*/

                myGLRenderer.setxCam(myGLRenderer.getxCam()+((dx>0)?0.015f:-0.015f));
                myGLRenderer.setyCam(myGLRenderer.getyCam()+((dy>0)?0.015f:-0.015f));

        }

        previousX = x;
        previousY = y;
        return true;
    }

}