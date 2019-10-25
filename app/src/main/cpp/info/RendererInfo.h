//
// Created by Administrator on 2019/10/24.
//

#ifndef MYQQVIEW_RENDERERINFO_H
#define MYQQVIEW_RENDERERINFO_H


#include <GLES2/gl2.h>

const static GLfloat FULL_RECT_VERTEX[] ={
        -1.0f,-1.0f,
        1.0f,-1.0f,
        -1.0f,1.0f,
        1.0f,1.0f,
};


const static float FULL_RECT_TEXTURE_VERTEX[] = {
        0.0f,0.0f,
        1.0f,0.0f,
        0.0f,1.0f,
        1.0f,1.0f
};



#endif //MYQQVIEW_RENDERERINFO_H
