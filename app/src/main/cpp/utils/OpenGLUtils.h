//
// Created by Administrator on 2019/10/21.
//

#ifndef MYQQVIEW_OPENGLUTILS_H
#define MYQQVIEW_OPENGLUTILS_H

#include <GLES2/gl2.h>
#include <android/asset_manager.h>
#include <string>

GLuint loadTextureFromAssets(AAssetManager *manager, const char *fileName);
std::string* readShaderFromAsset(AAssetManager *manager, const char *fileName);
GLuint buildProgram(const char* verTex,const char* FragTex);

void checkError(const char *outString);
GLuint complieShader(GLenum type,const char *shaderCode);

#endif //MYQQVIEW_OPENGLUTILS_H
