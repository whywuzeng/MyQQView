//
// Created by Administrator on 2019/10/25.
//

#include "MagicNoFilter.h"

MagicNoFilter::MagicNoFilter(AAssetManager *manager) : GPUImageFilter(manager) {

}

void MagicNoFilter::release() {
    deleteFrameBuffer();
}

void MagicNoFilter::clear() {
    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
}

