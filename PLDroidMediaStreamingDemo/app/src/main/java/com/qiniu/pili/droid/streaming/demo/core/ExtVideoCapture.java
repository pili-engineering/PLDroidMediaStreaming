package com.qiniu.pili.droid.streaming.demo.core;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.qiniu.pili.droid.streaming.av.common.PLFourCC;

import java.io.IOException;
import java.util.List;

public final class ExtVideoCapture implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private static final String TAG = "ExtVideoCapture";

    public interface OnPreviewFrameCallback {
        void onPreviewFrameCaptured(byte[] data, int width, int height, int orientation, boolean mirror, int fmt, long tsInNanoTime);
    }

    public static final int MAX_CALLBACK_BUFFER_NUM = 2;

    private Camera mCamera;
    private int mCurrentFacingId = Camera.CameraInfo.CAMERA_FACING_BACK;

    private OnPreviewFrameCallback mOnPreviewFrameCallback;

    private int mPreviewWidth = 0;
    private int mPreviewHeight = 0;

    private Context mContext;
    private int mCameraPreviewDegree;

    public ExtVideoCapture(SurfaceView sv) {
        sv.getHolder().addCallback(this);
        sv.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mContext = sv.getContext();
    }

    public void setOnPreviewFrameCallback(OnPreviewFrameCallback callback) {
        mOnPreviewFrameCallback = callback;
    }

    public void switchCamera() {
        stopPreviewAndFreeCamera();

        if (mCurrentFacingId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            mCurrentFacingId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            mCurrentFacingId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (safeCameraOpen(mCurrentFacingId)) {
            try {
                mCamera.setPreviewDisplay(holder);

                int degree = getDeviceRotationDegree(mContext);
                Camera.CameraInfo camInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(mCurrentFacingId, camInfo);
                int orientation;
                if (mCurrentFacingId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    orientation = (camInfo.orientation + degree) % 360;
                    mCameraPreviewDegree = orientation;
                    orientation = (360 - orientation) % 360;  // compensate the mirror
                } else {  // back-facing
                    orientation = (camInfo.orientation - degree + 360) % 360;
                    mCameraPreviewDegree = orientation;
                }
                mCamera.setDisplayOrientation(orientation);

                Camera.Parameters params = mCamera.getParameters();
                params.setPreviewFormat(ImageFormat.NV21);

                final Camera.Size previewSize = params.getPreviewSize();
                final int bitsPerPixel = ImageFormat.getBitsPerPixel(params.getPreviewFormat());
                final int previewBufferSize = (previewSize.width * previewSize.height * bitsPerPixel) / 8;
                for (int i = 0; i < MAX_CALLBACK_BUFFER_NUM; i++) {
                    mCamera.addCallbackBuffer(new byte[previewBufferSize]);
                }

                mPreviewWidth = previewSize.width;
                mPreviewHeight = previewSize.height;

                mCamera.setPreviewCallbackWithBuffer(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopPreviewAndFreeCamera();
    }

    private boolean safeCameraOpen(int id) {
        boolean qOpened = false;

        try {
            releaseCameraAndPreview();
            mCamera = Camera.open(id);
            qOpened = (mCamera != null);
        } catch (Exception e) {
            Log.e(TAG, "failed to open Camera");
            e.printStackTrace();
        }

        if (qOpened) {
            mCurrentFacingId = id;
        }

        return qOpened;
    }

    private void releaseCameraAndPreview() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * When this function returns, mCamera will be null.
     */
    private void stopPreviewAndFreeCamera() {
        if (mCamera != null) {
            // Call stopPreview() to stop updating the preview surface.
            mCamera.stopPreview();

            // Important: Call release() to release the camera for use by other
            // applications. Applications should release the camera immediately
            // during onPause() and re-open() it during onResume()).
            mCamera.release();

            mCamera = null;
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (camera == null || data == null) {
            return;
        }

        if (mOnPreviewFrameCallback != null) {
            mOnPreviewFrameCallback.onPreviewFrameCaptured(data, mPreviewWidth, mPreviewHeight, mCameraPreviewDegree, false, PLFourCC.FOURCC_NV21, System.nanoTime());
        }

        if (mCamera != null) {
            mCamera.addCallbackBuffer(data);
        }
    }

    private static int getDisplayDefaultRotation(Context ctx) {
        WindowManager windowManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        return windowManager.getDefaultDisplay().getRotation();
    }

    private static int getDeviceRotationDegree(Context ctx) {
        switch (getDisplayDefaultRotation(ctx)) {
            // normal portrait
            case Surface.ROTATION_0:
                return 0;
            // expected landscape
            case Surface.ROTATION_90:
                return 90;
            // upside down portrait
            case Surface.ROTATION_180:
                return 180;
            // "upside down" landscape
            case Surface.ROTATION_270:
                return 270;
        }
        return 0;
    }
}
