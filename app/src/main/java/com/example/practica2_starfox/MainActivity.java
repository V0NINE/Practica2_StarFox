package com.example.practica2_starfox;

import android.os.Bundle;
import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MainActivity extends Activity {

    MyOpenGLRenderer myGLRenderer;
    float previous_x, previous_y;

    float regionLeft;
    float regionTop;
    float regionRight;
    float regionBottom;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GLSurfaceView view = new GLSurfaceView(this);
        view.setRenderer(myGLRenderer = new MyOpenGLRenderer(this));
        setContentView(view);


        this.regionLeft = 2300-130;
        this.regionTop = 0;
        this.regionRight = 2300;
        this.regionBottom = 170;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();

        if(myGLRenderer.getStarwing().getStarIdle())
            myGLRenderer.getStarwing().setStarIdle(false);

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isTouchInRegion(x, y))
                    myGLRenderer.vigliance_camera = !myGLRenderer.vigliance_camera;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = x - previous_x;
                float dy = y - previous_y;

                myGLRenderer.getStarwing().setStarY(myGLRenderer.getStarwing().getStarY() + dy/80);
                myGLRenderer.getStarwing().setStarX(myGLRenderer.getStarwing().getStarX() + dx/80);

                myGLRenderer.getStarwing().setLateralInclination(dx/3);
                myGLRenderer.getStarwing().setVerticalInclination(dy/2);

                break;
            case MotionEvent.ACTION_UP:
                myGLRenderer.getStarwing().setLateralInclination(0);
                myGLRenderer.getStarwing().setVerticalInclination(0);
                myGLRenderer.getStarwing().setStarIdle(true);
                break;
        }

        previous_x = x;
        previous_y = y;

        return true;
    }

    private boolean isTouchInRegion(float x, float y) {
        return x >= regionLeft && x <= regionRight && y >= regionTop && y <= regionBottom;
    }

}