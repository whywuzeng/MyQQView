//
// Created by Administrator on 2019/10/24.
//

#ifndef MYQQVIEW_CAMERARENDER_H
#define MYQQVIEW_CAMERARENDER_H

#include <android/asset_manager.h>
#include <android/native_window.h>
#include "EGLManager.h"
#include "../egl/EGLManager.h"
#include "../filter/OESImageFilter.h"


class CameraRender {

public:
    CameraRender(ANativeWindow *window,AAssetManager *manager);

    ~CameraRender();

    GLuint create();

    void change();

    void draw(float matrix[]);

private:
    EGLManager *pEGLManager;

    OESImageFilter *oesImageFilter;
};


#endif //MYQQVIEW_CAMERARENDER_H
