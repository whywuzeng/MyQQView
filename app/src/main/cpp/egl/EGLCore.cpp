//
// Created by Administrator on 2019/10/22.
//

#include "EGLCore.h"
#include <android/log.h>
#include <EGL/eglext.h>

#define LOG_TAG "C++ EGLCore"
#define ALOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define ALOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)


EGLCore::EGLCore():mEGLDisPlay(EGL_NO_DISPLAY),mEGLContext(EGL_NO_CONTEXT),mEGLConfig(nullptr) {
}

EGLCore::~EGLCore() {
    mEGLDisPlay = EGL_NO_DISPLAY;
    mEGLContext = EGL_NO_CONTEXT;
    mEGLConfig = nullptr;
}

EGLBoolean EGLCore::initEGLContent(EGLContext shareContent) {
    if (shareContent == nullptr)
    {
        shareContent = EGL_NO_CONTEXT;
    }

    //先创建一个默认的Display
    mEGLDisPlay = eglGetDisplay(EGL_DEFAULT_DISPLAY);

    if (mEGLDisPlay == EGL_NO_DISPLAY)
    {
        ALOGE("eglGetDisplay(EGL_DEFAULT_DISPLAY) is error");
        return GL_FALSE;
    }

    EGLint major = 0;
    EGLint minor = 0;
    if (!eglInitialize(mEGLDisPlay,&major,&minor))
    {
        ALOGE("eglInitialize is error");
        mEGLDisPlay = nullptr;
        return GL_FALSE;
    }

    if (mEGLContext != EGL_NO_CONTEXT)
    {
        return GL_FALSE;
    }

    //简便写法

    //颜色使用565，读写类型需要egl扩展
    EGLint attribList[] = {
            EGL_RED_SIZE,5,
            EGL_GREEN_SIZE,6,
            EGL_BLUE_SIZE,5,
            EGL_RENDERABLE_TYPE,EGL_OPENGL_ES3_BIT_KHR, //渲染类型，为相机扩展类型
            EGL_SURFACE_TYPE,EGL_WINDOW_BIT,  //绘图类型，
            EGL_NONE
    };

    EGLint num_config = 0;
    //让EGL推荐匹配的EGLConfig  eglChooseConfig
    if (!eglChooseConfig(mEGLDisPlay,attribList,&mEGLConfig,1,&num_config))
    {
        ALOGE("eglChooseConfig failed: %d",eglGetError());
        return GL_FALSE;
    }

    if (num_config<1)
    {
        ALOGE("can't find EGLconfig this number less than one");
        return GL_FALSE;
    }

    EGLint alist[] ={
            EGL_CONTEXT_CLIENT_VERSION,2,EGL_NONE
    };

    mEGLContext = eglCreateContext(mEGLDisPlay,mEGLConfig,mEGLContext,alist);

    if (mEGLContext == EGL_NO_CONTEXT)
    {
        ALOGE("eglCreateContext is failed");
        return GL_FALSE;
    }

    EGLint version = 0;

    eglQueryContext(mEGLDisPlay,mEGLContext,EGL_CONTEXT_CLIENT_VERSION,&version);

    ALOGE("EGLContent is created client version is %d",version);
}

EGLSurface EGLCore::createWindowSurface(ANativeWindow *window) {

    EGLint  format = 0;

    if (!eglGetConfigAttrib(mEGLDisPlay,mEGLConfig,EGL_NATIVE_VISUAL_ID,&format)){
        ALOGE("eglGetConfigAttrib is failed %d",eglGetError());
        return nullptr;
    }

    ANativeWindow_setBuffersGeometry(window,0,0,format);


   EGLSurface surface = eglCreateWindowSurface(mEGLDisPlay, mEGLConfig, window, 0);
    
    if (surface == EGL_NO_SURFACE)
    {
        ALOGE("eglCreateWindowSurface failed: %d",eglGetError());
        return nullptr;
    }
    
    return surface;
}

EGLBoolean EGLCore::makeCurrent(EGLSurface eglSurface) {
    if (mEGLDisPlay == EGL_NO_DISPLAY)
    {
        ALOGE("mEGLDisPlay is EGL_NO_DISPLAY");
        return EGL_FALSE;
    }

    if(eglMakeCurrent(mEGLDisPlay,eglSurface,eglSurface,mEGLContext))
    {
        ALOGE("eglMakeCurrent failed: %d",eglGetError());
        return EGL_FALSE;
    }
}

/**
 * 现在只使用单缓冲绘制
 */
void EGLCore::swapBuffer(EGLSurface surface) {
    //双缓冲绘图，原来是检测出前台display和后台缓冲的差别的dirty区域，然后再区域替换buffer
    //1）首先计算非dirty区域，然后将非dirty区域数据从上一个buffer拷贝到当前buffer；
    //2）完成buffer内容的填充，然后将previousBuffer指向buffer，同时queue buffer。
    //3）Dequeue一块新的buffer，并等待fence。如果等待超时，就将buffer cancel掉。
    //4）按需重新计算buffer
    //5）Lock buffer，这样就实现page flip，也就是swapbuffer

    eglSwapBuffers(mEGLDisPlay,surface);

}

void EGLCore::release(EGLSurface surface) {
    eglDestroySurface(mEGLDisPlay,surface);
    eglMakeCurrent(mEGLDisPlay,EGL_NO_SURFACE,EGL_NO_SURFACE,EGL_NO_CONTEXT);
    eglDestroyContext(mEGLDisPlay,mEGLContext);
}







