package com.utsoft.jan.myqqview.callback;

import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.utsoft.jan.common.app.AppProfile;
import com.utsoft.jan.myqqview.douyin.common.camera.CameraCompat;
import com.utsoft.jan.myqqview.utils.OpenGLJinLib;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2019/10/23.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.callback
 */
public class CameraCallback implements SurfaceHolder.Callback {

    private static final String TAG = "CameraCallback";
    private  CameraCompat mCameraCompat;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private SurfaceTexture mSurfaceTexture;
    private int width;
    private int height;
    private SurfaceTexture surfaceTexture;

    Handler mainHandler = new Handler(Looper.getMainLooper());

    private float[] Matrix = new float[16];

    public CameraCallback(CameraCompat cameraCompat){
        this.mCameraCompat = cameraCompat;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.width = width;
        this.height = height;

        initOpenGL(holder.getSurface(),width,height);
    }

    private void initOpenGL(final Surface surface, final int width, final int height) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                final int textureID = OpenGLJinLib.cameraFilterInite(surface, width, height, AppProfile.getContext().getAssets());
                if (textureID<0)
                {
                    Log.e(TAG, "run: surfaceChanged create is failed" );
                }

                surfaceTexture = new SurfaceTexture(textureID);
                surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                    @Override
                    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                        drawOpenGL();
                    }
                });

                mCameraCompat.setSurfaceTexture(surfaceTexture);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mCameraCompat.startPreview();
                    }
                });

            }
        });
    }

    private void drawOpenGL() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                surfaceTexture.updateTexImage();
                surfaceTexture.getTransformMatrix(Matrix);
                OpenGLJinLib.cameraFilterDraw(Matrix);
            }
        });
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                surfaceTexture.release();
                surfaceTexture = null;
                OpenGLJinLib.cameraFilterRelease();
            }
        });
    }
}
