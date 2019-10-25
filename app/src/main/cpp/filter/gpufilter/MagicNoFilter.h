//
// Created by Administrator on 2019/10/25.
//

#ifndef MYQQVIEW_MAGICNOFILTER_H
#define MYQQVIEW_MAGICNOFILTER_H


#include "../GPUImageFilter.h"

class MagicNoFilter : public GPUImageFilter{
public:

    MagicNoFilter(AAssetManager *manager);

    void release();

protected:
    void clear() override;
};


#endif //MYQQVIEW_MAGICNOFILTER_H
