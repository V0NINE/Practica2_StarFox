package com.example.practica2_starfox;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

import org.w3c.dom.Text;

public class MyOpenGLRenderer implements Renderer {

	private final Context context;

	private TextureCube hud_lives;
	private TextureCube hud_shield;

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

		gl.glEnable(gl.GL_BLEND);
		gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);


		hud_lives = new TextureCube();
		hud_shield = new TextureCube();

		hud_lives.loadTexture(gl, context, R.raw.hud_lives);
		hud_shield.loadTexture(gl, context, R.raw.hud_shield);
	}

	//float x_scale = (float)(1.2 * (1080 / (float)getWidth()));

	float lives_x_scale;
	float lives_y_scale;

	float shield_x_scale;
	float shield_y_scale;

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		setOrthographicProjection(gl);

		lives_x_scale = (float)(1.2 * (1080 / (float)getWidth()));
		lives_y_scale = (float)(0.13 * (2285 / (float)getHeight()));

		shield_x_scale = (float)(1.7 * (1080 / (float)getWidth()));
		shield_y_scale = (float)(0.13 * (2285 / (float)getHeight()));

		//System.out.println("H " + getHeight() + "W " + getWidth());

		gl.glPushMatrix();
		gl.glBindTexture(GL10.GL_TEXTURE_2D, hud_lives.getTextureID());
		gl.glTranslatef((lives_x_scale - 10/2),-(lives_y_scale -8/2),0);
		gl.glScalef(lives_x_scale, lives_y_scale, 0);
		hud_lives.draw(gl);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glBindTexture(GL10.GL_TEXTURE_2D, hud_shield.getTextureID());
		gl.glTranslatef((shield_x_scale - 10/2),(shield_y_scale -8/2),0);
		gl.glScalef(shield_x_scale, shield_y_scale, 0);
		hud_shield.draw(gl);
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

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// Define the Viewport
		this.width=width;
		this.height=height;

		gl.glViewport(0, 0, width, height);
	}
}
