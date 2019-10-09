package com.utsoft.jan.myqqview.douyin.common.preview.filter;

/**
 * Created by Administrator on 2019/10/9.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.preview.filter
 */
public interface BaseRenderImageFilterImpl {
    //创建 mProgram 程序
    void onCreate();

    //改变image尺寸
    void onChanged(int width,int height);

    //设置纹理
    void setInputTextureId(int textureId);

    //得到纹理
    int getOutputTextureId();

    //顶点着色器
    String getVertexSource();

    //片元着色器
    String getFragmentSource();

    void onDraw();
}
