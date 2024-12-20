package com.example.practica2_starfox;

import static com.example.practica2_starfox.MyOpenGLRenderer.MAX_HORIZONTAL_OFFSET;
import static com.example.practica2_starfox.MyOpenGLRenderer.MAX_VERTICAL_OFFSET;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;


/*
 * A cube with texture.
 * Define the vertices for only one representative face.
 * Render the cube by translating and rotating the face.
 */
public class HUDTexture {
    private FloatBuffer vertexBuffer; // Buffer for vertex-array
    private FloatBuffer texBuffer;    // Buffer for texture-coords-array (NEW)

    // Texture Size attributes
    private final float original_width;
    private final float original_height;
    private float scale_width;
    private float scale_height;

    // Texture offset attributes
    private float original_horizontal_offset = 0;
    private float original_vertical_offset = 0;
    private float scale_horizontal_offset;
    private float scale_vertical_offset;

    // Animation attributes
    private int frame_amount = 1;
    private int frame;
    private int[] animation;

    private float[] vertices = { // Vertices for a face
            -1.0f, -1.0f, 0.0f,  // 0. left-bottom-front
            1.0f, -1.0f, 0.0f,  // 1. right-bottom-front
            -1.0f,  1.0f, 0.0f,  // 2. left-top-front
            1.0f,  1.0f, 0.0f   // 3. right-top-front
    };

    float[] texCoords;

    int[] textureIDs = new int[1];   // Array for 1 texture-ID (NEW)

    public HUDTexture(float width, float height) {
        this.original_width = width;
        this.original_height = height;
        this.texCoords = new float[] { // Texture coords for the above face (NEW)
                            0.0f, 1.0f,  // A. left-bottom (NEW)
                            1.0f, 1.0f,  // B. right-bottom (NEW)
                            0.0f, 0.0f,  // C. left-top (NEW)
                            1.0f, 0.0f   // D. right-top (NEW)
                        };

        setUpTexture();
    }

    public HUDTexture(float width, float height, int frame_amount, int initial_frame) {
        this.original_width = width;
        this.original_height = height;
        this.frame_amount = frame_amount;

        this.texCoords = new float[] {
                            (float)(initial_frame)/this.frame_amount, 1.0f,
                            (float)(initial_frame+1)/this.frame_amount, 1.0f,
                            (float)(initial_frame)/this.frame_amount, 0.0f,
                            (float)(initial_frame+1)/this.frame_amount, 0.0f
                        };

        setUpTexture();
    }

    // Set up the buffers for the constructor
    public void setUpTexture() {
        // Setup vertex-array buffer. Vertices in float. An float has 4 bytes
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder()); // Use native byte order
        vertexBuffer = vbb.asFloatBuffer(); // Convert from byte to float
        vertexBuffer.put(vertices);         // Copy data into buffer
        vertexBuffer.position(0);           // Rewind

        // Setup texture-coords-array buffer, in float. An float has 4 bytes (NEW)
        ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4);
        tbb.order(ByteOrder.nativeOrder());
        texBuffer = tbb.asFloatBuffer();
        texBuffer.put(texCoords);
        texBuffer.position(0);
    }

    public void animateTexture() {
        frame++;
        frame = frame%frame_amount;

        this.texCoords = new float[] {
                            (float)animation[frame]/frame_amount, 1.0f,
                            (float)(animation[frame]+1)/frame_amount, 1.0f,
                            (float)animation[frame]/frame_amount, 0.0f,
                            (float)(animation[frame]+1)/frame_amount, 0.0f
                        };

        texBuffer.put(texCoords);
        texBuffer.position(0);
    }

    // Draw the shape
    public void draw(GL10 gl) {
        gl.glColor4f(1,1,1,1);

        gl.glFrontFace(GL10.GL_CCW);    // Front face in counter-clockwise orientation
        gl.glEnable(GL10.GL_CULL_FACE); // Enable cull face
        gl.glCullFace(GL10.GL_BACK);    // Cull the back face (don't display)
        gl.glEnable(GL10.GL_TEXTURE_2D);  // Enable texture

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);  // Enable texture-coords-array
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer); // Define texture-coords buffer

        // front
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.0f, 1.0f);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
        gl.glPopMatrix();

        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);  // Disable texture-coords-array (NEW)
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisable(GL10.GL_CULL_FACE);
        gl.glDisable(GL10.GL_TEXTURE_2D);  // Enable texture (NEW));
    }

    // Load an image into GL texture
    public void loadTexture(GL10 gl, Context context, int resource_id) {
        gl.glGenTextures(1, textureIDs, 0); // Generate texture-ID array

        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[0]);   // Bind to texture ID
        // Set up texture filters
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

        // Construct an input stream to texture image "res\drawable\nehe.png"
        InputStream istream = context.getResources().openRawResource(resource_id);

        Bitmap bitmap;
        try {
            // Read and decode input as bitmap
            bitmap = BitmapFactory.decodeStream(istream);
        } finally {
            try {
                istream.close();
            } catch(IOException e) { }
        }

        // Build Texture from loaded bitmap for the currently-bind texture ID
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
    }

    public int getTextureID() {
        return textureIDs[0];
    }

    public void setAnimation(int[] animation) {
        this.animation = animation;
    }

    public void setTextureScales() {
        scaleTextureSize();
        scaleTextureOffset();
    }

    private void scaleTextureSize() {
        this.scale_width = original_width;
        this.scale_height = original_height;
    }

    private void scaleTextureOffset() {
        this.scale_horizontal_offset = original_horizontal_offset;
        this.scale_vertical_offset = original_vertical_offset;
    }

    public float setTop() {
        return MAX_VERTICAL_OFFSET - scale_height;
    }

    public float setBottom() {
        return -MAX_VERTICAL_OFFSET + scale_height;
    }

    public float setLeft() {
        return -MAX_HORIZONTAL_OFFSET + scale_width;
    }

    public float setRight() {
        return MAX_HORIZONTAL_OFFSET - scale_width;
    }

    public void setHorizontalOffset(float offset) {
        this.original_horizontal_offset = offset;
        scaleTextureOffset();
    }

    public void setVerticalOffset(float offset) {
        this.original_vertical_offset = offset;
        scaleTextureOffset();
    }

    public float getTextureWidth() {
        return this.scale_width;
    }

    public float getTextureHeight() {
        return this.scale_height;
    }

    public float getVerticalOffset() {
        return this.scale_vertical_offset;
    }

    public float getHorizontalOffset() {
        return this.scale_horizontal_offset;
    }
}