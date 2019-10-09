package com.utsoft.jan.myqqview.douyin.common.preview.filter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Administrator on 2019/10/9.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.preview.filter
 */
public class MultiRenderInfo {

    //着色器顶点
    private final static float[] FULL_RECT_VERTEX = {
            -1.0f, -1.0f,// 左下角
            1.0f, -1.0f,// 右下角
            -1.0f, 1.0f,// 左上角
            1.0f, 1.0f // 右上角
    };

    //前置摄像头的坐标
    protected final static float frontTextureData[] = {
            1f, 1f, // 右上角
            1f, 0f, // 右下角
            0f, 1f, // 左上角
            0f, 0f //  左下角
    };

    //后置摄像头坐标
    protected final static float backTextureData[] = {
            0f, 1f, // 左上角
            0f, 0f, //  左下角
            1f, 1f, // 右上角
            1f, 0f  // 右上角
    };

    //原有的图像坐标(占不用，弄清楚原理再使用)
    private final static float[] FULL_RECT_TEXTURE_VERTEX = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f
    };

    //显示的纹理坐标点
    protected final static float displayTextureData[] = {
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f,
    };

    //帧缓冲空间坐标点
    protected final static float frameBufferData[] = {
            0f, 0f,
            1f, 0f,
            0f, 1f,
            1f, 1f
    };

    //顶点坐标 Buffer
    private FloatBuffer mVertexBuffer;
    protected int mVertexBufferId;

    //纹理坐标 Buffer
    private FloatBuffer mFrontTextureBuffer;
    //此vbo的用法 不熟悉 抛弃
    //protected int mFrontTextureBufferId;

    //纹理坐标 Buffer
    private FloatBuffer mBackTextureBuffer;
    //protected int mBackTextureBufferId;

    private FloatBuffer mDisplayTextureBuffer;
    //protected int mDisplayTextureBufferId;

    private FloatBuffer mFrameTextureBuffer;
    //protected int mFrameTextureBufferId;


    public FloatBuffer getVertexBuffer() {
        return mVertexBuffer;
    }

    public FloatBuffer getFrontTextureBuffer() {
        return mFrontTextureBuffer;
    }

    public FloatBuffer getBackTextureBuffer() {
        return mBackTextureBuffer;
    }

    public FloatBuffer getDisplayTextureBuffer() {
        return mDisplayTextureBuffer;
    }

    public FloatBuffer getFrameTextureBuffer() {
        return mFrameTextureBuffer;
    }

    public MultiRenderInfo(){
        //装载本地数据  （顶点坐标）
        mVertexBuffer = ByteBuffer.allocateDirect(FULL_RECT_VERTEX.length * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mVertexBuffer.position(0);
        mVertexBuffer.put(FULL_RECT_VERTEX);

        //前
        mFrontTextureBuffer = ByteBuffer.allocateDirect(frontTextureData.length * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mFrontTextureBuffer.position(0);
        mFrontTextureBuffer.put(frontTextureData);

        //后
        mBackTextureBuffer = ByteBuffer.allocateDirect(backTextureData.length * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mBackTextureBuffer.position(0);
        mBackTextureBuffer.put(backTextureData);

        //显示
        mDisplayTextureBuffer = ByteBuffer.allocateDirect(displayTextureData.length * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mDisplayTextureBuffer.position(0);
        mDisplayTextureBuffer.put(displayTextureData);

        ///帧缓冲空间坐标点
        mFrameTextureBuffer = ByteBuffer.allocateDirect(frameBufferData.length * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mFrameTextureBuffer.position(0);
        mFrameTextureBuffer.put(frameBufferData);

    }

}
