//
// Created by Administrator on 2019/10/22.
//
#include <GLES2/gl2.h>
#include <EGL/egl.h>
#include <android/native_window.h>

#ifndef MYQQVIEW_EGLCORE_H
#define MYQQVIEW_EGLCORE_H


class EGLCore {
public:
    EGLCore();

    ~EGLCore();

    EGLBoolean initEGLContent(EGLContext shareContent);

    EGLSurface createWindowSurface(ANativeWindow *window);

    EGLBoolean makeCurrent(EGLSurface eglSurface);

    void swapBuffer(EGLSurface surface);

    void release(EGLSurface surface);

private:
    EGLDisplay mEGLDisPlay;
    EGLContext mEGLContext;
    EGLConfig mEGLConfig;

    static const int EGL_RECORDABLE_ANDROID = 0x3142;
    static const int FLAG_RECORDABLE = 0x01;


};


#endif //MYQQVIEW_EGLCORE_H
