package com.pili.pldroid.streaming.camera.demo;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pili.pldroid.streaming.CameraStreamingManager;
import com.pili.pldroid.streaming.CameraStreamingManager.EncodingType;
import com.pili.pldroid.streaming.CameraStreamingSetting;
import com.pili.pldroid.streaming.FrameCapturedCallback;
import com.pili.pldroid.streaming.MicrophoneStreamingSetting;
import com.pili.pldroid.streaming.StreamStatusCallback;
import com.pili.pldroid.streaming.StreamingPreviewCallback;
import com.pili.pldroid.streaming.StreamingProfile;
import com.pili.pldroid.streaming.StreamingProfile.StreamStatusConfig;
import com.pili.pldroid.streaming.StreamingProfile.StreamStatus;
import com.pili.pldroid.streaming.StreamingProfile.ENCODING_ORIENTATION;
import com.pili.pldroid.streaming.SurfaceTextureCallback;
import com.pili.pldroid.streaming.camera.demo.gles.FBO;
import com.pili.pldroid.streaming.camera.demo.ui.RotateLayout;
import com.pili.pldroid.streaming.widget.AspectFrameLayout;
import com.qiniu.android.dns.DnsManager;
import com.qiniu.android.dns.IResolver;
import com.qiniu.android.dns.NetworkInfo;
import com.qiniu.android.dns.http.DnspodFree;
import com.qiniu.android.dns.local.AndroidDnsServer;
import com.qiniu.android.dns.local.Resolver;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;

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

        mCameraStreamingManager = new CameraStreamingManager(this, afl, cameraPreviewFrameView,
                EncodingType.SW_VIDEO_WITH_SW_AUDIO_CODEC);  // soft codec
        mCameraStreamingManager.prepare(mCameraStreamingSetting, mMicrophoneStreamingSetting, mProfile);
        mCameraStreamingManager.setStreamingStateListener(this);
        mCameraStreamingManager.setStreamingPreviewCallback(this);
        mCameraStreamingManager.setSurfaceTextureCallback(this);
        mCameraStreamingManager.setStreamingSessionListener(this);
        mCameraStreamingManager.setStreamStatusCallback(this);

        // update the StreamingProfile
//        mProfile.setStream(new Stream(mJSONObject1));
//        mCameraStreamingManager.setStreamingProfile(mProfile);
//        mCameraStreamingManager.setNativeLoggingEnabled(false);
        setFocusAreaIndicator();
    }
}
