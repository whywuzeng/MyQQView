//
// Created by Administrator on 2019/10/24.
//

#include "GPUImageFilter.h"
#include "../utils/OpenGLUtils.h"
#include "../info/RendererInfo.h"

float matrix[] = {
        1,0,0,0,
        0,1,0,0,
        0,0,1,0,
        0,0,0,1
};

GPUImageFilter::GPUImageFilter( AAssetManager *manager) {
    this->pManager = manager;
    this->pMatrix = matrix;
}

void GPUImageFilter::create() {
    mProgram = buildProgram(getVertexSource(), getFragmentSource());
}


void GPUImageFilter::surfaceChangeSize(GLint width, GLint height) {
    this->width = width;
    this->height = height;
    onChanged();
}

void GPUImageFilter::draw() {
    clear();
    useProgram();
    onSetExpandData();
    onBindTexture();
    ViewPort(0, 0, width, height);
    onDrawBefore();
    onDraw();
    onDrawAfter();
}

void GPUImageFilter::setMatrix(float *smatrix) {
//    size_t n = sizeof(this->matrix) / sizeof(matrix[0]);
//    size_t tn = sizeof(smatrix) / sizeof(smatrix);
//    int i1 = (int) n;
//    int i2 = (int) tn;
//    if (i1 != i2) {
//
//    }
//    for (int i = 0; i < n; ++i) {
//        this->matrix[i] = smatrix[i];
//    }

    this->pMatrix = smatrix;
}

void GPUImageFilter::clear() {
    glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
}

void GPUImageFilter::useProgram() {
    glUseProgram(mProgram);
}

void GPUImageFilter::onSetExpandData() {
    glUniformMatrix4fv(vMatrixLocation, 1, GL_FALSE, pMatrix);
    checkError("vMatrixLocation");
}

void GPUImageFilter::onBindTexture() {
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, mInputTextureId);
    glUniform1i(vTextureLocation, 0);
    checkError("vTextureLocation");
}

GLuint GPUImageFilter::getOutPutTextureId() {
    if (fTextures>0)
    {
        return fTextures;
    }
    return mInputTextureId;
}

void GPUImageFilter::setInputTextureId(GLuint mTextureId) {
    GPUImageFilter::mInputTextureId = mTextureId;
}

void GPUImageFilter::ViewPort(int x, int y, int width, int height) {
    glViewport(x,y,width,height);
}

void GPUImageFilter::onDraw() {
    glEnableVertexAttribArray(vPositionLocation);
    glEnableVertexAttribArray(vCoordLocation);

    glVertexAttribPointer(vPositionLocation,2,GL_FLOAT, GL_FALSE,0,FULL_RECT_VERTEX);
    checkError("vPositionLocation");
    glVertexAttribPointer(vCoordLocation,2,GL_FLOAT, GL_FALSE,0,FULL_RECT_TEXTURE_VERTEX);
    checkError("vCoordLocation");

    glDrawArrays(GL_TRIANGLE_STRIP,0,4);

    glDisableVertexAttribArray(vPositionLocation);
    glDisableVertexAttribArray(vCoordLocation);


}

void GPUImageFilter::deleteFrameBuffer() {
      glDeleteRenderbuffers(1,&pFrender);
      glDeleteFramebuffers(1,&pframe);
      glDeleteTextures(1,&fTextures);
}

void GPUImageFilter::createFrameBuffer() {
    deleteFrameBuffer();
    glGenFramebuffers(1,&pframe);
    glGenRenderbuffers(1,&pFrender);
    glBindRenderbuffer(GL_RENDERBUFFER,pFrender);
    glRenderbufferStorage(GL_RENDERBUFFER,GL_DEPTH_COMPONENT16,width,height);
    glFramebufferRenderbuffer(GL_FRAMEBUFFER,GL_DEPTH_ATTACHMENT,GL_RENDERBUFFER,pFrender);
    glBindRenderbuffer(GL_RENDERBUFFER,0);
    fTextures = createFrameTexture(width,height);
}

const char *GPUImageFilter::getVertexSource() {
    std::string *vertex = readShaderFromAsset(pManager, "default_vertex.glsl");
    return vertex->c_str();
}

const char *GPUImageFilter::getFragmentSource() {
    std::string *pFragmentVextex = readShaderFromAsset(pManager, "default_fragment.glsl");
    return pFragmentVextex->c_str();
}

void GPUImageFilter::onChanged() {
    vPositionLocation = glGetAttribLocation(mProgram, "vPosition");
    vCoordLocation = glGetAttribLocation(mProgram, "vCoord");
    vMatrixLocation = glGetUniformLocation(mProgram, "vMatrix");
    vTextureLocation = glGetUniformLocation(mProgram, "vTexture");
}

void GPUImageFilter::onDrawBefore() {

}

void GPUImageFilter::onDrawAfter() {

    glBindTexture(GL_TEXTURE_2D, GL_NONE);
}









