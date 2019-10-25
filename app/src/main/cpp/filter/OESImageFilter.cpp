//
// Created by Administrator on 2019/10/24.
//

#include "OESImageFilter.h"
#include "../utils/OpenGLUtils.h"
#include <GLES2/gl2ext.h>

OESImageFilter::OESImageFilter(AAssetManager *manager) : GPUImageFilter(manager) {

}

const char *OESImageFilter::getVertexSource() {
    std::string *vertex = readShaderFromAsset(pManager, "base_vertext.glsl");
    return vertex->c_str();
}

const char *OESImageFilter::getFragmentSource() {
    std::string *pFragmentVextex = readShaderFromAsset(pManager, "base_fragText.glsl");
    return pFragmentVextex->c_str();
}

void OESImageFilter::onBindTexture() {
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, mInputTextureId);
    glUniform1f(vTextureLocation, 0);
}

void OESImageFilter::onDrawBefore() {
    bindFrameTexture(pframe,fTextures,pFrender);
}

void OESImageFilter::onDrawAfter() {
    unbindFrameBuffer();
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, GL_NONE);
}

void OESImageFilter::onChanged() {

    vPositionLocation = glGetAttribLocation(mProgram, "aPosition");
    vCoordLocation = glGetAttribLocation(mProgram, "aTextureCoord");
    vMatrixLocation = glGetUniformLocation(mProgram, "uTexMatrix");
    vTextureLocation = glGetUniformLocation(mProgram, "vTexture");

    GPUImageFilter::createFrameBuffer();
}

void OESImageFilter::release() {
    deleteFrameBuffer();
}




