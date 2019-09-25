package com.utsoft.jan.common.widget.Imageview;

import android.graphics.Matrix;

/**
 * Created by Administrator on 2019/9/24.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.common.widget.Imageview
 */
public class StickOption {

    private Matrix matrix = new Matrix();

    private float mScale;

    public float getScale() {
        return mScale;
    }

    public void setScale(float mScale) {
        this.mScale = mScale;
    }

    private boolean isEdit =false;

    private int width;

    private int height;

    public int getWidth() {
        return width;
    }

    public StickOption setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public StickOption setHeight(int height) {
        this.height = height;
        return this;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public StickOption setEdit(boolean edit) {
        isEdit = edit;
        return this;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }
}
