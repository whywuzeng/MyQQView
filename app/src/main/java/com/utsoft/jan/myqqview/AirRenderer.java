package com.utsoft.jan.myqqview;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.utsoft.jan.myqqview.helper.MatrixHelper;
import com.utsoft.jan.myqqview.object.Mallet;
import com.utsoft.jan.myqqview.object.Table;
import com.utsoft.jan.myqqview.programs.ColorShaderProgram;
import com.utsoft.jan.myqqview.programs.TextureShaderProgram;
import com.utsoft.jan.myqqview.utils.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;

/**
 * Created by Administrator on 2019/8/23.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview
 */
public class AirRenderer implements GLSurfaceView.Renderer {

    private final Context context;

    private final float[] matrixFloats = new float[16];

    private final float[] modelMatrix = new float[16];

    private Table table;

    private Mallet mallet;

    private TextureShaderProgram textureShaderProgram;

    private ColorShaderProgram colorShaderProgram;

    private int texture;

    public AirRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f,0.0f,0.0f,0.0f);

        this.table = new Table();
        this.mallet = new Mallet();

        textureShaderProgram = new TextureShaderProgram(context);
        colorShaderProgram = new ColorShaderProgram(context);

        texture = TextureHelper.loadTexture(context,R.drawable.air_hockey_surface);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        MatrixHelper.perspectiveM(matrixFloats, 45, (float) width / (float) height, 1f, -10f);

        //modelMatrix
        Matrix.setIdentityM(modelMatrix,0);
        Matrix.translateM(modelMatrix,0,0,0,-2.5f);
        Matrix.rotateM(modelMatrix,0,-60f,1f,0f,0f);

        final float[] temp = new float[16];
        Matrix.multiplyMM(temp,0,matrixFloats,0,modelMatrix,0);
        System.arraycopy(temp,0,matrixFloats,0,temp.length);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);

        textureShaderProgram.useProgram();
        textureShaderProgram.setUniform(matrixFloats,texture);
        table.bindData(textureShaderProgram);
        table.draw();

        colorShaderProgram.useProgram();
        colorShaderProgram.setUniform(modelMatrix);
        mallet.bindData(colorShaderProgram);
        mallet.draw();
    }
}
