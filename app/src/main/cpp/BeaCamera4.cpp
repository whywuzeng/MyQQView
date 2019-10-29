//
// Created by Administrator on 2019/10/21.
//
#include <jni.h>
#include <string>
#include <android/native_window_jni.h>
#include <android/asset_manager_jni.h>
#include <android/log.h>
#include "camera/CameraEngine.h"
#include "camera/CameraRender.h"

#define LOG_TAG "C++ BeaCamera4 Jni"
#define ALOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define ALOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)


extern "C" {

CameraEngine *glCamera = nullptr;

CameraRender *cameraRender = nullptr;

//过滤相机的初始化
JNIEXPORT jint JNICALL
Java_com_utsoft_jan_myqqview_utils_OpenGLJinLib_cameraFilterInite(JNIEnv *env, jclass type,
                                                                  jobject surface, jint width,
                                                                  jint height,
                                                                  jobject assetManager) {

    // TODO
    if (cameraRender) {
        //销毁
        cameraRender->stop();
        delete cameraRender;
        cameraRender = nullptr;
    }

    ANativeWindow *pWindow = ANativeWindow_fromSurface(env, surface);

    AAssetManager *pManager = AAssetManager_fromJava(env, assetManager);
    cameraRender = new CameraRender(pWindow, pManager);
    GLuint textureId = cameraRender->create();
    cameraRender->change(width, height);
    return textureId;

}
//过滤相机的draw
JNIEXPORT void JNICALL
Java_com_utsoft_jan_myqqview_utils_OpenGLJinLib_cameraFilterDraw(JNIEnv *env, jclass type,
                                                                 jfloatArray matrix_) {
    jfloat *matrix = env->GetFloatArrayElements(matrix_, NULL);

    // TODO
    if(!cameraRender)
    {
        ALOGE("cameraRender obj is null");
        return;
    }
    cameraRender->draw(matrix);

    env->ReleaseFloatArrayElements(matrix_, matrix, 0);
}

//过滤相机的销毁
JNIEXPORT void JNICALL
Java_com_utsoft_jan_myqqview_utils_OpenGLJinLib_cameraFilterRelease(JNIEnv *env, jclass type) {

    // TODO
    if(cameraRender)
    {
        ALOGE("cameraRender obj is null");
        return;
    }

    cameraRender->stop();
    delete cameraRender;
    cameraRender = nullptr;

}

JNIEXPORT void JNICALL
Java_com_utsoft_jan_myqqview_utils_OpenGLJinLib_camereRelease(JNIEnv *env, jclass type) {

    // TODO
    if (glCamera) {
        glCamera->stop();
    }
}

JNIEXPORT void JNICALL
Java_com_utsoft_jan_myqqview_utils_OpenGLJinLib_cameroDraw(JNIEnv *env, jclass type,
                                                           jfloatArray matrix_) {
    jfloat *matrix = env->GetFloatArrayElements(matrix_, NULL);

    // TODO
    if (glCamera) {
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
    glCamera->reSize(width, height);
    return glCamera->create();
}


}