package com.utsoft.jan.myqqview;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.utsoft.jan.myqqview.helper.MatrixHelper;
import com.utsoft.jan.myqqview.object.Mallet;
import com.utsoft.jan.myqqview.object.Puck;
import com.utsoft.jan.myqqview.object.Table;
import com.utsoft.jan.myqqview.programs.ColorShaderProgram;
import com.utsoft.jan.myqqview.programs.TextureShaderProgram;
import com.utsoft.jan.myqqview.utils.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

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

    private Puck mPuck;

    private TextureShaderProgram textureShaderProgram;

    private ColorShaderProgram colorShaderProgram;

    private int texture;

    private final float[] viewMatrix = new float[16];

    private final float[] viewProjectionMatrix = new float[16];

    private final float[] modeViewProjectionMatrix = new float[16];

    public AirRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f,0.0f,0.0f,0.0f);

        this.table = new Table();
        this.mallet = new Mallet(0.08f,0.15f,32);
        this.mPuck = new Puck(0.06f,0.02f,32);

        textureShaderProgram = new TextureShaderProgram(context);
        colorShaderProgram = new ColorShaderProgram(context);

        texture = TextureHelper.loadTexture(context,R.drawable.air_hockey_surface);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //MatrixHelper.perspectiveM(matrixFloats, 45, (float) width / (float) height, 1f, -10f);
        //
        ////modelMatrix
        //Matrix.setIdentityM(modelMatrix,0);
        //Matrix.translateM(modelMatrix,0,0,0,-2.5f);
        //Matrix.rotateM(modelMatrix,0,-60f,1f,0f,0f);
        //
        //final float[] temp = new float[16];
        //Matrix.multiplyMM(temp,0,matrixFloats,0,modelMatrix,0);
        //System.arraycopy(temp,0,matrixFloats,0,temp.length);

        glViewport(0,0,width,height);
        MatrixHelper.perspectiveM(matrixFloats, 45, (float) width / (float) height, 1f, -10f);
        Matrix.setLookAtM(viewMatrix,0,0f,1.2f,2.2f,0f,0f,0f,0f,1f,0f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);

        Matrix.multiplyMM(viewProjectionMatrix,0,matrixFloats,0,viewMatrix,0);

        positionTableInScene();
        textureShaderProgram.useProgram();
        textureShaderProgram.setUniform(matrixFloats,texture);
        table.bindData(textureShaderProgram);
        table.draw();

        positionObjectInScene(0,mallet.height/2,-0.4f);
        colorShaderProgram.useProgram();
        colorShaderProgram.setUniform(matrixFloats,1f,0f,0f);
        mallet.bindData(colorShaderProgram);
        mallet.draw();

        positionObjectInScene(0,mallet.height/2,-0.4f);
        colorShaderProgram.setUniform(matrixFloats,0f,0f,1f);
        mallet.draw();

        positionObjectInScene(0f,mPuck.height/2f,0.4f);
        colorShaderProgram.setUniform(matrixFloats,0.8f,0.8f,1f);
        mPuck.bindData(colorShaderProgram);
        mPuck.draw();

    }

    private void positionTableInScene(){
        Matrix.setIdentityM(modelMatrix,0);
        Matrix.rotateM(modelMatrix,0,-90f,1f,0f,0f);
        Matrix.multiplyMM(modeViewProjectionMatrix,0,matrixFloats,0,viewMatrix,0);
    }

    private void positionObjectInScene(float x,float y,float z){
        Matrix.setIdentityM(modelMatrix,0);
        Matrix.translateM(modelMatrix,0,x,y,z);
        Matrix.multiplyMM(modeViewProjectionMatrix,0,viewProjectionMatrix,0,modelMatrix,0);
    }
}
