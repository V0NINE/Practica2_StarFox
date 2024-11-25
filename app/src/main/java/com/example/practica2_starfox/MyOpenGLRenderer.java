package com.example.practica2_starfox;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

public class MyOpenGLRenderer implements Renderer {

	// Ratio of the screen
	private static final int WIDE_RATIO = 5;
	private static final int HEIGHT_RATIO = 4;

	private final Context context;

	private int frame;

	private HUDTexture hud_lives;
	private HUDTexture lives_count;
	private HUDTexture hud_shield;

	float lives_x_scale;
	float lives_y_scale;

	float lives_count_x_scale;
	float lives_count_y_scale;

	float shield_x_scale;
	float shield_y_scale;

	private int width;
	private int height;

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

		hud_lives = new HUDTexture();
		lives_count = new HUDTexture();
		hud_shield = new HUDTexture();

		hud_lives.loadTexture(gl, context, R.raw.hud_lives);
		lives_count.loadTexture(gl, context, R.raw.lives);
		hud_shield.loadTexture(gl, context, R.raw.hud_shield);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		frame++;

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		setOrthographicProjection(gl);

		gl.glPushMatrix();
		gl.glBindTexture(GL10.GL_TEXTURE_2D, hud_lives.getTextureID());
		gl.glTranslatef((lives_x_scale - WIDE_RATIO),-(lives_y_scale - HEIGHT_RATIO),0);
		gl.glScalef(lives_x_scale, lives_y_scale, 0);
		hud_lives.draw(gl);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glBindTexture(GL10.GL_TEXTURE_2D, hud_shield.getTextureID());
		gl.glTranslatef((shield_x_scale - WIDE_RATIO) + 0.04f,(shield_y_scale - HEIGHT_RATIO) + 0.06f,0);
		gl.glScalef(shield_x_scale, shield_y_scale, 0);
		hud_shield.draw(gl);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glBindTexture(GL10.GL_TEXTURE_2D, lives_count.getTextureID());
		gl.glTranslatef((lives_count_x_scale + (float)(1.77 * (1080 / (float)getWidth())) - WIDE_RATIO),-(lives_count_y_scale + (float)(0.061 * (2285 / (float)getHeight())) - HEIGHT_RATIO),0);
		gl.glScalef(lives_count_x_scale, lives_count_y_scale, 0);
		lives_count.draw(gl);
		gl.glPopMatrix();

		if (frame%120 == 0)
			setLife();
	}

	private int left = 0;
	private int right = 0;

	public void setLife() {
		left = (left+1)%3;
		right = (right+1)%3;

		System.out.println("caca");
		lives_count.setTextureCoords(left,right+1);
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

		gl.glEnable(GL10.GL_BLEND);  // enables image transparency
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);  // Indicates the color on alpha channel

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// Define the Viewport
		this.width=width;
		this.height=height;

		setHUDScales();  // Maintains the same HUD scale on all screen sizes

		gl.glViewport(0, 0, width, height);
	}

	private void setHUDScales() {
		lives_x_scale = (float)(1.2 * (1080 / (float)getWidth()));
		lives_y_scale = (float)(0.15 * (2285 / (float)getHeight()));

		lives_count_x_scale = (float)(0.3 * (1080 / (float)getWidth()));
		lives_count_y_scale = (float)(0.09 * (2285 / (float)getHeight()));

		shield_x_scale = (float)(1.7 * (1080 / (float)getWidth()));
		shield_y_scale = (float)(0.13 * (2285 / (float)getHeight()));
	}
}
