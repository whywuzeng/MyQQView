package com.utsoft.jan.myqqview.douyin.common.preview.filter;

import java.nio.FloatBuffer;

/**
 * Created by Administrator on 2019/10/9.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.preview.filter
 */
public class MultiRenderInfo {

    private BaseRenderImageFilterImpl baseRenderImageFilter;

    public MultiRenderInfo(BaseRenderImageFilterImpl baseRenderImageFilter) {
        //必须实现接口
        this.baseRenderImageFilter = baseRenderImageFilter;
    }

    //着色器顶点
    private final static float[] FULL_RECT_VERTEX = {
            -1.0f, -1.0f,// 左下角
            1.0f, -1.0f,// 右下角
            -1.0f, 1.0f,// 左上角
            1.0f, 1.0f // 右上角
    };

    //前置摄像头的坐标
    protected float frontTextureData[] = {
            1f, 1f, // 右上角
            1f, 0f, // 右下角
            0f, 1f, // 左上角
            0f, 0f //  左下角
    };

    //后置摄像头坐标
    protected float backTextureData[] = {
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
    protected float displayTextureData[] = {
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f,
    };

    //帧缓冲空间坐标点
    protected float frameBufferData[] = {
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
    protected int mFrontTextureBufferId;

    //纹理坐标 Buffer
    private FloatBuffer mBackTextureBuffer;
    protected int mBackTextureBufferId;

    private FloatBuffer mDisplayTextureBuffer;
    protected int mDisplayTextureBufferId;

    private FloatBuffer mFrameTextureBuffer;
    protected int mFrameTextureBufferId;

    public void create(){

    }

}
