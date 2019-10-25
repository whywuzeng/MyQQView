//
// Created by Administrator on 2019/10/25.
//

#include "MagicProcessFilter.h"
#include "../../utils/OpenGLUtils.h"

MagicProcessFilter::MagicProcessFilter(AAssetManager *manager) : GPUImageFilter(manager) {}

void MagicProcessFilter::release() {
    deleteFrameBuffer();
}

void MagicProcessFilter::onDrawBefore() {
    GPUImageFilter::onDrawBefore();
    bindFrameTexture(pframe,fTextures,pFrender);
}

void MagicProcessFilter::onDrawAfter() {
    unbindFrameBuffer();
    GPUImageFilter::onDrawAfter();
}

void MagicProcessFilter::onChanged() {
    GPUImageFilter::onChanged();
    GPUImageFilter::createFrameBuffer();
}
