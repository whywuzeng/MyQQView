package com.utsoft.jan.myqqview.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;

import com.utsoft.jan.myqqview.LoggerConfig;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;

/**
 * Created by Administrator on 2019/8/23.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.utils
 */
public class TextureHelper {

    private static final String TAG = "TextureHelper";

    public static int loadTexture(Context context,int resourceId)
    {
        final int[] textureObjectId = new int[1];

        glGenTextures(1,textureObjectId,0);

        if (textureObjectId[0] == 0)
        {
            if (LoggerConfig.ON)
            {
                Log.e(TAG, "loadTexture: not create texture object" );
            }
            return 0;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

        if (bitmap == null)
        {
            if (LoggerConfig.ON)
            {
                Log.e(TAG, "loadTexture: not get bitmap from no-dip folder" );
            }

            glDeleteTextures(1,textureObjectId,0);
            return 0;
        }

        glBindTexture(GL_TEXTURE_2D,textureObjectId[0]);

        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR_MIPMAP_LINEAR);

        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);

        GLUtils.texImage2D(GL_TEXTURE_2D,0,bitmap,0);

        bitmap.recycle();

        glGenerateMipmap(GL_TEXTURE_2D);

        glBindTexture(GL_TEXTURE_2D,0);

        return textureObjectId[0];
    }
}
