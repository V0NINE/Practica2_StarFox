package com.example.practica2_starfox;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class WhiteDots extends Square{

    private static final int COLUMNS = 13;
    private static final int ROWS = 5;
    private static final float START_Z = -50f;
    private static final float CAMERA_Z = 10;
    private static final float START_X = -40;
    private static final float FLOOR_Y = -6.3f;
    private static final float DOTS_VELOCITY = 0.4f;

    private static List<float[]> white_dots;

    public WhiteDots() {
        super(new float[] {
                -0.1f, -0.1f, 0.0f,
                -0.1f,  0.1f, 0.0f,
                0.1f,  0.1f, 0.0f,
                0.1f, -0.1f, 0.0f
        });

        white_dots = new ArrayList<>();
        initializeWhiteDots();
    }

    private void initializeWhiteDots() {
        white_dots.clear();

        for(int row = 0; row < ROWS; row++) {
            for(int column = 0; column < COLUMNS; column++) {
                white_dots.add(new float[] {START_X + column*8, FLOOR_Y, START_Z+row*10});
            }
        }
    }

    public void drawDots(GL10 gl) {
        for(float[] dot : white_dots) {
            gl.glPushMatrix();
            gl.glTranslatef(dot[0], dot[1], dot[2]);
            draw(gl);
            gl.glPopMatrix();
        }

        updateWhiteDots();
    }

    public void updateWhiteDots() {
        for(float[] dot : white_dots) {
            dot[2] += DOTS_VELOCITY;
            if(dot[2] > CAMERA_Z)
                dot[2] = START_Z;
        }
    }
}