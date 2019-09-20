package com.utsoft.jan.common.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.SystemClock;

import com.utsoft.jan.common.app.AppProfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/9/20.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.common.utils
 */
public class MutilBitmapUtils {

    // 最大的上传图片大小860kb
    static long MAX_UPLOAD_IMAGE_LENGTH = 860 * 1024;

    public static Drawable Bitmaps2Drawable(List<String> bitmaps) {
        List<String> tmpBitmap = new ArrayList<>();
        for (String bitmap : bitmaps) {

            String cacheDir = AppProfile.getContext().getCacheDir().getAbsolutePath();
            String tempFile = String.format("%s/image/Cache_%s.png", cacheDir, SystemClock.uptimeMillis());
            final File file = new File(bitmap);
            if (!file.exists()) {
                continue;
            }
            if (PicturesCompressor.compressImage(file.getAbsolutePath(), tempFile,
                    MAX_UPLOAD_IMAGE_LENGTH)) {

                tmpBitmap.add(tempFile);
                StreamUtil.delete(bitmap);

            }
        }
        List<Bitmap> tmpbits = new ArrayList<>();
        for (String path : tmpBitmap) {
            final Bitmap bitmap = BitmapFactory.decodeFile(path);
            tmpbits.add(bitmap);
        }

        final Bitmap bitmap = add2Bitmap(tmpbits);

        final String pathFile = Environment.getExternalStorageDirectory().getPath() + File.separator + System.currentTimeMillis() + "temp.png";

        FileUtils.createFile(pathFile);

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(pathFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        return null;
    }


    /**
     * 横向拼接
     *
     * @param first
     * @param second
     * @return 
     */
    private static Bitmap add2Bitmap(List<Bitmap> tmpbits) {

        int width =0 ;
        int height = 0;
        int oldHeight = 0;
        for (Bitmap tmpbit : tmpbits) {
            width += tmpbit.getWidth();
            oldHeight = height = Math.max(tmpbit.getHeight(), oldHeight);

        }

        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        for (int i = 0; i < tmpbits.size(); i++) {
            if (i == 0)
            {
                canvas.drawBitmap(tmpbits.get(i), 0, 0, null);
                continue;
            }
            canvas.drawBitmap(tmpbits.get(i), tmpbits.get(i-1).getWidth(), 0, null);
        }
        return result;
    }

}
