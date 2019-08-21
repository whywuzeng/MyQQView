package com.utsoft.jan.myqqview;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class MainActivity2 extends AppCompatActivity {


    private ApproveLayout layApprove;
    private ImageView loveImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        layApprove = findViewById(R.id.lay_approve);
        loveImg = (ImageView) findViewById(R.id.im_heart);

        loveImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layApprove.addHeart();
                AnimatorSet set = getEnterAnimator(loveImg);
                if (set.isRunning())
                {
                    set.cancel();
                    set.start();
                }else {
                    set.start();
                }
            }
        });
    }

    private AnimatorSet getEnterAnimator(View target) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(target, View.SCALE_X, 0.5f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(target, View.SCALE_Y, 0.5f, 1.0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.setDuration(1000);
        return animatorSet;
    }
}
