package com.utsoft.jan.myqqview.data;

import com.utsoft.jan.myqqview.common.Constants;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * Created by Administrator on 2019/8/23.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.data
 */
public class VertexArray {
    private FloatBuffer floatBuffer;

    public VertexArray(float[] floats) {
        this.floatBuffer = ByteBuffer.allocateDirect(floats.length * Constants.BYTE_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(floats);
    }

    public void setVertexAttribPointer(int dataOffset, int attributeLocation,
                                       int componentCount, int stride) {
        floatBuffer.position(dataOffset);
        glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT,
                false, stride, floatBuffer);
        glEnableVertexAttribArray(attributeLocation);
        floatBuffer.position(0);
    }
}
