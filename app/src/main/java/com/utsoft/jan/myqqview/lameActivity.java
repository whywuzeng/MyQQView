package com.utsoft.jan.myqqview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.utsoft.jan.mp3encoder.Mp3Encoder;

/**
 * Created by Administrator on 2019/10/29.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview
 */
public class lameActivity extends AppCompatActivity {
    private static final String TAG = "lameActivity";
    private Button btnLame;

    public static void start(Activity from ) {
        final Intent intent = new Intent(from, lameActivity.class);
        from.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lame);
        btnLame = (Button)findViewById(R.id.btn_lame);
        btnLame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pcmPath = "/mnt/sdcard/a_songstudio/vocal.pcm";
                int audioChannels = 2;
                int bitRate = 128 * 1024;
                int sampleRate = 44100;
                String mp3Path = "/mnt/sdcard/a_songstudio/vocal.mp3";
                final int ret = Mp3Encoder.init(pcmPath, audioChannels, bitRate, sampleRate, mp3Path);

                if (ret  >= 0)
                {
                    Mp3Encoder.encode();
                    Mp3Encoder.destroy();
                    Log.i(TAG, "Encode Mp3 Success");
                }else {
                    Log.i(TAG, "Encoder Initialized Failed...");
                }
            }
        });
    }
}
