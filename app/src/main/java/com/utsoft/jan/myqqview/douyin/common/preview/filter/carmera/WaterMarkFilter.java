package com.utsoft.jan.myqqview.douyin.common.preview.filter.carmera;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.opengl.GLES20;
import android.opengl.GLUtils;



/**
 * 水印的Filter
 */

public class WaterMarkFilter extends NoFilter{
    private int x,y,w,h;
    private int width,height;
    private Bitmap mBitmap;
    private Bitmap mGifBitmap;
    private NoFilter mFilter;
    public android.graphics.Matrix mMatrix;

    public WaterMarkFilter() {
        mFilter=new NoFilter(){
            @Override
            protected void clear() {
            }
        };
    }
    private boolean mIsGif = false;
    private int mRotateDegree;
    public WaterMarkFilter( boolean isGif, int bitRes, float rotateDegree) {
        mIsGif = isGif;
        mRotateDegree = (int)rotateDegree;
        mFilter=new NoFilter(){
            @Override
            protected void clear() {
            }
        };
    }

    public void setWaterMark(Bitmap bitmap){
        if(this.mBitmap!=null && !mBitmap.isRecycled()){
            this.mBitmap.recycle();
            mBitmap = null;
        }
        this.mBitmap=bitmap;
    }
    private long mStartTime,mEndTime;
    public void setShowTime(long startTime,long endTime){
        mStartTime = startTime;
        mEndTime = endTime;
    }
    private float[] mRotationMatrix = new float[16];
    @Override
    public void draw() {
        super.draw();
        GLES20.glViewport(x,y,w == 0 ? mBitmap.getWidth():w,h==0?mBitmap.getHeight():h);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        //这个要画的话  。。上面要走父类的方法
        mFilter.draw();
    }

    public void draw(long time) {
        super.draw();
        if(mIsGif){
            createTexture();
        }
        int i = (int) (mBitmap.getWidth() * 1.15);
        int i1 = (int) (mBitmap.getHeight() * 1.15);
        GLES20.glViewport(x, y, w == 0 ? i : w, h == 0 ? i1 : h);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        mFilter.draw();
    }

    @Override
    protected void onCreated() {
        super.onCreated();
        mFilter.create();
        createTexture();
    }
    private int[] textures=new int[1];
    private void createTexture() {
        if(mBitmap!=null){
            GLES20.glGenTextures(1,textures,0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textures[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            if(!mIsGif) {
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0,  mBitmap, 0);
            }else {
            }
            //对画面进行矩阵旋转
//            MatrixUtils.flip(mFilter.getMatrix(),false,true);

            mFilter.setInputTextureId(textures[0]);
        }
    }
    public void setMatrix(Matrix matrix){
        mMatrix = matrix;
    }
    @Override
    protected void onChanged(int width, int height) {
        this.width=width;
        this.height=height;
        /*GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE_MINUS_SRC_ALPHA);*/
        mFilter.surfaceChangedSize(width,height);
    }
    public void setPosition(int x,int y,int width,int height){
        this.x=x;
        this.y=y;
        this.w=width;
        this.h=height;
    }
}
