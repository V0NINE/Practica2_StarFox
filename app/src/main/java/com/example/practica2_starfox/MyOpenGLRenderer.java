package com.example.practica2_starfox;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

public class MyOpenGLRenderer implements Renderer {

	public static final int MAX_HORIZONTAL_OFFSET = 5;
	public static final int MAX_VERTICAL_OFFSET = 4;
	
	public static final int ORIGINAL_SCREEN_WIDTH = 1080;
	public static final int ORIGINAL_SCREEN_HEIGHT = 2285;

	private final Context context;

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

		float[] x = {0,1,1/3f,1,0,0,1/3f,0};

		hud_lives = new HUDTexture(1.2f, 0.15f);
		lives_count = new HUDTexture(0.3f, 0.09f, x);
		hud_shield = new HUDTexture(1.7f, 0.13f);

		hud_lives.loadTexture(gl, context, R.raw.hud_lives);
		lives_count.loadTexture(gl, context, R.raw.lives);
		hud_shield.loadTexture(gl, context, R.raw.hud_shield);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		setOrthographicProjection(gl);

		gl.glPushMatrix();
		gl.glBindTexture(GL10.GL_TEXTURE_2D, hud_lives.getTextureID());
		gl.glTranslatef(hud_lives.setLeft(), hud_lives.setTop(),0);
		gl.glScalef(hud_lives.getTextureWidth(), hud_lives.getTextureHeight(), 0);
		hud_lives.draw(gl);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glBindTexture(GL10.GL_TEXTURE_2D, hud_shield.getTextureID());
		gl.glTranslatef(hud_shield.setLeft() + 0.04f,hud_lives.setBottom() + 0.06f,0);
		gl.glScalef(hud_shield.getTextureWidth(), hud_shield.getTextureHeight(), 0);
		hud_shield.draw(gl);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glBindTexture(GL10.GL_TEXTURE_2D, lives_count.getTextureID());
		gl.glTranslatef(lives_count.setLeft() + (float)(1.77 * (1080 / (float)getWidth())), lives_count.setTop() - (float)(0.061 * (2285 / (float)getHeight())),0);
		gl.glScalef(lives_count.getTextureWidth(), lives_count.getTextureHeight(), 0);
		lives_count.draw(gl);
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

	// This function ensures that an object maintains the same pixel size regardless of the screen size.
	// Formula: x * (originalScreenSize / newScreenSize)
	// Where:
	//   - 'x' is the object's intended size for the original screen resolution.
	//   - 'originalScreenSize' is the reference screen size.
	//   - 'newScreenSize' is the current screen size.
	// The ratio (originalScreenSize / newScreenSize) is the scale factor applied to preserve the size.
	private void setHUDScales() {
		hud_lives.setTextureScale(getWidth(),getHeight());
		lives_count.setTextureScale(getWidth(),getHeight());
		hud_shield.setTextureScale(getWidth(),getHeight());
	}
}
