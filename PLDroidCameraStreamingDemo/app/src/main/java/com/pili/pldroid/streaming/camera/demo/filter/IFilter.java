package com.pili.pldroid.streaming.camera.demo.filter;

import java.nio.FloatBuffer;

/**
 * Created by jerikc on 16/2/23.
 */
public interface IFilter {
    int getTextureTarget();

    void setTextureSize(int width, int height);

    void onDraw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount,
                int coordsPerVertex, int vertexStride, FloatBuffer texBuffer,
                int textureId, int texStride);

    void releaseProgram();
}
