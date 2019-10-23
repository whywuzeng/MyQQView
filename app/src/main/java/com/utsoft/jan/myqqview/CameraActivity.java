package com.utsoft.jan.myqqview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.utsoft.jan.myqqview.callback.CameraCallback;
import com.utsoft.jan.myqqview.douyin.common.camera.CameraCompat;

/**
 * Created by Administrator on 2019/10/23.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview
 */
public class CameraActivity extends AppCompatActivity {

   private SurfaceView mSurfaceView;
    private CameraCompat cameraCompat;

    public static void startCameraActivity(Context context, Bundle extras) {
        final Intent intent = new Intent(context, CameraActivity.class);
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_camera);
        cameraCompat = CameraCompat.newInstance(this);
        initView();
    }

    private void initView() {
        mSurfaceView = (SurfaceView) findViewById(R.id.glsurfaceview_camera);
        mSurfaceView.getHolder().addCallback(new CameraCallback(cameraCompat));
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraCompat.startPreview();
        final CameraCompat.CameraSize outputSize = cameraCompat.getOutputSize();
        //glSurfaceView.setPreviewSize(outputSize.width,outputSize.height);
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraCompat.stopPreview(true);
    }


}
