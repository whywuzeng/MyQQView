//
// Created by Administrator on 2019/10/22.
//

#include "EGLManager.h"

EGLBoolean EGLManager::biuldContent() {

    if(!mEGLCore->initEGLContent(nullptr))
    {
        return EGL_FALSE;
    }

    EGLSurface surface = mEGLCore->createWindowSurface(mWindow);
    mEGLSurface = surface;
    if (!surface)
    {
        return EGL_FALSE;
    }

    return mEGLCore->makeCurrent(surface);
}

EGLManager::EGLManager(ANativeWindow *window):mEGLCore(new EGLCore()),mWindow(window),mEGLSurface(EGL_NO_SURFACE) {

}

EGLManager::~EGLManager() {
    if (mEGLCore)
    {
        delete mEGLCore;
        mEGLCore = nullptr;
    }

    if (mWindow)
    {
        ANativeWindow_release(mWindow);
        mWindow = nullptr;
    }

    mEGLSurface = EGL_NO_SURFACE;
}

void EGLManager::swapBuffer() {
    if (mEGLSurface == EGL_NO_SURFACE)
    {
        return;
    }
    mEGLCore->swapBuffer(mEGLSurface);
}

void EGLManager::release() {
    mEGLCore->release(mEGLSurface);
}

