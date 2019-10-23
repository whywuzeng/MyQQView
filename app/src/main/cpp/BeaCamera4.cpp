//
// Created by Administrator on 2019/10/21.
//
#include <jni.h>
#include <string>
#include <android/native_window_jni.h>
#include <android/asset_manager_jni.h>
#include "camera/CameraEngine.h"



extern "C" {

CameraEngine *glCamera = nullptr;

JNIEXPORT void JNICALL
Java_com_utsoft_jan_myqqview_utils_OpenGLJinLib_camereRelease(JNIEnv *env, jclass type) {

    // TODO
    if (glCamera)
    {
        glCamera->stop();
    }
}

JNIEXPORT void JNICALL
Java_com_utsoft_jan_myqqview_utils_OpenGLJinLib_cameroDraw(JNIEnv *env, jclass type,
                                                           jfloatArray matrix_) {
    jfloat *matrix = env->GetFloatArrayElements(matrix_, NULL);

    // TODO
    if(glCamera)
    {
       glCamera->draw(matrix);
    }

    env->ReleaseFloatArrayElements(matrix_, matrix, 0);
}

JNIEXPORT jstring JNICALL
Java_com_utsoft_jan_myqqview_utils_OpenGLJinLib_sayHello(JNIEnv *env, jclass type) {

    // TODO
    std::string hello = "hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_utsoft_jan_myqqview_utils_OpenGLJinLib_stringHello(JNIEnv *env, jclass type) {

    // TODO

    return env->NewStringUTF("new String");
}


//初始化相机 操作 返回 textureHandle ID
extern "C"
JNIEXPORT jint JNICALL
Java_com_utsoft_jan_myqqview_utils_OpenGLJinLib_cameroInit(JNIEnv *env, jclass type,
                                                           jobject surface, jint width, jint height,
                                                           jobject assetManager) {

    if (glCamera) {
        glCamera->stop();
        delete glCamera;
        glCamera = nullptr;
    }

    // TODO
    ANativeWindow *pWindow = ANativeWindow_fromSurface(env, surface);
    AAssetManager *pManager = AAssetManager_fromJava(env, assetManager);

    glCamera = new CameraEngine(pWindow);
    glCamera->setAssertManager(pManager);
    return glCamera->create();
}

}