package com.utsoft.jan.myqqview.douyin.common.view.record;

import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.utsoft.jan.common.app.AppProfile;
import com.utsoft.jan.myqqview.R;
import com.utsoft.jan.myqqview.douyin.common.preview.GLUtils;
import com.utsoft.jan.myqqview.douyin.common.preview.filter.BaseRenderImageFilter;
import com.utsoft.jan.myqqview.douyin.common.preview.filter.GroupFilter;
import com.utsoft.jan.myqqview.douyin.common.preview.filter.OriginRenderImage;
import com.utsoft.jan.myqqview.douyin.common.preview.filter.WaterMarkFilter;
import com.utsoft.jan.myqqview.douyin.common.preview.filter.carmera.NoFilter;
import com.utsoft.jan.myqqview.douyin.common.preview.filter.carmera.ProcessFilter;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Administrator on 2019/10/8.
 * <p>
 * by author wz
 * 编码的render 最终生成视频，多个过滤
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.view.record
 */
public class EncodeRender3 implements GLSurfaceView.Renderer {

    private int mTextureId;

    private SurfaceTexture mSurfaceTexture;
    private int mCanvasWidth;
    private int mCanvasHeight;

    private int mFrameBuffer;

    private OriginRenderImage originRenderImage;

    private GroupFilter groupFilter;

    private ProcessFilter mProcessFilter;

    private BaseRenderImageFilter mShow;


    private static final String TAG = "EncodeRender";

    //这个类的 fbo
    private int[] fFrame = new int[1];
    private int[] fTexture = new int[1];

    ////////////////////////////


    public EncodeRender3() {
        this.mFrameBuffer = 0;
        this.mTextureId = 0;
        originRenderImage =new OriginRenderImage();
        mProcessFilter = new ProcessFilter();
        mShow = new NoFilter();
        groupFilter = new GroupFilter();
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    //设置最开始 纹理ID
    public void setInputTexture(int texture) {
        this.mTextureId = texture;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //OES展示视频
        mTextureId = GLUtils.createTextureObject(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        mSurfaceTexture = new SurfaceTexture(mTextureId);

        originRenderImage.create();
        //直接调用
        mProcessFilter.onCreated();

        mShow.create();

        groupFilter.init();

        final WaterMarkFilter drawer = new WaterMarkFilter();
        drawer.setWaterMark(BitmapFactory.decodeResource(AppProfile.getContext().getResources(), R.mipmap.bufuhanzhe));
        drawer.setPosition(0, 70, 0, 0);
        groupFilter.addFilter(drawer);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mCanvasWidth = width;
        mCanvasHeight = height;

        GLES20.glDeleteFramebuffers(1, fFrame, 0);
        GLES20.glDeleteTextures(1, fTexture, 0);

        GLES20.glGenFramebuffers(1,fFrame,0);
        //创建一个 textureId ，创建一个buffersFrame textureId
        fTexture[0] = GLUtils.createFrameTexture(width,height);

        originRenderImage.surfaceChangedSize(width,height);
        originRenderImage.setInputTextureId(mTextureId);

        groupFilter.setSize(width,height);

        //直接调用
        mProcessFilter.onChanged(width,height);

        //调用父类
        mShow.surfaceChangedSize(width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {


        float matrix[] = new float[16];
        if (mSurfaceTexture!=null)
        {
            //从图像流中将纹理图像更新为最近的帧。 就可以消耗这些图像内存
            //可以通过 SurfaceTexture 来创建一个 Surface 然后通过 Surface 向 SurfaceTexture里的 BQ 中提供图像内存
            mSurfaceTexture.updateTexImage();
        }

        mSurfaceTexture.getTransformMatrix(matrix);

        GLUtils.bindFrameTexture(fFrame[0],fTexture[0]);

        originRenderImage.draw(0,matrix);
        GLUtils.unBindFrameBuffer();

        groupFilter.draw(matrix,fTexture[0]);

        mProcessFilter.setInputTextureId(groupFilter.getOutputTexture());
        mProcessFilter.draw();

        GLES20.glViewport(0,0,mCanvasWidth,mCanvasHeight);

        mShow.setInputTextureId(mProcessFilter.getOutputTextureId());
        mShow.draw();
    }
}
