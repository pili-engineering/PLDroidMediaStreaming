package com.pili.pldroid.streaming.camera.demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.pili.pldroid.streaming.CameraStreamingManager;
import com.pili.pldroid.streaming.CameraStreamingManager.EncodingType;
import com.pili.pldroid.streaming.WatermarkSetting;
import com.pili.pldroid.streaming.widget.AspectFrameLayout;
/**
 * Created by jerikc on 15/10/29.
 */
public class SWCodecCameraStreamingActivity extends StreamingBaseActivity {
    private static final String TAG = "SWCodecCameraStreaming";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AspectFrameLayout afl = (AspectFrameLayout) findViewById(R.id.cameraPreview_afl);
        afl.setShowMode(AspectFrameLayout.SHOW_MODE.REAL);
        CameraPreviewFrameView cameraPreviewFrameView =
                (CameraPreviewFrameView) findViewById(R.id.cameraPreview_surfaceView);
        cameraPreviewFrameView.setListener(this);

        WatermarkSetting watermarksetting = new WatermarkSetting(this);
        watermarksetting.setResourceId(R.drawable.qiniu_logo)
                .setAlpha(100)
                .setSize(WatermarkSetting.WATERMARK_SIZE.MEDIUM)
                .setCustomPosition(0.5f, 0.5f);

        mCameraStreamingManager = new CameraStreamingManager(this, afl, cameraPreviewFrameView,
                EncodingType.SW_VIDEO_WITH_SW_AUDIO_CODEC); // sw codec

        mCameraStreamingManager.prepare(mCameraStreamingSetting, mMicrophoneStreamingSetting, watermarksetting, mProfile);

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
