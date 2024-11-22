package com.example.practica2_starfox;

import android.os.Bundle;
import android.app.Activity;
import android.opengl.GLSurfaceView;

public class MainActivity extends Activity {

    MyOpenGLRenderer myGLRenderer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GLSurfaceView view = new GLSurfaceView(this);
        view.setRenderer(myGLRenderer = new MyOpenGLRenderer(this));
        setContentView(view);
    }
}