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
	private HUDTexture hud_turbo;

	private int width;
	private int height;

	private int global_frame;
	private int live_count_frame;

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

		hud_lives = new HUDTexture(1.2f, 0.15f);
		hud_lives.loadTexture(gl, context, R.raw.hud_lives);

		lives_count = new HUDTexture(0.25f, 0.09f, 3, 2);
		lives_count.setHorizontalOffset(1.82f);
		lives_count.setVerticalOffset(-0.06f);
		lives_count.setAnimation(new int[] {2,1,0});
		lives_count.loadTexture(gl, context, R.raw.lives);

		hud_shield = new HUDTexture(1.7f, 0.13f);
		hud_shield.setHorizontalOffset(0.1f);
		hud_shield.setVerticalOffset(0.04f);
		hud_shield.loadTexture(gl, context, R.raw.hud_shield);

		hud_turbo = new HUDTexture(1.7f, 0.13f);
		hud_turbo.setHorizontalOffset(0.1f);
		hud_turbo.setVerticalOffset(0.04f);
		hud_turbo.loadTexture(gl, context,R.raw.hud_turbo);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// Draw HUD
		setOrthographicProjection(gl);

		drawHUDComponent(gl, hud_lives, "top", "left", false);
		drawHUDComponent(gl, lives_count, "top", "left", true);
		drawHUDComponent(gl, hud_shield, "bottom", "left", true);
		drawHUDComponent(gl, hud_turbo, "bottom", "right", true);

		// Animations sector
		global_frame++;
		if (global_frame%3 == 0)
			animateHUD();
	}

	// This function is called 20 times per second
	private void animateHUD() {
		// Lives count animation
		live_count_frame = (live_count_frame+1)%20;
		if(live_count_frame == 0) 
			lives_count.animateTexture();

		// Turbo animation
	}

	private void drawHUDComponent(GL10 gl, HUDTexture component, String verticalSide, String horizontalSide, boolean offset) {
		gl.glPushMatrix();
		gl.glBindTexture(GL10.GL_TEXTURE_2D, component.getTextureID());

		if (offset)
			gl.glTranslatef(component.getHorizontalOffset(), component.getVerticalOffset(), 0);

		switch(verticalSide) {
			case "top":
				gl.glTranslatef(0, component.setTop(), 0);
				break;
			case "bottom":
				gl.glTranslatef(0, component.setBottom(), 0);
				break;
		}

		switch(horizontalSide) {
			case "left":
				gl.glTranslatef(component.setLeft(), 0, 0);
				break;
			case "right":
				gl.glTranslatef(component.setRight(), 0, 0);
				break;
		}

		gl.glScalef(component.getTextureWidth(), component.getTextureHeight(), 0);
		component.draw(gl);
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
		hud_lives.setTextureScales(getWidth(), getHeight());
		lives_count.setTextureScales(getWidth(), getHeight());
		hud_shield.setTextureScales(getWidth(), getHeight());
		hud_turbo.setTextureScales(getWidth(), getHeight());
	}
}