package com.utsoft.jan.myqqview.douyin.common.preview.filter;

/**
 * Created by Administrator on 2019/9/3.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.douyin.common.preview.filter
 */
public class ImageFilter {
    private static final String FRAGMENT_CODE =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES uTexture;\n" +
                    "void main() {\n" +
                    "   gl_FragColor = texture2D(uTexture,vTextureCoord);\n" +
                    "}\n";

    private static final String VERTEX_CODE =
            "uniform mat4 uTexMatrix;\n" +
                    "attribute vec2 aPosition;\n" +
                    "attribute vec4 aTextureCoord;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "    gl_Position = vec4(aPosition,0.0,1.0);\n" +
                    "   vTextureCoord = (uTexMatrix * aTextureCoord).xy;\n" +
                    "}\n";


}
