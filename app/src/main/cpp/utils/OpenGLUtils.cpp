//
// Created by Administrator on 2019/10/21.
//

#include "OpenGLUtils.h"
#include <android/log.h>
#include <GLES2/gl2ext.h>

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
    GLint error = glGetError();
    if (error!=GL_NO_ERROR)
    {
        char err = (char)error;
        ALOGE("glGetError %s %d", outString, err);
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
        char *infoLog= (char*)malloc(sizeof(char) * infoLen);
        glGetShaderInfoLog(shaderId,infoLen, nullptr,infoLog);
        ALOGE("load shader failed log is %s",infoLog);
        glDeleteShader(shaderId);
        return 0;
    }

    return shaderId;

}

GLuint createFrameTexture(int width, int height) {
    if (width <= 0 || height <= 0) {
        ALOGE("createOutputTexture: width or height is 0");
        return -1;
    }
    GLuint textures = 0;
    glGenTextures(1, &textures);
    if (textures == 0) {
        ALOGE("createFrameTexture: glGenTextures is 0");
        return -1;
    }

    glBindTexture(GL_TEXTURE_2D, textures);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, nullptr);

    //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    return textures;
}

void bindFrameTexture(GLuint frame, GLuint fTexture,GLuint fRender) {
    //2.绑定FBO
    glBindFramebuffer(GL_FRAMEBUFFER, frame);
    //4。把纹理绑定到FBO
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, fTexture, 0);

    //6.检查是否绑定成功
    if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
        ALOGE("glFramebufferTexture2D error");
    }

    glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT,
            GL_RENDERBUFFER, fRender);
}

void unbindFrameBuffer() {
    glBindFramebuffer(GL_FRAMEBUFFER, 0);
}

GLuint getOESTextureId() {

    GLuint mTextureId;

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
    return mTextureId;
}






