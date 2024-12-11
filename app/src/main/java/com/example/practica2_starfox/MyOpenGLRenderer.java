package com.example.practica2_starfox;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

public class MyOpenGLRenderer implements Renderer {

	public static final int MAX_HORIZONTAL_OFFSET = 5;
	public static final int MAX_VERTICAL_OFFSET = 4;

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

	private HUDTexture hud_lives;
	private HUDTexture lives_count;
	private HUDTexture hud_shield;
	private HUDTexture hud_turbo;

	private WhiteDots dot;

	private Light light;

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

		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_NORMALIZE);

		background = new TextureCube();
		background.loadTexture(gl, context, R.raw.fondo);

		mountains = new TextureCube();
		mountains.loadTexture(gl, context, R.raw.muntanyes);

		clouds = new TextureCube();
		clouds.loadTexture(gl, context, R.raw.nubols);

		light = new Light(gl, GL10.GL_LIGHT0);
		light.setPosition(new float[] {2, 5, 5, 0});

		light.setAmbientColor(new float[]{0.3f, 0.3f, 0.1f});
		light.setDiffuseColor(new float[]{1f, 1f, 1f});

		starwing = new Starwing(context, R.raw.starwing);

		hud_lives = new HUDTexture(0.55f, 0.25f);
		hud_lives.loadTexture(gl, context, R.raw.hud_lives);

		lives_count = new HUDTexture(0.12f, 0.15f, 3, 2);
		lives_count.setHorizontalOffset(0.823f);
		lives_count.setVerticalOffset(-0.09f);
		lives_count.setAnimation(new int[] {2,1,0});
		lives_count.loadTexture(gl, context, R.raw.lives);

		hud_shield = new HUDTexture(0.85f, 0.35f);
		hud_shield.setHorizontalOffset(0.04f);
		hud_shield.setVerticalOffset(0.08f);
		hud_shield.loadTexture(gl, context, R.raw.hud_shield);

		hud_turbo = new HUDTexture(0.85f, 0.35f);
		hud_turbo.setHorizontalOffset(-0.04f);
		hud_turbo.setVerticalOffset(0.08f);
		hud_turbo.loadTexture(gl, context,R.raw.hud_turbo);

		dot = new WhiteDots();
		dot.setColor(gl,1,1,1);
	}

	@Override
	public void onDrawFrame(GL10 gl) {

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		setPerspectiveProjection(gl);

		GLU.gluLookAt(gl, starwing.getStarX()/6f, -starwing.getStarY()/5, 10,
					      starwing.getStarX()/6f, -starwing.getStarY()/5, 0,
				          starwing.getLateralInclination()/600, 1f, 0f);
		//GLU.gluLookAt(gl,20,10,6,5,0,0,0,1,0); maybe vigilance camera mode

		// Starwing block
		gl.glPushMatrix();

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

		for(int i = -3; i < 4; i++) {
			gl.glPushMatrix();
			//gl.glTranslatef(i,0,ini_pos+acercamiento)
			gl.glTranslatef(i,-1,dot.getPosition());
			gl.glScalef(0.5f,0.5f,1);
			dot.draw(gl);
			gl.glPopMatrix();
		}

		gl.glDisable(GL10.GL_LIGHTING);

		// Background block
		drawBackgroundComponent(gl, background, new float[]{55,22,1}, new float[]{0,0.5f,-26});
		drawBackgroundComponent(gl, clouds, new float[]{45,5,1}, new float[]{0,0.55f,-22});
		drawBackgroundComponent(gl, mountains, new float[]{45,25,1}, new float[]{0,0.15f,-15});
		

		// HUD block
		setOrthographicProjection(gl);

		drawHUDComponent(gl, hud_lives, "top", "left", false);
		drawHUDComponent(gl, lives_count, "top", "left", true);
		drawHUDComponent(gl, hud_shield, "bottom", "left", true);
		drawHUDComponent(gl, hud_turbo, "bottom", "right", true);

		gl.glEnable(GL10.GL_LIGHTING);

		// Animations sector
		global_frame++;
		if (global_frame%3 == 0)
			animateHUD();
	}

	private void drawBackgroundComponent(GL10 gl, TextureCube component, float[] scale, float[] translation) {
		gl.glPushMatrix();
		gl.glBindTexture(GL10.GL_TEXTURE_2D, component.getTextureID());
		gl.glScalef(scale[0],scale[1],scale[2]);
		gl.glTranslatef(translation[0],translation[1],translation[2]);
		component.draw(gl);
		gl.glPopMatrix();
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


	public Starwing getStarwing() {
		return this.starwing;
	}


	// This function ensures that an object maintains the same pixel size regardless of the screen size.
	// Formula: x * (originalScreenSize / newScreenSize)
	// Where:
	//   - 'x' is the object's intended size for the original screen resolution.
	//   - 'originalScreenSize' is the reference screen size.
	//   - 'newScreenSize' is the current screen size.
	// The ratio (originalScreenSize / newScreenSize) is the scale factor applied to preserve the size.
	private void setHUDScales() {
		hud_lives.setTextureScales();
		lives_count.setTextureScales();
		hud_shield.setTextureScales();
		hud_turbo.setTextureScales();
	}
}

