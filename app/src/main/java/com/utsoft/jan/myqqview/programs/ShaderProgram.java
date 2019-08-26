package com.utsoft.jan.myqqview.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.utsoft.jan.myqqview.ShapeHelper;
import com.utsoft.jan.myqqview.TextResourceReader;

/**
 * Created by Administrator on 2019/8/23.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.programs
 */
public class ShaderProgram {

    //uniform constants
    protected static final String U_MATRIX = "u_Matrix";

    protected static final String U_TEXTUREUNIT = "u_TextureUnit";

    //Attribute constants
    protected static final String a_Position = "a_Position";

    protected static final String a_TextureCoordinates = "a_TextureCoordinates";

    protected static final String a_Color = "a_Color";

    protected final int program;

    protected static final String U_COLOR ="u_Color";


    public ShaderProgram(Context context, int vextexShaderResourceId, int fragmentShaderResourceId) {
        program = ShapeHelper.buildProgram(TextResourceReader.readTextResource(context, vextexShaderResourceId), TextResourceReader.readTextResource(context, fragmentShaderResourceId));
    }

    public void useProgram() {
        GLES20.glUseProgram(program);
    }
}
