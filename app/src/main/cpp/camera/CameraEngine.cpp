//
// Created by Administrator on 2019/10/21.
//

#include "CameraEngine.h"
#include "../utils/OpenGLUtils.h"
#include <GLES2/gl2ext.h>

const static GLfloat VERTICES[] = {
        -1.0f, 1.0f,
        1.0f, 1.0f,
        -1.0f, -1.0f,
        1.0f, -1.0f
};

const static GLfloat TEX_COORDS[] = {
        0.0f, 1.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 0.0f
};

CameraEngine::CameraEngine(ANativeWindow *window):manager(new EGLManager(window)) {

}

CameraEngine::~CameraEngine() {
    if (manager){
        delete manager;
        manager = nullptr;
    }
}

int CameraEngine::create() {
    if (!manager->biuldContent())
    {
        return -1;
    }
    std::string *baseVer = readShaderFromAsset(mAssetManager, "base_fragText.glsl");
    std::string *baseFrag = readShaderFromAsset(mAssetManager, "base_vertext.glsl");

    //程序id 参考java代码
    mProgram = buildProgram(baseFrag->c_str(),baseVer->c_str());

    glGenTextures(1,&mTextureId);
    //绑定纹理
    glBindTexture(GL_TEXTURE_EXTERNAL_OES,mTextureId);
    //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色，少量计算，渲染比较快，但是效果差
    glTexParameterf(GL_TEXTURE_EXTERNAL_OES,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
    //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色，需要算法计算，用时相对变长，效果好
    glTexParameterf(GL_TEXTURE_EXTERNAL_OES,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
    //这里GL_TEXTURE_WRAP_S 纹理坐标是以S轴方向与T轴方向纹理（对应平面坐标x，y方向）
    glTexParameterf(GL_TEXTURE_EXTERNAL_OES,GL_TEXTURE_WRAP_S,GL_CLAMP_TO_EDGE);
    glTexParameterf(GL_TEXTURE_EXTERNAL_OES,GL_TEXTURE_WRAP_T,GL_CLAMP_TO_EDGE);

    //顶点数据
    uTexMatrixLocation = glGetUniformLocation(mProgram,"uTexMatrix");
    aPositionLocation = glGetAttribLocation(mProgram,"aPosition");
    aTextureCoordLocation = glGetAttribLocation(mProgram,"aTextureCoord");

    //frag 数据
    uTextureLocation = glGetUniformLocation(mProgram,"uTexture");

    delete baseVer;
    delete baseFrag;
    return mTextureId;
}



void CameraEngine::setAssertManager(AAssetManager *manager) {
    mAssetManager = manager;
}

void CameraEngine::draw(GLfloat *matrix) {
    glViewport(0,0,width,height);
    glClear(GL_COLOR_BUFFER_BIT);
    glUseProgram(mProgram);

    //激活纹理
    glActiveTexture(GL_TEXTURE0);

    //绑定纹理
    glBindTexture(GL_TEXTURE_EXTERNAL_OES,mTextureId);

    //加载纹理
    glUniform1i(uTextureLocation,0);

    //加载矩阵
    glUniformMatrix4fv(uTexMatrixLocation,1, GL_FALSE,matrix);

    //开启顶点数组缓冲区，第0个
    glEnableVertexAttribArray(aPositionLocation);
    glEnableVertexAttribArray(aTextureCoordLocation);

    glVertexAttribPointer(aPositionLocation, 2, GL_FLOAT, GL_FALSE, 0, VERTICES);
    checkError("aPositionLocation");

    glVertexAttribPointer(aTextureCoordLocation, 2, GL_FLOAT, GL_FALSE, 0, TEX_COORDS);
    checkError("aTextureCoordLocation");

    //画方形
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

    glDisableVertexAttribArray(aPositionLocation);
    glDisableVertexAttribArray(aTextureCoordLocation);

    glFlush();

    manager->swapBuffer();
}

void CameraEngine::stop() {
    glDeleteTextures(1, &mTextureId);
    glDeleteProgram(mProgram);
    manager->release();
}

void CameraEngine::reSize(GLsizei width1, GLsizei height1) {
    width = width1;
    height = height1;
}




