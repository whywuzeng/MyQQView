//
// Created by Administrator on 2019/10/24.
//

#ifndef MYQQVIEW_OESIMAGEFILTER_H
#define MYQQVIEW_OESIMAGEFILTER_H


#include "GPUImageFilter.h"

class OESImageFilter : public GPUImageFilter{
public:
    OESImageFilter(AAssetManager *manager);

    const char *getVertexSource() override;

    const char *getFragmentSource() override;

protected:
    void onDrawBefore() override;

    void onDrawAfter() override;

public:
    void onChanged() override;

    void release();

protected:
    void onBindTexture() override;

};


#endif //MYQQVIEW_OESIMAGEFILTER_H
