package com.pili.pldroid.streaming.camera.demo.gles;

/**
 * Created by jerikc on 16/2/23.
 */
import android.opengl.Matrix;

import com.pili.pldroid.streaming.camera.demo.filter.IFilter;

/**
 * This class essentially represents a viewport-sized sprite that will be rendered with
 * a texture, usually from an external source like the camera.
 */
public class FullFrameRect {
    private final Drawable2d mRectDrawable = new Drawable2d();
    private IFilter mFilter;
    public final float[] IDENTITY_MATRIX = new float[16];

    /**
     * Prepares the object.
     *
     * @param program The program to use.  FullFrameRect takes ownership, and will release
     * the program when no longer needed.
     */
    public FullFrameRect(IFilter program) {
        mFilter = program;
        Matrix.setIdentityM(IDENTITY_MATRIX, 0);
    }

    /**
     * Releases resources.
     * <p>
     * This must be called with the appropriate EGL context current (i.e. the one that was
     * current when the constructor was called).  If we're about to destroy the EGL context,
     * there's no value in having the caller make it current just to do this cleanup, so you
     * can pass a flag that will tell this function to skip any EGL-context-specific cleanup.
     */
    public void release(boolean doEglCleanup) {
        if (mFilter != null) {
            if (doEglCleanup) {
                mFilter.releaseProgram();
            }
            mFilter = null;
        }
    }

    /**
     * Returns the program currently in use.
     */
    public IFilter getFilter() {
        return mFilter;
    }

    /**
     * Changes the program.  The previous program will be released.
     * <p>
     * The appropriate EGL context must be current.
     */
    public void changeProgram(IFilter newFilter) {
        mFilter.releaseProgram();
        mFilter = newFilter;
    }

    /**
     * Draws a viewport-filling rect, texturing it with the specified texture object.
     */
    public void drawFrame(int textureId) {
        // Use the identity matrix for MVP so our 2x2 FULL_RECTANGLE covers the viewport.
        mFilter.onDraw(IDENTITY_MATRIX, mRectDrawable.getVertexArray(), 0,
                mRectDrawable.getVertexCount(), mRectDrawable.getCoordsPerVertex(),
                mRectDrawable.getVertexStride(), mRectDrawable.getTexCoordArray(),
                textureId, mRectDrawable.getTexCoordStride());
    }
}
