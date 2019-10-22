//
// Created by Administrator on 2019/10/22.
//
#include <EGL/egl.h>
#include "EGLCore.h"
#include <android/native_window.h>

#ifndef MYQQVIEW_EGLMANAGER_H
#define MYQQVIEW_EGLMANAGER_H


class EGLManager {
public:
    EGLManager(ANativeWindow *window);
    ~EGLManager();
    EGLBoolean biuldContent();

    void swapBuffer();

    void release();

private:
    EGLCore *mEGLCore;
    ANativeWindow *mWindow;
    EGLSurface mEGLSurface;
};


#endif //MYQQVIEW_EGLMANAGER_H
