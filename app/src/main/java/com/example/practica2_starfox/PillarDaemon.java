package com.example.practica2_starfox;
import android.content.Context;

public class PillarDaemon extends Object3D{

    private float[] position;
    private float velocity = 0;
    private int cool_down;

    public PillarDaemon(Context ctx, int filenameId) {
        super(ctx, filenameId);

        position = new float[3];
    }

    public void setPosition(float[] position) {
        this.position[0] = position[0];
        this.position[1] = position[1];
        this.position[2] = position[2];
    }

    public void setZ(float z) { this.position[2] = z; }
    public void setX(float x) { this.position[0] = x; }

    public void setVelocity(float velocity) { this.velocity = velocity; }

    public void setCoolDown(int cool_down) { this.cool_down = cool_down; }

    public float getX() { return position[0]; }
    public float getY() { return position[1]; }
    public float getZ() { return position[2]; }

    public float getVelocity() { return velocity; }

    public int getCoolDown() { return cool_down; }
}
