package com.utsoft.jan.myqqview.programs;

import android.content.Context;

import com.utsoft.jan.myqqview.R;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by Administrator on 2019/8/23.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.programs
 */
public class TextureShaderProgram extends ShaderProgram{

    //纹理着色器 uniform
    private final int uTextureUnitLocation;
    private final int uMatrixLocation;

    //attribute location
    private final int aPositionLocaiton;
    private final int aTextureCoordinatesLocation;

    public TextureShaderProgram(Context context) {
        super(context, R.raw.texture_vertex_shader, R.raw.texture_fragment_shader);

        uTextureUnitLocation = glGetUniformLocation(program,U_TEXTUREUNIT);
        uMatrixLocation = glGetUniformLocation(program,U_MATRIX);

        aPositionLocaiton = glGetAttribLocation(program,a_Position);
        aTextureCoordinatesLocation = glGetAttribLocation(program,a_TextureCoordinates);

    }

    public void setUniform(float[] matrix,int textureId){
        glUniformMatrix4fv(uMatrixLocation,1,false,matrix,0);

        glActiveTexture(GL_TEXTURE0);

        glBindTexture(GL_TEXTURE_2D,textureId);

        glUniform1i(uTextureUnitLocation,0);
    }

    public int getPositionAttributeLocation(){
        return aPositionLocaiton;
    }

    public int getTextureCoordinatesLocation(){
        return aTextureCoordinatesLocation;
    }
}
