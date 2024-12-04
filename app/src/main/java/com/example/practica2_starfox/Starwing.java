package com.example.practica2_starfox;

import android.content.Context;
import java.lang.System;

public class Starwing extends Object3D{

    private float star_y;
    private float star_x;
    private float lateral_inclination;
    private float vertical_inclination;
    private boolean starship_idle;

    public Starwing(Context ctx, int filenameId) {
        super(ctx, filenameId);
    }

    public void setStarY(float y) {
        // -6.3 < star_y < 6.3
        this.star_y = Math.max(Math.min(y,6.3f),-6.3f);
    }
    public void setStarX(float x) {
        // -2.8 < star_x < 2.8
        this.star_x = Math.max(Math.min(x,2.8f),-2.8f);
    }

    public void setStarIdle(boolean idle) { this.starship_idle = idle; }
    public boolean getStarIdle() { return this.starship_idle; }

    public void setLateralInclination(float inclination) {
        // -25º < lateral_inclination < 25º
        this.lateral_inclination = Math.max(Math.min(inclination,25),-25);
    }
    public void setVerticalInclination(float inclination) {
        // -25º < vertical_inclination < 25º
        this.vertical_inclination = Math.max(Math.min(inclination,25),-25);
    }

    public float getStarY() { return this.star_y; }
    public float getStarX() { return this.star_x; }
    public float getLateralInclination() { return this.lateral_inclination; }
    public float getVerticalInclination() {return this.vertical_inclination;}

    public float getVerticalHover() { return this.star_y + (float)Math.sin(2*Math.PI*System.nanoTime() / 2000000000.0f) * 0.1f; }
}
