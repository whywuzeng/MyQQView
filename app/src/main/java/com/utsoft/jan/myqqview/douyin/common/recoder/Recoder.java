package com.utsoft.jan.myqqview.douyin.common.recoder;

import com.utsoft.jan.myqqview.douyin.common.C;

import java.io.IOException;

/**
 * Created by Administrator on 2019/9/5.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.recoder
 */
public interface Recoder<T> {

    @C.DataType
    int getDataType();

    void setOnRecordFinishListener(OnRecordFinishListener mListener);

    void configure(T config);

    void start();

    void stop();

    void prepareCodec() throws IOException;

    void shutdown();

    boolean isStarted();
}
