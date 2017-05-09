package com.qiniu.pili.droid.streaming.demo.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.qiniu.pili.droid.streaming.demo.R;
import com.qiniu.pili.droid.streaming.demo.gles.GlUtil;

import java.nio.FloatBuffer;

public class CameraFilterBeauty extends CameraFilter {
    private int singleStepOffset;

    private static final float offset_array[] = {
            2, 2,
    };

    public CameraFilterBeauty(Context context) {
        super(context);
        offset_array[0] = offset_array[0] / 90;
        offset_array[1] = offset_array[1] / 160;
    }

    @Override
    protected int createProgram(Context applicationContext) {
        return GlUtil.createProgram(applicationContext, R.raw.vertex_shader,
                R.raw.fragment_shader_beauty);
    }

    @Override
    protected void getGLSLValues() {
        super.getGLSLValues();

        singleStepOffset = GLES20.glGetUniformLocation(mProgramHandle, "singleStepOffset");
    }

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex,
                                  int vertexStride, FloatBuffer texBuffer, int texStride) {
        super.bindGLSLValues(mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride,
                texBuffer, texStride);

        GLES20.glUniform2fv(singleStepOffset, 1, offset_array, 0);
    }
}

