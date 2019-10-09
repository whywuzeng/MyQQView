package com.utsoft.jan.myqqview.douyin.common.recoder.video;

import android.graphics.SurfaceTexture;
import android.view.Surface;

import com.utsoft.jan.common.app.AppProfile;
import com.utsoft.jan.myqqview.douyin.common.view.record.EncodeRender;
import com.utsoft.jan.myqqview.douyin.common.view.record.RecordRender;

/**
 * Created by Administrator on 2019/9/28.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.recoder.video
 */
public class OutputSurface implements SurfaceTexture.OnFrameAvailableListener {

    private Surface mSurface;
    private SurfaceTexture surfaceTexture;
    private Object mFrameSyncObject = new Object();
    private  boolean mFrameAvailable;
    private EncodeRender encodeRender;

    private RecordRender mRecordRender;

    public Surface getSurface() {
        return mSurface;
    }


    public void setup(int videoWidth, int videoHeight){
        encodeRender = new EncodeRender(AppProfile.getContext());
        encodeRender.onSurfaceCreated(null,null);
        encodeRender.onSurfaceChanged(null,videoWidth,videoHeight);

        surfaceTexture = encodeRender.getSurfaceTexture();
        surfaceTexture.setOnFrameAvailableListener(this);
        mSurface = new Surface(surfaceTexture);

    }

    //阻塞 等待新newImage
    public void awaitNewImage() {
        final int TIMEOUT_MS = 500;
        synchronized (mFrameSyncObject){
            while (!mFrameAvailable)
            {

                try {
                    mFrameSyncObject.wait(TIMEOUT_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mFrameAvailable = false;
        }
    }


    public void drawImage(long presentationTime) {
        encodeRender.onDrawFrame(null);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        synchronized (mFrameSyncObject){
            if (mFrameAvailable) {
                throw new RuntimeException("mFrameAvailable already set, frame could be dropped");
            }

            mFrameAvailable = true;
            mFrameSyncObject.notifyAll();
        }
    }
}
