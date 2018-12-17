package com.example.junmung.hangangparksmap.ARGuide;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class ARArrow {
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mColorBuffer;
    private ByteBuffer  mIndexBuffer;


    private float[] vertices = { // 5 vertices of the pyramid in (x,y,z)
            -0.3f, -0.3f, -0.3f,  // 0. left-bottom-back
            0.3f, -0.3f, -0.3f,  // 1. right-bottom-back
            0.3f, -0.3f,  0.3f,  // 2. right-bottom-front
            -0.3f, -0.3f,  0.3f,  // 3. left-bottom-front
            0.0f,  1.0f,  0.0f   // 4. top
    };


    private float[] colors = {  // Colors of the 5 vertices in RGBA
            0.6f, 0.0f, 0.0f, 1f,  // 0. blue
            0.4f, 0.0f, 0.0f, 1f,  // 1. green
            0.4f, 0.0f, 0.0f, 1f,  // 2. blue
            0.4f, 0.0f, 0.0f, 1f,  // 3. green
            1.0f, 0.0f, 0.0f, 1.0f   // 4. red
    };


    private byte indices[] = {
            2, 4, 3,   // front face (CCW)
            1, 4, 2,   // right face
            0, 4, 1,   // back face
            4, 0, 3    // left face
    };


    public ARArrow() {
        mVertexBuffer = createFloatBuffer(vertices);
        mColorBuffer = createFloatBuffer(colors);

        mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
        mIndexBuffer.put(indices);
        mIndexBuffer.position(0);
    }

    public void draw(GL10 gl) {
        gl.glFrontFace(GL10.GL_CW);

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_BYTE,
                mIndexBuffer);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 3);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
    }

    protected FloatBuffer createFloatBuffer(float[] array){
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(array.length * 4);// = array.length * 4
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        floatBuffer.put(array);
        floatBuffer.position(0);

        return floatBuffer;
    }


}