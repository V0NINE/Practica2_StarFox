package com.example.practica2_starfox;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

public class MyOpenGLRenderer implements Renderer {

	public static final int ORIGINAL_SCREEN_WIDTH = 1080;
	public static final int ORIGINAL_SCREEN_HEIGHT = 2285;

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
	private TextureCube mountains;
	private TextureCube clouds;
	private Starwing starwing;

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
		gl.glClearColor(0.2f, 0.3f, 0.4f, 0.5f);

		background = new TextureCube();
		background.loadTexture(gl, context, R.raw.fondo);

		mountains = new TextureCube();
		mountains.loadTexture(gl, context, R.raw.muntanyes);

		clouds = new TextureCube();
		clouds.loadTexture(gl, context, R.raw.nubols);

		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_NORMALIZE);

		light = new Light(gl, GL10.GL_LIGHT0);
		light.setPosition(new float[] {2, 5, 5, 0});

		light.setAmbientColor(new float[]{0.3f, 0.3f, 0.1f});
		light.setDiffuseColor(new float[]{1f, 1f, 1f});

		starwing = new Starwing(context, R.raw.starwing);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// Initial settings
		setPerspectiveProjection(gl);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		GLU.gluLookAt(gl, starwing.getStarX()/6f, -starwing.getStarY()/5, 10,
					      starwing.getStarX()/6f, -starwing.getStarY()/5, 0,
				          starwing.getLateralInclination()/600, 1f, 0f);
		//GLU.gluLookAt(gl,20,10,6,5,0,0,0,1,0); maybe vigilance camera mode

		// Background block
		gl.glDisable(GL10.GL_LIGHTING);

		drawBackgroundComponent(gl, background, new float[]{55,22,1}, new float[]{0,0.5f,-26});
		drawBackgroundComponent(gl, clouds, new float[]{45,5,1}, new float[]{0,0.55f,-22});
		drawBackgroundComponent(gl, mountains, new float[]{45,25,1}, new float[]{0,0.15f,-15});

		gl.glEnable(GL10.GL_LIGHTING);

		// Starwing block
		gl.glPushMatrix();

		//gl.glTranslatef(0,0, 35);

		if(starwing.getStarIdle()) {
			gl.glTranslatef(starwing.getStarX(), -starwing.getVerticalHover(), 0);
		}
		else {
			gl.glTranslatef(starwing.getStarX(), -starwing.getStarY(), 0);
		}

		gl.glRotatef(-starwing.getVerticalInclination(),1,0,0);
		gl.glRotatef(-starwing.getLateralInclination(),0,0,1);
		gl.glRotatef(180,0, 1, 0 );
		starwing.draw(gl);
		gl.glPopMatrix();
	}

	private void drawBackgroundComponent(GL10 gl, TextureCube component, float[] scale, float[] translation) {
		gl.glPushMatrix();
		gl.glBindTexture(GL10.GL_TEXTURE_2D, component.getTextureID());
		gl.glScalef(scale[0],scale[1],scale[2]);
		gl.glTranslatef(translation[0],translation[1],translation[2]);
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

		gl.glEnable(GL10.GL_BLEND);  // enables image transparency
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);  // Indicates the color on alpha channel

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

	public Starwing getStarwing() {
		return this.starwing;
	}
}
