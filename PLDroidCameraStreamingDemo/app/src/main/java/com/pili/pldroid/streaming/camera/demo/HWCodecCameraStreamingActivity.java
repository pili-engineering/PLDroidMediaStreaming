package com.pili.pldroid.streaming.camera.demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pili.pldroid.streaming.CameraStreamingManager;
import com.pili.pldroid.streaming.CameraStreamingManager.EncodingType;
import com.pili.pldroid.streaming.CameraStreamingSetting;
import com.pili.pldroid.streaming.FrameCapturedCallback;
import com.pili.pldroid.streaming.StreamStatusCallback;
import com.pili.pldroid.streaming.StreamingProfile;
import com.pili.pldroid.streaming.SurfaceTextureCallback;
import com.pili.pldroid.streaming.camera.demo.gles.FBO;
import com.pili.pldroid.streaming.widget.AspectFrameLayout;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created by jerikc on 15/10/29.
 */
public class HWCodecCameraStreamingActivity extends StreamingBaseActivity {
    private static final String TAG = "HWCodecCameraStreaming";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AspectFrameLayout afl = (AspectFrameLayout) findViewById(R.id.cameraPreview_afl);
        afl.setShowMode(AspectFrameLayout.SHOW_MODE.REAL);
        CameraPreviewFrameView cameraPreviewFrameView =
                (CameraPreviewFrameView) findViewById(R.id.cameraPreview_surfaceView);
        cameraPreviewFrameView.setListener(this);

        mCameraStreamingManager = new CameraStreamingManager(this, afl, cameraPreviewFrameView,
                EncodingType.HW_VIDEO_WITH_HW_AUDIO_CODEC); // hw codec
        mCameraStreamingManager.prepare(mCameraStreamingSetting, mProfile);

        mCameraStreamingManager.setStreamingStateListener(this);
        mCameraStreamingManager.setSurfaceTextureCallback(this);
        mCameraStreamingManager.setStreamingSessionListener(this);
//        mCameraStreamingManager.setNativeLoggingEnabled(false);
        mCameraStreamingManager.setStreamStatusCallback(this);
        // update the StreamingProfile
//        mProfile.setStream(new Stream(mJSONObject1));
//        mCameraStreamingManager.setStreamingProfile(mProfile);
        setFocusAreaIndicator();
    }
}
