package com.utsoft.jan.myqqview;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.utsoft.jan.myqqview.helper.MatrixHelper;
import com.utsoft.jan.myqqview.object.Mallet;
import com.utsoft.jan.myqqview.object.Puck;
import com.utsoft.jan.myqqview.object.Table;
import com.utsoft.jan.myqqview.programs.ColorShaderProgram;
import com.utsoft.jan.myqqview.programs.TextureShaderProgram;
import com.utsoft.jan.myqqview.utils.Geometry;
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
    private static final String TAG = "AirRenderer";

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

    private boolean malletPressed = false;

    private Geometry.Point blueMalletPosition;

    //抵消视图矩阵 和 投影矩阵的效果
    private final float[] invertedViewProjectionMatrix = new float[16];

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

        //给木追的位置初始化一个值
        blueMalletPosition = new Geometry.Point(0f, mallet.height / 2f, 0.4f);

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

        //整个视角的 viewprojectionMatrix 矩阵
        Matrix.multiplyMM(viewProjectionMatrix,0,matrixFloats,0,viewMatrix,0);

        //抵消inverted放到这个矩阵
        Matrix.invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);

        positionTableInScene();
        textureShaderProgram.useProgram();
        textureShaderProgram.setUniform(modeViewProjectionMatrix, texture);
        table.bindData(textureShaderProgram);
        table.draw();

        positionObjectInScene(0,mallet.height/2,-0.4f);
        colorShaderProgram.useProgram();
        colorShaderProgram.setUniform(modeViewProjectionMatrix, 1f, 0f, 0f);
        mallet.bindData(colorShaderProgram);
        mallet.draw();

        //positionObjectInScene(0,mallet.height/2,-0.4f);
        positionObjectInScene(blueMalletPosition.x,blueMalletPosition.y,blueMalletPosition.z);
        colorShaderProgram.setUniform(modeViewProjectionMatrix, 0f, 0f, 1f);
        mallet.draw();

        positionObjectInScene(0f,mPuck.height/2f,0.4f);
        colorShaderProgram.setUniform(modeViewProjectionMatrix, 0.8f, 0.8f, 1f);
        mPuck.bindData(colorShaderProgram);
        mPuck.draw();

    }

    private void positionTableInScene(){
        Matrix.setIdentityM(modelMatrix,0);
        Matrix.rotateM(modelMatrix,0,-90f,1f,0f,0f);
        Matrix.multiplyMM(modeViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
    }

    private void positionObjectInScene(float x,float y,float z){
        Matrix.setIdentityM(modelMatrix,0);
        Matrix.translateM(modelMatrix,0,x,y,z);
        Matrix.multiplyMM(modeViewProjectionMatrix,0,viewProjectionMatrix,0,modelMatrix,0);
    }

    public void handleTouchPress(float normalizedX, float normalizedY) {
        Log.i(TAG, "handleTouchPress normalizedX: " + normalizedX);
        Log.i(TAG, "handleTouchPress normalizedY: " + normalizedY);

        final Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);

        final Geometry.Sphere malletBoundingSphere = new Geometry.Sphere(new Geometry.Point(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z), mallet.height / 2f);

        malletPressed = Geometry.intersects(malletBoundingSphere, ray);

    }

    //Ray 是啥
    private Geometry.Ray convertNormalized2DPointToRay(float normalizedX, float normalizedY) {
        final float[] nearPointNdc = {normalizedX, normalizedY, -1, 1};
        final float[] farPointNdc = {normalizedX, normalizedY, 1, 1};

        final float[] nearPointWorld = new float[4];
        final float[] farPointWorld = new float[4];

        Matrix.multiplyMV(nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);

        Matrix.multiplyMV(farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);

        divideByW(nearPointWorld);
        divideByW(farPointWorld);

        final Geometry.Point nearPointRay = new Geometry.Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);

        final Geometry.Point farPointRay = new Geometry.Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);

        return new Geometry.Ray(nearPointRay, Geometry.Vector.vectorBetween(nearPointRay, farPointRay));
    }

    private void divideByW(float[] nearPointWorld) {
        nearPointWorld[0] /= nearPointWorld[3];
        nearPointWorld[1] /= nearPointWorld[3];
        nearPointWorld[2] /= nearPointWorld[3];
    }

    public void handleTouchMove(float normalizedX, float normalizedY) {
        Log.i(TAG, "handleTouchPress normalizedX: " + normalizedX);
        Log.i(TAG, "handleTouchPress normalizedY: " + normalizedY);

        if (malletPressed) {
            final Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);

            final Geometry.Plane plane = new Geometry.Plane(new Geometry.Point(0, 0, 0), new Geometry.Vector(0, 1, 0));

            final Geometry.Point touchPoint = Geometry.intersectionPoint(ray, plane);

            blueMalletPosition = new Geometry.Point(touchPoint.x, mallet.height / 2f, touchPoint.z);
        }
    }
}
