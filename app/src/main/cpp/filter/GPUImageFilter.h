//
// Created by Administrator on 2019/10/24.
//

#ifndef MYQQVIEW_GPUIMAGEFILTER_H
#define MYQQVIEW_GPUIMAGEFILTER_H

#include <GLES2/gl2.h>

#include <android/asset_manager.h>
#include <android/native_window.h>

class GPUImageFilter {
public:
    GPUImageFilter(AAssetManager *manager);

    void create();

    virtual const char * getVertexSource();
    virtual const char * getFragmentSource();
    virtual void onChanged();

    void surfaceChangeSize(GLint width,GLint height);

    void draw();

     float *pMatrix;

    void setMatrix(float matrix[]);

    void setInputTextureId(GLuint textureId);

    GLuint getOutPutTextureId ();
private:

    GLuint mOutputTextureId = 0;

private:

    void useProgram();

    void ViewPort(int x,int y,int width,int height);


protected:

    GLuint mProgram = 0;
    //vertex location
    GLint vPositionLocation = 0;

    GLint vCoordLocation = 0;

    GLint vMatrixLocation = 0;

    GLint vTextureLocation = 0;

    GLuint mInputTextureId = 0;

    AAssetManager *pManager;


    GLint width = 0;
    GLint height = 0;

    virtual void clear();

    void onSetExpandData();

    virtual void onBindTexture();

    void onDraw();

    virtual void onDrawBefore();

    virtual void onDrawAfter();

    //framebuffer 变量
    GLuint pFrender = 0;
     GLuint pframe = 0;
     GLuint fTextures = 0;

    void deleteFrameBuffer();
    void createFrameBuffer();
};



#endif //MYQQVIEW_GPUIMAGEFILTER_H
