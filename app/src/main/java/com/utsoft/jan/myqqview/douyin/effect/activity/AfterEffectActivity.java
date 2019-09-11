package com.utsoft.jan.myqqview.douyin.effect.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.utsoft.jan.common.app.PresenterActivity;
import com.utsoft.jan.myqqview.R;
import com.utsoft.jan.myqqview.douyin.common.view.record.RecordSurfaceView;

/**
 * Created by Administrator on 2019/9/11.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.effect.activity
 */
public class AfterEffectActivity extends PresenterActivity {

    private static final String key_path_final = "key:path";

    private RecordSurfaceView surfaceView;

    public static void start(Activity from, String fileName) {
        final Intent intent = new Intent(from, AfterEffectActivity.class);
        intent.putExtra(key_path_final, fileName);
        from.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_effect);
        surfaceView = findViewById(R.id.sv_record);
    }
}
