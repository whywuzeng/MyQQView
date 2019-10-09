package com.utsoft.jan.myqqview.douyin.common.preview.filter;

import com.utsoft.jan.myqqview.douyin.common.preview.RendererInfo;

/**
 * Created by Administrator on 2019/10/9.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.preview.filter
 */
public class BaseRenderImageFilter  {

    //数据顶点
    protected RendererInfo mRendererInfo = new RendererInfo();

    //程序点
    protected int mProgram;

    private BaseRenderImageFilterImpl mBaseRenderImageFilter;

    public BaseRenderImageFilter(BaseRenderImageFilterImpl baseRenderImageFilter) {
        mBaseRenderImageFilter = baseRenderImageFilter;
    }

    private MultiRenderInfo multiRenderInfo;

    public void create(){
        multiRenderInfo = new MultiRenderInfo();
        //初始化 mpro
    }
}
