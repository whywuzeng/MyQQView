//
// Created by Administrator on 2019/10/29.
//

#ifndef MYQQVIEW_MP3ENCODER_H
#define MYQQVIEW_MP3ENCODER_H


#include <cstdio>
#include "../lame/include/lame.h"

class Mp3Encoder {
private:
    FILE* pcmFile;
    FILE* mp3File;
public:
    int init(const char* pcmPath,const char* mp3Path, int audioChannels, int bitRate, int sampleRate);
    void encoder();
    void destory();

    lame_t lameClient;
};


#endif //MYQQVIEW_MP3ENCODER_H
