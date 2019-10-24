//
// Created by Administrator on 2019/10/24.
//

#include "CameraRender.h"

CameraRender::CameraRender(ANativeWindow *window, AAssetManager *manager):pEGLManager(new EGLManager(window)) {

}


CameraRender::~CameraRender() {

}

GLuint CameraRender::create() {

    if (!pEGLManager->biuldContent())
    {
        return -1;
    }



}

void CameraRender::change() {

}

void CameraRender::draw(float *matrix) {

}
