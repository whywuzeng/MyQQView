//
// Created by Administrator on 2019/10/24.
//

#include "CameraRender.h"
#include "../utils/OpenGLUtils.h"

CameraRender::CameraRender(ANativeWindow *window, AAssetManager *manager) : pEGLManager(
        new EGLManager(window)) {
    this->Assetmanager = manager;
}


CameraRender::~CameraRender() {
    if (this->Assetmanager) {
        delete this->Assetmanager;
        this->Assetmanager = nullptr;
    }

    if (this->pEGLManager) {
        delete this->pEGLManager;
        this->pEGLManager = nullptr;
    }

    if (this->oesImageFilter) {
        delete this->oesImageFilter;
        this->oesImageFilter = nullptr;
    }

}

GLuint CameraRender::create() {

    if (!pEGLManager->biuldContent()) {
        return -1;
    }

    oesImageFilter = new OESImageFilter(this->Assetmanager);
    if (oesImageFilter != nullptr) {
        oesImageFilter->create();
    }

    OESTextureId = getOESTextureId();

    //过滤文件filter 编写

}

void CameraRender::change(int width, int height) {
    if (oesImageFilter != nullptr) {
        oesImageFilter->surfaceChangeSize(width, height);
    }
}

void CameraRender::draw(float *matrix) {

    oesImageFilter->setMatrix(matrix);
    if (oesImageFilter != nullptr) {
        oesImageFilter->draw();
    }

    glFlush();
    pEGLManager->swapBuffer();
}

void CameraRender::stop() {
    pEGLManager->release();
    oesImageFilter->release();
}
