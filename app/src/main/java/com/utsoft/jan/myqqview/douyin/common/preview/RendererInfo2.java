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
public class RendererInfo2 {

    private final static float[] FULL_RECT_VERTEX = {
            -1.0f,  1.0f,
            -1.0f, -1.0f,
            1.0f, 1.0f,
            1.0f,  -1.0f,
    };

    private final static float[] FULL_RECT_TEXTURE_VERTEX = {
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
    };

    private FloatBuffer rectVertex;

    public FloatBuffer getRectVertex() {
        return rectVertex;
    }

    public FloatBuffer getTexVertext() {
        return texVertext;
    }

    private FloatBuffer texVertext;

    public RendererInfo2() {
        rectVertex = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        rectVertex.put(FULL_RECT_VERTEX);
        rectVertex.position(0);
        texVertext = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        texVertext.put(FULL_RECT_TEXTURE_VERTEX);
        texVertext.position(0);
    }



}
