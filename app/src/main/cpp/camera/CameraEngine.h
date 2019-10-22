//
// Created by Administrator on 2019/10/21.
//
#include <android/asset_manager.h>
#include "../egl/EGLManager.h"
#include <android/native_window.h>


#ifndef MYQQVIEW_CAMERAENGNI_H
#define MYQQVIEW_CAMERAENGNI_H


class CameraEngine {
public:
    CameraEngine(ANativeWindow *window);
    ~CameraEngine();
    void setAssertManager(AAssetManager *manager);
    int create();

    void draw(GLfloat *matrix);

    void stop();

private:
    EGLManager *manager;
    AAssetManager *mAssetManager;
    GLint mProgram;
    GLuint mTextureId;
    GLint uTexMatrixLocation;
    GLint aPositionLocation;
    GLint aTextureCoordLocation;
    GLint uTextureLocation;
    GLsizei width;
    GLsizei height;
};


#endif //MYQQVIEW_CAMERAENGNI_H
