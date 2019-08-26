package com.utsoft.jan.myqqview.programs;

import android.content.Context;

import com.utsoft.jan.myqqview.R;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by Administrator on 2019/8/23.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.programs
 */
public class ColorShaderProgram extends ShaderProgram{

    //attribute
    private final int aPositionLocation;
    private final int aColorLocation;

    //uniform
    private final int uMatrixLocation;
    private final int uColorLocation;

    public ColorShaderProgram(Context context) {
        super(context, R.raw.simple_vertex_shader, R.raw.simple_fragment_shader);

        aPositionLocation = glGetAttribLocation(program,a_Position);
        aColorLocation = glGetAttribLocation(program,a_Color);

        uMatrixLocation = glGetUniformLocation(program,U_MATRIX);
        uColorLocation = glGetUniformLocation(program,U_COLOR);
    }

    public void setUniform(float[] matrix,float r,float g,float b){
        glUniformMatrix4fv(uMatrixLocation,1,false,matrix,0);
        glUniform4f(uColorLocation,r,g,b,1f);
    }

    public int getPositionAttributeLocation(){
        return aPositionLocation;
    }

    public int getColorAttributeLocation(){
        return aColorLocation;
    }
}
