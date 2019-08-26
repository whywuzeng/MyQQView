package com.utsoft.jan.myqqview.object;

import com.utsoft.jan.myqqview.data.VertexArray;
import com.utsoft.jan.myqqview.programs.ColorShaderProgram;
import com.utsoft.jan.myqqview.utils.Geometry;

import java.util.List;

/**
 * Created by Administrator on 2019/8/26.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.object
 */
public class Puck {
    private static final int POSITION_COMPONENT_COUNT = 3;

    public final float radius, height;

    private final VertexArray vertexArray;

    private final List<ObjectBuilder.DrawCommand> drawCommands;

    public Puck(float radius, float height, int numPointsAroundPuck) {
        ObjectBuilder.GeneratedData generatedData = ObjectBuilder.createPuck(new Geometry.Cylinder(new Geometry.Point(0f,0f,0f),height,radius),numPointsAroundPuck);


        this.radius = radius;
        this.height = height;
        this.vertexArray = new VertexArray(generatedData.vertexData);
        this.drawCommands = generatedData.drawList;
    }

    public void bindData(ColorShaderProgram shaderProgram){
        vertexArray.setVertexAttribPointer(0,shaderProgram.getColorAttributeLocation(),
                POSITION_COMPONENT_COUNT,0);
    }

    public void draw(){
        for (ObjectBuilder.DrawCommand drawCommand : drawCommands) {
            drawCommand.draw();
        }
    }
}
