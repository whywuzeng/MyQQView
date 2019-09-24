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

    private boolean isEdit =false;

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }
}
