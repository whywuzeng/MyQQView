package com.utsoft.jan.common.utils;

import android.support.annotation.DimenRes;

import com.utsoft.jan.common.app.AppProfile;


/**
 * Created by 薛贤俊 on 2018/4/23.
 */

public class ResourcesUtil {

    public static int getDimensionPixel(@DimenRes int id) {
        return AppProfile.getContext().getResources().getDimensionPixelSize(id);
    }
}
