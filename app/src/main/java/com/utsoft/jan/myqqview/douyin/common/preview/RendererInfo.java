package com.utsoft.jan.myqqview.douyin.common.preview;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Administrator on 2019/9/4.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.preview
 */
public class RendererInfo {

    private final static float[] FULL_RECT_VERTEX = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f
    };

    private final static float[] FULL_RECT_TEXTURE_VERTEX = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f
    };

    private FloatBuffer rectVertex;

    public FloatBuffer getRectVertex() {
        return rectVertex;
    }

    public FloatBuffer getTexVertext() {
        return texVertext;
    }

    private FloatBuffer texVertext;

    public RendererInfo() {
        rectVertex = ByteBuffer.allocateDirect(FULL_RECT_VERTEX.length * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        rectVertex.position(0);
        rectVertex.put(FULL_RECT_VERTEX);
        texVertext = ByteBuffer.allocateDirect(FULL_RECT_TEXTURE_VERTEX.length * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        texVertext.position(0);
        texVertext.put(FULL_RECT_TEXTURE_VERTEX);
    }



}
