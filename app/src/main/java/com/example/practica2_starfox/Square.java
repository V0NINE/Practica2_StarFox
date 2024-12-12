package com.example.practica2_starfox;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Square {
    /*private float vertices[] = {
            -1f, -1f, 0.0f,
            -1f,  1f, 0.0f,
            1f,  1f, 0.0f,
            1f, -1f, 0.0f
    };*/
    private short faces[] = { 0, 1, 2, 0, 2, 3 };

    private FloatBuffer vertexBuffer;

    private ShortBuffer indexBuffer;

    public Square(float vertices[]) {
        // Move the vertices list into a buffer
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        // Move the faces list into a buffer
        ByteBuffer ibb = ByteBuffer.allocateDirect(faces.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        indexBuffer = ibb.asShortBuffer();
        indexBuffer.put(faces);
        indexBuffer.position(0);
    }

    public void setColor(GL10 gl, float r, float g, float b) {
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY); // Disable color arrays
        gl.glColor4f(r, g, b, 1.0f); // Set the color (RGBA)
    }

    public void draw(GL10 gl) {
        // Enable the vertices buffer for writing and to be used during rendering
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        // Specify the location and data format of an array of vertex coordinates to use when rendering
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

        // Draw the square
        gl.glDrawElements(GL10.GL_TRIANGLES, faces.length, GL10.GL_UNSIGNED_SHORT, indexBuffer);

        // Disable the vertices buffer
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
}