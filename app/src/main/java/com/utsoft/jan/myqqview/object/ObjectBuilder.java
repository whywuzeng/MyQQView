package com.utsoft.jan.myqqview.object;

import com.utsoft.jan.myqqview.utils.Geometry;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glDrawArrays;

/**
 * Created by Administrator on 2019/8/26.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.object
 */
public class ObjectBuilder {

    //知道多少个顶点  3个顶点 为圆的数据
    private static final int float_per_vertex = 3;

    private final float[] vertexData;

    private int offset = 0;

    private List<DrawCommand> drawCommands = new ArrayList<>();

    public ObjectBuilder(int sizeInVertices) {
        this.vertexData = new float[sizeInVertices*float_per_vertex];
    }

    //圆和 圆柱体 顶点的个数
    private static int sizeofCircleInVertices(int numPoints) {
        return 1 + (numPoints) + 1;
    }

    //圆柱体
    private static int sizeOfCylinderInVertices(int numPoints) {
        return (1 + numPoints) * 2;
    }

    //puck 冰球 建造一个冰球
    public static GeneratedData createPuck(Geometry.Cylinder cylinder, int numPoints) {
        //具体的尺寸大小  交给调用者.

        //要造一个冰球
        final int size = sizeofCircleInVertices(numPoints) + sizeOfCylinderInVertices(numPoints);

        final ObjectBuilder builder = new ObjectBuilder(size);

        //要圆要上升 height/2
        final float translateY = cylinder.height / 2.0f;

        final Geometry.Circle circle = new Geometry.Circle(cylinder.center.translateY(translateY), cylinder.radius);

        builder.append(circle, numPoints);
        builder.append(cylinder, numPoints);

       return builder.build();

    }

   public static GeneratedData createMallet(Geometry.Point center, float radius, float height, int numPoints) {
        final int size = sizeofCircleInVertices(numPoints) * 2 + sizeOfCylinderInVertices(numPoints) * 2;

        final ObjectBuilder builder = new ObjectBuilder(size);

        final float baseHeight = height * 0.25f;

        final Geometry.Circle baseCircle = new Geometry.Circle(center.translateY(-baseHeight), radius);

        final Geometry.Cylinder baseCylinder = new Geometry.Cylinder(baseCircle.center.translateY(-baseHeight / 2f), radius, height);

        builder.append(baseCircle,numPoints);
        builder.append(baseCylinder,numPoints);

        final float handleHeight = height * 0.75f;
        final float handleRadius = radius / 3f;

        final Geometry.Circle handleCircle = new Geometry.Circle(center.translateY(height * 0.5f), handleRadius);

        final Geometry.Cylinder handleCylinder = new Geometry.Cylinder(handleCircle.center.translateY(-handleHeight / 2f), handleHeight, handleRadius);

        builder.append(handleCircle,numPoints);

        builder.append(handleCylinder,numPoints);

        return builder.build();
    }

    //绘制
    private GeneratedData build() {
        return new GeneratedData(vertexData, drawCommands);
    }

    //加入圆的顶点数据
    private void append(Geometry.Circle circle, final int numPoints) {

        final int startVertex = offset / float_per_vertex;
        final int numVertices = sizeofCircleInVertices(numPoints);

        vertexData[offset++] = circle.center.x;
        vertexData[offset++] = circle.center.y;
        vertexData[offset++] = circle.center.z;

        for (int i = 0; i <= numPoints; i++) {
            final float angleFloat = (float) (Math.PI * 2.0f * i / numPoints);
            final float disZ = (float) (circle.radius * Math.sin(angleFloat));
            final float disX = (float) (circle.radius * Math.cos(angleFloat));
            vertexData[offset++] = circle.center.x + disX;
            vertexData[offset++] = circle.center.y;
            vertexData[offset++] = circle.center.z + disZ;
        }

        drawCommands.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_FAN, startVertex, numVertices);
            }
        });
    }

    //加入圆柱体的顶点数据
    private void append(Geometry.Cylinder cylinder, int numPoints) {

        final int startVertex = offset / float_per_vertex;
        final int numVertices = sizeOfCylinderInVertices(numPoints);

        final float startY = cylinder.center.y - cylinder.height / 2;
        final float endY = cylinder.center.y + cylinder.height / 2;

        for (int i = 0; i <=numPoints; i++) {

            float angleR = (float) (2 * Math.PI * i / numPoints);
            final float disX = (float) (Math.cos(angleR) * cylinder.radius);
            final float disZ = (float) (Math.sin(angleR) * cylinder.radius);

            vertexData[offset++] = cylinder.center.x + disX;
            vertexData[offset++] = startY;
            vertexData[offset++] = cylinder.center.z + disZ;

            vertexData[offset++] = cylinder.center.x + disX;
            vertexData[offset++] = endY;
            vertexData[offset++] = cylinder.center.z + disZ;
        }

        drawCommands.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_STRIP, startVertex, numVertices);
            }
        });


    }

    static interface DrawCommand {
        void draw();
    }

    static class GeneratedData {
        final float[] vertexData;
        final List<DrawCommand> drawList;

        GeneratedData(float[] vertexData, List<DrawCommand> drawList) {
            this.vertexData = vertexData;
            this.drawList = drawList;
        }
    }
}
