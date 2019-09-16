package com.utsoft.jan.myqqview.douyin.common.preview.filter;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;

import com.utsoft.jan.myqqview.douyin.common.preview.RendererInfo;
import com.utsoft.jan.myqqview.douyin.common.preview.TextrueProgram;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * Created by Administrator on 2019/9/3.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.preview.filter
 */
public class ImageFilter {

    protected  String getFragmentCode() {
        return FRAGMENT_CODE;
    }

    protected  String getVertexCode() {
        return VERTEX_CODE;
    }

    private static final String FRAGMENT_CODE =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES uTexture;\n" +
                    "void main() {\n" +
                    "   gl_FragColor = texture2D(uTexture,vTextureCoord);\n" +
                    "}\n";

    private static final String VERTEX_CODE =
            "uniform mat4 uTexMatrix;\n" +
                    "attribute vec2 aPosition;\n" +
                    "attribute vec4 aTextureCoord;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "    gl_Position = vec4(aPosition,0.0,1.0);\n" +
                    "   vTextureCoord = (uTexMatrix * aTextureCoord).xy;\n" +
                    "}\n";

    protected RendererInfo mRendererInfo = new RendererInfo();

    private ThreadLocal<TextrueProgram> mProgram = new ThreadLocal<>();

    protected int aPositionLocation;

    protected int aTextureCoordLocation;

    protected int uTexMatrixLocation;

    protected int uTextureLocation;

    protected int mWidth;

    protected int mHeight;

    public void init(){
        if (mProgram.get() == null)
        {
            mProgram.set(new TextrueProgram(getVertexCode(),getFragmentCode()));
            onInit();
        }
    }

    private void onInit() {
        initVertexArgument();
        initFragmentArgument();
    }

    protected int getProgram(){
        return mProgram.get().getmProgramId();
    }

    protected void initVertexArgument() {
        aPositionLocation = glGetAttribLocation(getProgram(),"aPosition");
        aTextureCoordLocation = glGetAttribLocation(getProgram(),"aTextureCoord");
        uTexMatrixLocation = glGetUniformLocation(getProgram(),"uTexMatrix");
    }

    protected void initFragmentArgument() {
        uTextureLocation = glGetUniformLocation(getProgram(),"uTexture");
    }

    protected void enableArguments(){
        glEnableVertexAttribArray(aPositionLocation);
        glEnableVertexAttribArray(aTextureCoordLocation);
    }

    protected void disableArguments(){
        glDisableVertexAttribArray(aPositionLocation);
        glDisableVertexAttribArray(aTextureCoordLocation);
    }

    protected void setFragmentAttrs() {

    }

    protected void setVertexAttrs() {

    }

    protected void onDraw(int textureId,float[] texMatrix){
        setVertexAttrs();
        setFragmentAttrs();
        glUniformMatrix4fv(uTexMatrixLocation,1,false,texMatrix,0);
        checkGlError("glUniformMatrix4fv");
        mRendererInfo.getRectVertex().position(0);
        glVertexAttribPointer(aPositionLocation,2,GL_FLOAT,false,0,mRendererInfo.getRectVertex());
        checkGlError("glVertexAttribPointer");
        mRendererInfo.getTexVertext().position(0);
        glVertexAttribPointer(aTextureCoordLocation,2,GL_FLOAT,false,0,mRendererInfo.getTexVertext());
        checkGlError("glVertexAttribPointer");
        glActiveTexture(GL_TEXTURE0);
        //绑定好 申请textureId 绑定好纹理类型.已经绑定一次了。
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,textureId);
        glClear(GL_COLOR_BUFFER_BIT);
        glDrawArrays(GL_TRIANGLE_STRIP,0,4);
    }

    protected void onDraw(int textureId,float[] texMatrix,int width,int height){
        mWidth = width;
        mHeight = height;
        onDraw(textureId,texMatrix);
    }

    public void draw(int textureId,float[] texMatrix,int width,int height){
        if (mProgram == null || textureId == -1)
        {
            return;
        }
        mProgram.get().useProgram();
        checkGlError("useProgram");
        enableArguments();
        onDraw(textureId,texMatrix,width,height);
        disableArguments();
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

    public void release(){
        if (mProgram == null||mProgram.get() == null)
        {
            return;
        }

        glDeleteProgram(mProgram.get().getmProgramId());
        mProgram.set(null);
    }
}
