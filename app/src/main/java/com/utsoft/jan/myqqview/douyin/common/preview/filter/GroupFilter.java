package com.utsoft.jan.myqqview.douyin.common.preview.filter;

import android.opengl.GLES20;

import com.utsoft.jan.myqqview.douyin.common.preview.GLUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by Administrator on 2019/10/8.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.preview.filter
 */
public class GroupFilter extends ImageFilter{

    private Queue<ImageFilter> mFilterQueue = new ConcurrentLinkedDeque<>();
    private List<ImageFilter> mFilters;

    private int width=0, height=0;
    private int size=0;
    private int mTextureId;

    @Override
    public void init() {
        //super.init();
        if (mFilterQueue ==null)
        mFilterQueue = new ConcurrentLinkedDeque<>();
        if (mFilters == null)
        mFilters = new ArrayList<>();
        updateFilter();
        createFrameBuffer();
    }

    public void setSize(int width,int height)
    {
        this.width = width;
        this.height = height;
    }

    public void addFilter(final ImageFilter filter){
        mFilterQueue.add(filter);
    }

    public boolean removeFilter(ImageFilter filter){
        boolean b=mFilters.remove(filter);
        if(b){
            size--;
        }
        return b;
    }

    public ImageFilter removeFilter(int index){
        ImageFilter f= mFilters.remove(index);
        if(f!=null){
            size--;
        }
        return f;
    }

    /**
     * 双Texture,一个输入一个输出,循环往复
     */
    public void draw(float[] matrix, int textureId){
        this.mTextureId =textureId;
        updateFilter();
        textureIndex=0;
        for (ImageFilter filter:mFilters){
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fFrame[0]);
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                    GLES20.GL_TEXTURE_2D, fTexture[textureIndex%2], 0);
            GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                    GLES20.GL_RENDERBUFFER, fRender[0]);
            GLUtils.checkError();
            GLES20.glViewport(0,0,width,height);
            if(textureIndex==0){
                final WaterMarkFilter filter1 = (WaterMarkFilter) filter;
                filter1.init();
                filter1.onDraw(this.mTextureId,matrix);
            }else{
                filter.draw(fTexture[(textureIndex-1)%2],matrix,width,height);
            }
            unBindFrame();
            textureIndex++;
        }
    }

    private void updateFilter(){
        ImageFilter f;
        while ((f=mFilterQueue.poll())!=null){
            mFilters.add(f);
            size++;
        }
    }

    //创建离屏buffer
    private int fTextureSize = 2;
    private int[] fFrame = new int[1];
    private int[] fRender = new int[1];
    private int[] fTexture = new int[fTextureSize];
    private int textureIndex=0;

    //创建FrameBuffer
    private boolean createFrameBuffer() {
        GLES20.glGenFramebuffers(1, fFrame, 0);
        GLES20.glGenRenderbuffers(1, fRender, 0);

        genTextures();
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fFrame[0]);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, fRender[0]);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width,
                height);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, fTexture[0], 0);
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                GLES20.GL_RENDERBUFFER, fRender[0]);
        unBindFrame();
        return false;
    }

    //生成Textures
    private void genTextures() {
        GLES20.glGenTextures(fTextureSize, fTexture, 0);
        for (int i = 0; i < fTextureSize; i++) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fTexture[i]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height,
                    0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        }
    }

    //取消绑定Texture
    private void unBindFrame() {
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public int getOutputTexture(){
        return size==0?mTextureId:fTexture[(textureIndex-1)%2];
        //return mTextureId;
    }
}
