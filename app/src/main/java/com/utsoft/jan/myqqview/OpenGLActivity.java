package com.utsoft.jan.myqqview;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.utsoft.jan.myqqview.helper.MatrixHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES10.GL_POINTS;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

/**
 * Created by Administrator on 2019/8/22.
 * <p>
 * by author wz
 * 第三章结束
 * <p>
 * com.utsoft.jan.myqqview
 */
public class OpenGLActivity extends AppCompatActivity {

    private static final String TAG = "OpenGLActivity";

    private GLSurfaceView mGlSurfaceView;

    private boolean isSender = false;
    private ActivityManager activityManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_opengl);
        mGlSurfaceView = new GLSurfaceView(this);
        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo info = activityManager.getDeviceConfigurationInfo();
        final boolean isSupportEs2 = info.reqGlEsVersion >= 0x20000;
        final AirRenderer airRenderer = new AirRenderer(this);
        if (isSupportEs2) {
            mGlSurfaceView.setEGLContextClientVersion(2);

            mGlSurfaceView.setRenderer(airRenderer);
            isSender = true;
        }
        else {
            Log.i(TAG, "onCreate: 不支持");
        }
        mGlSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event != null) {
                    final float normalizedX = (event.getX() / (float) v.getWidth()) * 2 - 1;
                    final float normalizedY = -((event.getY() / (float) v.getHeight()) * 2 - 1);

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        mGlSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                airRenderer.handleTouchPress(normalizedX,normalizedY);
                            }
                        });
                    }
                    else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        mGlSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                airRenderer.handleTouchMove(normalizedX,normalizedY);
                            }
                        });
                    }
                    return true;
                }
                return false;
            }
        });

        setContentView(mGlSurfaceView);
    }

    class FirstOpenGLProjectRenderer implements GLSurfaceView.Renderer {

        private static final int position_component_count = 2;

        public static final int bytes_per_float = 4;

        private final FloatBuffer verData;

        private final Context mContext;

        private int program;

        private static final String A_POSITION = "a_Position";

        private int aPositionLocation;

        private static final String A_Color = "a_Color";

        private int aColorLocation;

        private static final int Color_Component_Count = 3;

        private static final int STRIDE = (position_component_count+Color_Component_Count)*bytes_per_float;

        private static final String u_Matrix = "u_Matrix";

        private int uMatrixLocation;

        private  float[] matrixFloats = new float[16];

//        private  float[] tableVer ={
//                -0.5f, -0.5f,
//                0.5f, 0.5f,
//                -0.5f, 0.5f,
//
//                -0.5f, -0.5f,
//                0.5f, -0.5f,
//                0.5f, 0.5f,
//
//                -0.5f, 0f,
//                0.5f, 0f,
//
//                0f, -0.25f,
//                0f, 0.25f,
//        };

        private  float[] tableVer ={
                0f, 0f,  1f, 1f, 1f,
                -0.5f, -0.8f,  0.7f, 0.7f, 0.7f,
                0.5f, -0.8f,  0.7f, 0.7f, 0.7f,

                0.5f, 0.8f,  0.7f, 0.7f, 0.7f,
                -0.5f, 0.8f,  0.7f, 0.7f, 0.7f,
                -0.5f, -0.8f,  0.7f, 0.7f, 0.7f,

                -0.5f, 0f,  1f, 0f, 0f,
                0.5f, 0f,  0f, 0f, 1f,

                0f, -0.25f,  0f, 0f, 1f,
                0f, 0.25f,  1f, 0f, 0f,
        };

        private  float[] copy;

        private Random random = new Random();

        private final float[] modelMatrix = new float[16];

        public FirstOpenGLProjectRenderer(Context context) {
            this.mContext = context;

            final float[] initCircle = initCircle();

             copy = Arrays.copyOf(tableVer, tableVer.length + initCircle.length);

            System.arraycopy(initCircle,0,copy,tableVer.length,initCircle.length);

            verData = ByteBuffer
                    .allocateDirect(tableVer.length * bytes_per_float)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            verData.put(tableVer);
        }

        private int vertCount = 200;

        private float[] initCircle(){
            final float[] floats = new float[200];
            final float delta = (float) (2.0f * Math.PI / vertCount);

            WindowManager wm1 = (WindowManager) mContext
                    .getSystemService(Context.WINDOW_SERVICE);
            int width = wm1.getDefaultDisplay().getWidth();
            int height = wm1.getDefaultDisplay().getHeight();

            float a = 0.2f;
            float b = a * width / height;

            for (int i = 0; i < vertCount; i+=2) {
                  float x = (float) (a * Math.cos(delta*i));
                  float y = (float) (b*Math.sin(delta*i));

                floats[i] = x;
                floats[i+1] = y;
            }

            return floats;
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

            String fragmentshape = TextResourceReader.readTextResource(mContext, R.raw.simple_fragment_shader);

            String vertexShape = TextResourceReader.readTextResource(mContext, R.raw.simple_vertex_shader);

            final int fragmentShapeId = ShapeHelper.complieFragmentShader(fragmentshape);
            final int vertexShaderId = ShapeHelper.complieVertexShader(vertexShape);

            program = ShapeHelper.linkProgram(vertexShaderId,fragmentShapeId);

            if (LoggerConfig.ON)
            {
                ShapeHelper.validateProgram(program);
            }

            glUseProgram(program);

            //得到这个位置
//            uColorLoacation = glGetUniformLocation(program,U_COLOR);
            aColorLocation =  glGetAttribLocation(program,A_Color);
            aPositionLocation = glGetAttribLocation(program,A_POSITION);

            uMatrixLocation = glGetUniformLocation(program,u_Matrix);

            verData.position(0);
            glVertexAttribPointer(aPositionLocation,position_component_count,GL_FLOAT,false,STRIDE,verData);
            glEnableVertexAttribArray(aPositionLocation);

            verData.position(position_component_count);
            glVertexAttribPointer(aColorLocation,Color_Component_Count,GL_FLOAT,false,STRIDE,verData);
            glEnableVertexAttribArray(aColorLocation);

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            glViewport(0, 0, width, height);
            //final float rate =  width> height?(float)width/height:(float)height/width;
            //if (width>height)
            //{
            //    Matrix.orthoM(matrixFloats,0,-rate,rate,-1,1,-1,1);
            //}else {
            //    Matrix.orthoM(matrixFloats,0,-1,1,-rate,rate,-1,1);
            //}

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
            //float changex = (random.nextFloat()-1);
            //Matrix.translateM(matrixFloats,0,changex,0,0);
//            glUniform4f(uColorLoacation,1.0f,1.0f,1.0f,1.0f);
            glUniformMatrix4fv(uMatrixLocation,1,false,matrixFloats,0);
            glDrawArrays(GL_TRIANGLE_FAN,0,6);
//            glUniform4f(uColorLoacation,1.0f,0.0f,0.0f,1.0f);
            glDrawArrays(GL_LINES,6,2);
//            glUniform4f(uColorLoacation,0.0f,0.0f,1.0f,1.0f);
            glDrawArrays(GL_POINTS,8,1);
//            glUniform4f(uColorLoacation,1.0f,0.0f,0.0f,1.0f);
            glDrawArrays(GL_POINTS,9,1);

            /**
             * 画圆
             */
 //            glUniform4f(uColorLoacation,0.0f,0.0f,0.0f,1.0f);
//            glDrawArrays(GL_POINTS,10,copy.length);


        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        if (isSender) {
            mGlSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSender) {
            mGlSurfaceView.onResume();
        }
    }
}
