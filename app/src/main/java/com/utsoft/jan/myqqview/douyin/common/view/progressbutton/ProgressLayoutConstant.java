package com.utsoft.jan.myqqview.douyin.common.view.progressbutton;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Administrator on 2019/9/10.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.view.progressbutton
 */
public class ProgressLayoutConstant {

    public static final int First_status = 1;

    public static final int Second_status = 2;

    public static final int LOADING_STATUS = 3;

    public static final int LOAD_FINISH = 4;

    public static final int MODE_EXTRA_FAST = 5;

    @IntDef({First_status, Second_status, LOADING_STATUS, LOAD_FINISH, MODE_EXTRA_FAST})
    @Retention(RetentionPolicy.SOURCE)
    public @interface StatusMode {

    }


}
