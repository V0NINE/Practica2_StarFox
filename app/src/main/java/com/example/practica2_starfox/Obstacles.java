package com.example.practica2_starfox;

import android.content.Context;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class Obstacles {

    private static final int START_Z = -79;
    private static final int FLOOR_Y = -9;
    private static final int MIN_X = -30;
    private static final int MAX_X = 30;
    private static final int NUM_OBSTACLES = 10;

    private final PillarDaemon[] obstacles;

    private Random random = new Random();
    private int activate;

    public Obstacles(Context ctx, int filenameId) {
        obstacles = new PillarDaemon[NUM_OBSTACLES];

        for(int i = 0; i < NUM_OBSTACLES; i++) {
            obstacles[i] = new PillarDaemon(ctx,filenameId);
            obstacles[i].setPosition(new float[] {getStartX(), FLOOR_Y, START_Z});
            obstacles[i].setCoolDown(random.nextInt(800-100)+100);
        }
    }

    public void update(GL10 gl) {
        for(PillarDaemon obstacle : obstacles) {
            obstacle.setZ(obstacle.getZ()+obstacle.getVelocity());

            if(obstacle.getZ() > 10f) {
                obstacle.setVelocity(0);
                obstacle.setZ(START_Z);
                obstacle.setCoolDown(random.nextInt(800-300)+300);
            }

            obstacle.setCoolDown(obstacle.getCoolDown()-1);
            if(obstacle.getCoolDown()==0) {
                obstacle.setVelocity(0.3f);
                obstacle.setX(getStartX());
            }

            gl.glPushMatrix();
            gl.glTranslatef(obstacle.getX(), obstacle.getY(), obstacle.getZ());
            obstacle.draw(gl);
            gl.glPopMatrix();
        }

    }

    public float getStartX() {
        return random.nextInt(MAX_X - MIN_X+1) + MIN_X;
    }
}
