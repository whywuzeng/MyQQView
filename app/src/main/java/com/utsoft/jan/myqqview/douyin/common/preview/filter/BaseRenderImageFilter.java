package com.utsoft.jan.myqqview.douyin.common.preview.filter;

import android.opengl.GLES20;
import android.util.Log;

import com.utsoft.jan.myqqview.douyin.common.preview.RendererInfo2;
import com.utsoft.jan.myqqview.douyin.common.preview.TextrueProgram;

import java.util.Arrays;

/**
 * Created by Administrator on 2019/10/9.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.preview.filter
 */
public abstract class BaseRenderImageFilter {

    //数据顶点
    protected RendererInfo2 mRendererInfo = new RendererInfo2();

    //程序点
    protected int mProgram;

    protected int width;
    protected int height;

    //获得顶点数据
    private MultiRenderInfo multiRenderInfo;

    private int textureType=0;      //默认使用Texture2D0

    protected int mTextureId;

    private int mHPosition;
    private int mHCoord;
    private int mHMatrix;
    private int mHTexture;

    /**
     * 单位矩阵
     */
    public static final float[] OM= getOriginalMatrix();

    private float[] matrix= Arrays.copyOf(OM,16);

    public void create() {

        multiRenderInfo = new MultiRenderInfo();

        //初始化 mpro
        mProgram = new TextrueProgram(getVertexSource(), getFragmentSource()).getmProgramId();
        //ShapeHelper.validateProgram(mProgram);
        onCreated();
    }

    public void surfaceChangedSize(int width, int height) {
        this.width = width;
        this.height = height;
        onChanged(width, height);
    }

    public void draw() {
        clear();
        useProgram();
        onSetExpandData();
        onBindTexture();
        viewPort(0, 0, width, height);
        onDraw();
    }

    protected void viewPort(int x, int y, int width, int height) {
        GLES20.glViewport(x, y, width, height);
    }

    protected void useProgram() {
        GLES20.glUseProgram(mProgram);
    }

    protected void clear() {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

    public void setInputTextureId(int textureId){
        this.mTextureId = textureId;
    }

    public abstract int getOutputTextureId();

    protected abstract String getVertexSource();

    protected abstract String getFragmentSource();

    protected void onCreated(){

        mHPosition= GLES20.glGetAttribLocation(mProgram, "vPosition");
        mHCoord= GLES20.glGetAttribLocation(mProgram,"vCoord");
        mHMatrix= GLES20.glGetUniformLocation(mProgram,"vMatrix");
        mHTexture= GLES20.glGetUniformLocation(mProgram,"vTexture");
    }

    /**
     * 绑定默认纹理
     */
    protected void onBindTexture(){
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0+textureType);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,mTextureId);
        GLES20.glUniform1i(mHTexture,textureType);
    }

    /**
     * 设置其他扩展数据
     */
    protected void onSetExpandData(){
        GLES20.glUniformMatrix4fv(mHMatrix,1,false,this.matrix,0);
    }

    protected abstract void onChanged(int width, int height);

    protected void onDraw(){
        GLES20.glEnableVertexAttribArray(mHPosition);
        GLES20.glVertexAttribPointer(mHPosition,2, GLES20.GL_FLOAT, false, 0,mRendererInfo.getRectVertex());
        checkGlError("mHPosition");
        GLES20.glEnableVertexAttribArray(mHCoord);
        GLES20.glVertexAttribPointer(mHCoord, 2, GLES20.GL_FLOAT, false, 0, mRendererInfo.getTexVertext());
        checkGlError("mHCoord");
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
        GLES20.glDisableVertexAttribArray(mHPosition);
        GLES20.glDisableVertexAttribArray(mHCoord);
    }

    /**
     * Checks to see if a GLES error has been raised.
     */
    public static void checkGlError(String op) {
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            String msg = op + ": glError 0x" + Integer.toHexString(error);
            Log.e("GLError", msg);
        }
    }

    public static float[] getOriginalMatrix(){
        return new float[]{
                1,0,0,0,
                0,1,0,0,
                0,0,1,0,
                0,0,0,1
        };
    }

    public void setMatrix(float[] matrix) {
        this.matrix = matrix;
    }
}
