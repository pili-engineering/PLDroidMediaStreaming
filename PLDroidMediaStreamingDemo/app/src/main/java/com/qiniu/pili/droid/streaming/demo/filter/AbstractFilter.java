package com.qiniu.pili.droid.streaming.demo.filter;

import android.content.Context;

import java.nio.FloatBuffer;

public abstract class AbstractFilter {
    protected abstract int createProgram(Context context);

    protected abstract void getGLSLValues();

    protected abstract void useProgram();

    protected abstract void bindTexture(int textureId);

    protected abstract void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer,
                                           int coordsPerVertex, int vertexStride, FloatBuffer texBuffer,
                                           int texStride);

    protected abstract void drawArrays(int firstVertex, int vertexCount);

    protected abstract void unbindGLSLValues();

    protected abstract void unbindTexture();

    protected abstract void disuseProgram();
}
