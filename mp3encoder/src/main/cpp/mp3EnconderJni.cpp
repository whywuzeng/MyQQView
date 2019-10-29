//
// Created by Administrator on 2019/10/29.
//
#include <jni.h>
#include <string>
#include <android/log.h>
#include "libmp3_encoder/Mp3Encoder.h"

#define LOG_TAG "mp3EnconderJni Jni"
#define ALOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define ALOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

Mp3Encoder *mp3Encoder = NULL;

extern "C"
JNIEXPORT jint JNICALL
Java_com_utsoft_jan_mp3encoder_Mp3Encoder_init(JNIEnv *env, jobject instance, jstring pcmPath_,
                                               jint audioChannels, jint bitRate, jint sampleRate,
                                               jstring mp3Path_) {
    const char *pcmPath = env->GetStringUTFChars(pcmPath_, 0);
    const char *mp3Path = env->GetStringUTFChars(mp3Path_, 0);

    // TODO
     mp3Encoder = new Mp3Encoder();
    int ret = mp3Encoder->init(pcmPath, mp3Path, audioChannels, bitRate, sampleRate);

    env->ReleaseStringUTFChars(pcmPath_, pcmPath);
    env->ReleaseStringUTFChars(mp3Path_, mp3Path);
    return ret;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_utsoft_jan_mp3encoder_Mp3Encoder_encode(JNIEnv *env, jobject instance) {

    // TODO
    if (NULL!=mp3Encoder)
    {
        mp3Encoder->encoder();
    }

}

extern "C"
JNIEXPORT void JNICALL
Java_com_utsoft_jan_mp3encoder_Mp3Encoder_destroy(JNIEnv *env, jobject instance) {

    // TODO
    if (NULL!=mp3Encoder)
    {
        mp3Encoder->destory();
        delete mp3Encoder;
        mp3Encoder = NULL;
    }
}