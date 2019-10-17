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
            -1f, -1f,// 左下角
            1f, -1f, // 右下角
            -1f, 1f, // 左上角
            1f, 1f,  // 右上角
    };

    private final static float[] FULL_RECT_TEXTURE_VERTEX = {
            0f, 0f, //左下角
            1f, 0f, //右下角
            0f, 1f,// 左上角
            1f, 1f, // 右上角
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
