package com.example.practica2_starfox;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

import org.w3c.dom.Text;

public class MyOpenGLRenderer implements Renderer {

	public float getxCam() {
		return xCam;
	}

	public void setxCam(float xCam) {
		this.xCam = xCam;
		if (this.xCam > 0.6f)
			this.xCam = 0.6f;
		if(this.xCam < -0.6f)
			this.xCam = -0.6f;
	}

	private float xCam = 0;

	public float getyCam() {
		return yCam;
	}

	public void setyCam(float yCam) {
		this.yCam = yCam;
		if (this.yCam > 0.8f)
			this.yCam = 0.8f;
		if(this.yCam < -0.6f)
			this.yCam = -0.6f;
	}

	private float yCam = 0;

	private final Context context;

	private TextureCube background;

	private Object3D spacexip;
	private Light light;

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
		gl.glClearColor(1f, 0.3f, 0.4f, 0.5f);

		background = new TextureCube();
		background.loadTexture(gl, context, R.raw.background_alt);

		spacexip = new Object3D(context, R.raw.starwing);

		gl.glEnable(GL10.GL_LIGHTING);

		light = new Light(gl, GL10.GL_LIGHT0);
		light.setPosition(new float[]{0, 5, 15, 0.0f});

		light.setAmbientColor(new float[]{1f, 1f, 1f});
		light.setDiffuseColor(new float[]{1, 1, 1});

	}

	float y_ship = yCam;

	@Override
	public void onDrawFrame(GL10 gl) {


		System.out.println("CACA " + y_ship);
		setPerspectiveProjection(gl);

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		GLU.gluLookAt(gl, xCam, -yCam, 5, xCam, -yCam, 0, 0, 1, 0);
		//System.out.println("CACA " + xCam + " " + yCam);

		gl.glPushMatrix();
		gl.glTranslatef(0,1,-2);
		gl.glScalef(10,5,0);
		background.draw(gl);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslatef(xCam*3.5f,-yCam*3.5f,0);
		gl.glRotatef(180,0,1,0);
		spacexip.draw(gl);
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
