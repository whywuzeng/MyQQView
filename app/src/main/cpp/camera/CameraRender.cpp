//
// Created by Administrator on 2019/10/24.
//

#include "CameraRender.h"
#include "../utils/OpenGLUtils.h"

CameraRender::CameraRender(ANativeWindow *window, AAssetManager *manager) : pEGLManager(
        new EGLManager(window)) {
    this->aAssetManager = manager;
}


CameraRender::~CameraRender() {
        aAssetManager = nullptr;
    if (this->pEGLManager) {
        delete this->pEGLManager;
        this->pEGLManager = nullptr;
    }

    if (this->oesImageFilter) {
        delete this->oesImageFilter;
        this->oesImageFilter = nullptr;
    }

    if (this->magicNoFilter)
    {
        delete this->magicNoFilter;
        this->magicNoFilter = nullptr;
    }

    if (magicProcessFilter)
    {
        delete magicProcessFilter;
        magicProcessFilter = nullptr;
    }
}

GLuint CameraRender::create() {

    if (!pEGLManager->biuldContent()) {
        return -1;
    }

    oesImageFilter = new OESImageFilter(this->aAssetManager);
    if (oesImageFilter != nullptr) {
        oesImageFilter->create();
    }

    OESTextureId = getOESTextureId();

    magicProcessFilter = new MagicProcessFilter(aAssetManager);
    if (magicProcessFilter!= nullptr)
    {
        magicProcessFilter->create();
    }

    //过滤文件filter 编写
    magicNoFilter = new MagicNoFilter(this->aAssetManager);
    if (magicNoFilter!= nullptr)
    {
        magicNoFilter->create();
    }

    return OESTextureId;
}

void CameraRender::change(int width, int height) {
    if (oesImageFilter != nullptr) {
        oesImageFilter->surfaceChangeSize(width, height);
    }

    if (magicNoFilter!= nullptr)
    {
        magicNoFilter->surfaceChangeSize(width,height);
    }

    if (magicProcessFilter!= nullptr)
    {
        magicProcessFilter->surfaceChangeSize(width,height);
    }
}

void CameraRender::draw(float *matrix) {

    //清屏
    glClearColor(0,0,0,0);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    oesImageFilter->setMatrix(matrix);
    oesImageFilter->setInputTextureId(OESTextureId);
    if (oesImageFilter != nullptr) {
        oesImageFilter->draw();
    }

//    if (magicProcessFilter!= nullptr)
//    {
//        magicProcessFilter->setInputTextureId(oesImageFilter->getOutPutTextureId());
//        magicProcessFilter->draw();
//    }

    if (magicNoFilter!= nullptr)
    {
        magicNoFilter->setInputTextureId(oesImageFilter->getOutPutTextureId());
        magicNoFilter->draw();
    }

    glFlush();
    pEGLManager->swapBuffer();
}

void CameraRender::stop() {
    pEGLManager->release();
    oesImageFilter->release();
    magicNoFilter->release();
    magicNoFilter->release();
}
