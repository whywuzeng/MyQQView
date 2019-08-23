package com.utsoft.jan.myqqview.object;

import com.utsoft.jan.myqqview.common.Constants;
import com.utsoft.jan.myqqview.data.VertexArray;
import com.utsoft.jan.myqqview.programs.TextureShaderProgram;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;

/**
 * Created by Administrator on 2019/8/23.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.object
 */
public class Table {
    public static final int position_component_count =2;
    public static final int texture_coordinates_component_count = 2;
    public static final int stride = (position_component_count+texture_coordinates_component_count)
            *Constants.BYTE_PER_FLOAT;

    private static final float[] VERTEX_DATA = {
            0f, 0f, 0.5f, 0.5f,
            -0.5f, -0.8f, 0f, 0.9f,
            0.5f, -0.8f, 1f, 0.9f,

            0.5f, 0.8f, 1f, 0.1f,
            -0.5f, 0.8f, 0f, 0.1f,
            -0.5f, -0.8f, 0f, 0.9f,
    };

    private final VertexArray vertexArray;

    public Table() {
        this.vertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bindData(TextureShaderProgram program){
        vertexArray.setVertexAttribPointer(
                0,
                program.getPositionAttributeLocation(),
                position_component_count,
                stride
                );

        vertexArray.setVertexAttribPointer(
                position_component_count,
                program.getTextureCoordinatesLocation(),
                texture_coordinates_component_count,
                stride
        );
    }

    public void draw(){
        glDrawArrays(GL_TRIANGLE_FAN,0,6);
    }
}
