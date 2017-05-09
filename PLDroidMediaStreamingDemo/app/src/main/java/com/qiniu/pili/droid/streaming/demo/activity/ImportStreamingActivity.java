package com.qiniu.pili.droid.streaming.demo.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.qiniu.pili.droid.streaming.AVCodecType;
import com.qiniu.pili.droid.streaming.StreamingManager;
import com.qiniu.pili.droid.streaming.demo.utils.Config;
import com.qiniu.pili.droid.streaming.demo.R;
import com.qiniu.pili.droid.streaming.demo.core.ExtAudioCapture;
import com.qiniu.pili.droid.streaming.demo.core.ExtVideoCapture;

public class ImportStreamingActivity extends StreamingBaseActivity {
    private static final String TAG = "ImportStreamingActivity";

    private SurfaceView mSurfaceView;

    private ExtAudioCapture mExtAudioCapture;
    private ExtVideoCapture mExtVideoCapture;

    private StreamingManager mStreamingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mExtVideoCapture = new ExtVideoCapture(mSurfaceView);
        mExtVideoCapture.setOnPreviewFrameCallback(mOnPreviewFrameCallback);
        mExtAudioCapture = new ExtAudioCapture();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mExtAudioCapture.startCapture();
        mExtAudioCapture.setOnAudioFrameCapturedListener(mOnAudioFrameCapturedListener);
        mStreamingManager.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mExtAudioCapture.stopCapture();
        mStreamingManager.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStreamingManager.destroy();
    }

    @Override
    public void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        } else {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(Config.SCREEN_ORIENTATION);
        setContentView(R.layout.activity_import_streaming);
        mSurfaceView = (SurfaceView) findViewById(R.id.ext_camerapreview_surfaceview);
    }

    @Override
    protected void initStreamingManager() {
        mStreamingManager = new StreamingManager(this, AVCodecType.HW_VIDEO_YUV_AS_INPUT_WITH_HW_AUDIO_CODEC);
        mStreamingManager.prepare(mProfile);
        mStreamingManager.setStreamingSessionListener(this);
        mStreamingManager.setStreamStatusCallback(this);
        mStreamingManager.setStreamingStateListener(this);
    }

    @Override
    protected boolean startStreaming() {
        return mStreamingManager.startStreaming();
    }

    @Override
    protected boolean stopStreaming() {
        return mStreamingManager.stopStreaming();
    }

    private ExtVideoCapture.OnPreviewFrameCallback mOnPreviewFrameCallback = new ExtVideoCapture.OnPreviewFrameCallback() {
        @Override
        public void onPreviewFrameCaptured(byte[] data, int width, int height, int orientation, boolean mirror, int fmt, long tsInNanoTime) {
            mStreamingManager.inputVideoFrame(data, width, height, orientation, false, fmt, tsInNanoTime);
        }
    };

    private ExtAudioCapture.OnAudioFrameCapturedListener mOnAudioFrameCapturedListener = new ExtAudioCapture.OnAudioFrameCapturedListener() {
        @Override
        public void onAudioFrameCaptured(byte[] audioData) {
            long timestamp = System.nanoTime();
            mStreamingManager.inputAudioFrame(audioData, timestamp, false);
        }
    };
}
