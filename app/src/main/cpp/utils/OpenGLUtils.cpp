//
// Created by Administrator on 2019/10/21.
//

#include "OpenGLUtils.h"
#include <android/log.h>

#define LOG_TAG "C++ OpenGLUtils"
#define ALOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define ALOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)


GLuint loadTextureFromAssets(AAssetManager *manager, const char *fileName) {
    GLuint texturesHandle = 0;
    glGenTextures(1, &texturesHandle);

    if (texturesHandle != 0) {
        glBindTexture(GL_TEXTURE_2D, texturesHandle);
        //纹理放大缩小使用线性插值
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        //超出的部份会重复纹理坐标的边缘，产生一种边缘被拉伸的效果，s/t相当于x/y轴坐标
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

    }

    //从manager加载图片，然后创建好textureID 返回

    return 0;
}

std::string *readShaderFromAsset(AAssetManager *manager, const char *fileName) {
    AAssetDir *pAssetDir = AAssetManager_openDir(manager, "");
    const char *nextFileName = nullptr;
    std::string *result = new std::string;
    while ((nextFileName = AAssetDir_getNextFileName(pAssetDir)) != nullptr) {
        if (strcmp(nextFileName, fileName) == 0) {
            AAsset *pAsset = AAssetManager_open(manager, fileName, AASSET_MODE_STREAMING);
            char buf[1024];
            int nb_read = 0;
            while ((nb_read = AAsset_read(pAsset, buf, 1024)) > 0) {
                result->append(buf, (unsigned) nb_read);
            }
            AAsset_close(pAsset);
            break;
        }
    }
    AAssetDir_close(pAssetDir);
    return result;
}

GLuint buildProgram(const char* verTex,const char* FragTex) {
    GLuint vertexShadeId = complieShader(GL_VERTEX_SHADER, verTex);
    GLuint vertexFragId = complieShader(GL_FRAGMENT_SHADER, FragTex);
    if (vertexFragId == 0 || vertexFragId == 0)
    {
        return 0;
    }

    GLuint programId = glCreateProgram();

    if (programId == 0)
    {
        return 0;
    }

    glAttachShader(programId,vertexShadeId);
    glAttachShader(programId,vertexFragId);

    glLinkProgram(programId);

    GLint param = 0;

    glGetProgramiv(programId,GL_LINK_STATUS,&param);

    if (param == 0)
    {
        GLint infoLen = 0;
        glGetProgramiv(programId, GL_INFO_LOG_LENGTH, &infoLen);
        GLchar *log =(char *)malloc(sizeof(char)*infoLen);
        glGetProgramInfoLog(programId,1, nullptr,log);
        ALOGE("program Log %s",log);
        glDeleteProgram(programId);
        return 0;
    }

    glValidateProgram(programId);
    GLint paramStatus = 0;
    glGetProgramiv(programId,GL_VALIDATE_STATUS,&paramStatus);

    if (paramStatus == 0)
    {
        glDeleteProgram(programId);
        return 0;
    }

    return programId;
}

void checkError(const char *outString) {
    if (glGetError()!=GL_NO_ERROR)
    {
        ALOGE("glGetError %s %d", outString, glGetError());
    }
}

GLuint complieShader(GLenum type,const char *shaderCode)
{
    GLuint shaderId = glCreateShader(type);
    if (shaderId == 0)
    {
        ALOGE("glCreateShader is failed");
        return 0;
    }

    glShaderSource(shaderId,1,&shaderCode, nullptr);
    glCompileShader(shaderId);

    GLint param = 0;

    glGetShaderiv(shaderId,GL_COMPILE_STATUS,&param);


    if (param == 0)
    {
        GLint infoLen = 0;
        glGetShaderiv(shaderId,GL_INFO_LOG_LENGTH,&infoLen);
        char *infoLog= (char*)malloc(sizeof(char) *infoLen);
        glGetShaderInfoLog(shaderId,1, nullptr,infoLog);
        ALOGE("load shader failed log is %s",infoLog);
        glDeleteShader(shaderId);
        return 0;
    }

    return shaderId;

}
