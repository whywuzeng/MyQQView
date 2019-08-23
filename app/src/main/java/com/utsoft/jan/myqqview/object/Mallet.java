package com.utsoft.jan.myqqview.object;

import com.utsoft.jan.myqqview.common.Constants;
import com.utsoft.jan.myqqview.data.VertexArray;
import com.utsoft.jan.myqqview.programs.ColorShaderProgram;

import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glDrawArrays;

/**
 * Created by Administrator on 2019/8/23.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.object
 */
public class Mallet {
    public static final int position_component_count =2;
    public static final int color_component_count = 3;
    public static final int stride = (position_component_count+color_component_count)
            *Constants.BYTE_PER_FLOAT;

    private static final float[] VERTEX_DATA ={
            0f,-0.4f,0f,0f,1f,
            0f,0.4f,1f,0f,0f
    };

    private final VertexArray vertexArray;

    public Mallet() {
        this.vertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bindData(ColorShaderProgram program){
        vertexArray.setVertexAttribPointer(
                0,
                program.getPositionAttributeLocation(),
                position_component_count,
                stride
        );

        vertexArray.setVertexAttribPointer(
                position_component_count,
                program.getColorAttributeLocation(),
                color_component_count,
                stride);
    }

    public void draw(){
        glDrawArrays(GL_POINTS,0,2);
    }
}
