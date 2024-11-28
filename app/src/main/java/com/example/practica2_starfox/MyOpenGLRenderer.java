package com.example.practica2_starfox;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

public class MyOpenGLRenderer implements Renderer {

	private final Context context;

	private Object3D starwing;

	private Light light;

	private int width;
	private int height;

	private int frame;

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public MyOpenGLRenderer(Context context){
		this.context = context;
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Image Background color
		gl.glClearColor(0.2f, 0.3f, 0.4f, 0.5f);

		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_NORMALIZE);

		light = new Light(gl, GL10.GL_LIGHT0);
		light.setPosition(new float[] {2, 5, 5, 0});

		light.setAmbientColor(new float[]{0.3f, 0.3f, 0.1f});
		light.setDiffuseColor(new float[]{1f, 1f, 1f});

		starwing = new Object3D(context, R.raw.starwing);
	}

	private float star_y;
	private float star_x;
	private boolean starship_idle;
	private float hover;
	private float state;

	public void setStarY(float y) {
		if(y > 6.3f)
			this.star_y = 6.3f;
		else this.star_y = Math.max(y, -6f);
	}
	public void setStarX(float x) {
		if(x > 2.8f)
			this.star_x = 2.8f;
		else this.star_x = Math.max(x, -2.8f);
	}

	public void setStarIdle(boolean idle) { this.starship_idle = idle; }
	public boolean getStarIdle() { return this.starship_idle; }

	public float getStarY() { return this.star_y; }
	public float getStarX() { return this.star_x; }

	private float inclination;
	public void setInclination(float inclination) { this.inclination = inclination; }

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		setPerspectiveProjection(gl);

		GLU.gluLookAt(gl, 0, 0.8f, 15, 0, 0, 0, 0, 1, 0);

		gl.glPushMatrix();

		if(starship_idle) {
			state += 0.05f;
			hover = (float)Math.sin(state) * 0.08f;
			gl.glTranslatef(star_x, -star_y + hover, 0);
		}
		else {
			state = 0;
			gl.glTranslatef(star_x, -star_y, 0);
		}

		gl.glRotatef(-this.inclination,0,0,1);
		gl.glRotatef(180,0, 1, 0 );
		starwing.draw(gl);
		gl.glPopMatrix();
	}

	private void setPerspectiveProjection(GL10 gl) {
		gl.glClearDepthf(1.0f);            // Set depth's clear-value to farthest
		gl.glEnable(GL10.GL_DEPTH_TEST);   // Enables depth-buffer for hidden surface removal
		gl.glDepthFunc(GL10.GL_LEQUAL);    // The type of depth testing to do
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);  // nice perspective view
		gl.glShadeModel(GL10.GL_SMOOTH);   // Enable smooth shading of color
		gl.glDisable(GL10.GL_DITHER);      // Disable dithering for better performance
		gl.glDepthMask(true);  // disable writes to Z-Buffer

		gl.glMatrixMode(GL10.GL_PROJECTION); // Select projection matrix
		gl.glLoadIdentity();                 // Reset projection matrix

		// Use perspective projection
		GLU.gluPerspective(gl, 60, (float) width / height, 0.1f, 100.f);

		gl.glMatrixMode(GL10.GL_MODELVIEW);  // Select model-view matrix
		gl.glLoadIdentity();                 // Reset
	}

	private void setOrthographicProjection(GL10 gl){
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(-5,5,-4,4,-5,5);
		gl.glDepthMask(false);  // disable writes to Z-Buffer
		gl.glDisable(GL10.GL_DEPTH_TEST);  // disable depth-testing

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	private float aspect;
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// Define the Viewport
		this.width=width;
		this.height=height;

		gl.glViewport(0, 0, width, height);
	}
}
