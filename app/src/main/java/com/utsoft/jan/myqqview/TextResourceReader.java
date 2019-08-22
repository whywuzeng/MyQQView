package com.utsoft.jan.myqqview;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2019/8/22.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview
 */
public class TextResourceReader {
    public static String readTextResource(Context context, int resourceId){
        StringBuilder body = new StringBuilder();

        try {
            InputStream inputStream = context.getResources().openRawResource(resourceId);

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String readLine;
            while ((readLine =bufferedReader.readLine())!=null)
            {
                body.append(readLine);
                body.append('\n');
            }
        } catch (IOException | Resources.NotFoundException e) {
            e.printStackTrace();
        }

        return body.toString();
    }
}
