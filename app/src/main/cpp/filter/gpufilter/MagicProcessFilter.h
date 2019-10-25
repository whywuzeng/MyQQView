//
// Created by Administrator on 2019/10/25.
//

#ifndef MYQQVIEW_MAGICPROCESSFILTER_H
#define MYQQVIEW_MAGICPROCESSFILTER_H


#include "../GPUImageFilter.h"

class MagicProcessFilter : public GPUImageFilter {
public:
    MagicProcessFilter(AAssetManager *manager);

    void release();

    void onDrawBefore() override;

    void onDrawAfter() override;

    void onChanged() override;
};


#endif //MYQQVIEW_MAGICPROCESSFILTER_H
