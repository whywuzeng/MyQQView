//
// Created by Administrator on 2019/10/21.
//
#include <jni.h>
#include <string>

extern "C"

JNIEXPORT jstring JNICALL
Java_com_utsoft_jan_myqqview_utils_OpenGLJinLib_sayHello(JNIEnv *env, jclass type ) {

    // TODO
    std::string hello ="hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_utsoft_jan_myqqview_utils_OpenGLJinLib_stringHello(JNIEnv *env, jclass type) {

    // TODO

    return env->NewStringUTF("new String");
}


//初始化相机 操作
extern "C"
JNIEXPORT jint JNICALL
Java_com_utsoft_jan_myqqview_utils_OpenGLJinLib_cameroInit(JNIEnv *env, jclass type,
                                                           jobject surface, jint width, jint height,
                                                           jobject assetManager) {

    // TODO


}