package com.qiniu.pili.droid.streaming.demo.activity;

import android.os.Bundle;

import com.qiniu.pili.droid.streaming.MediaStreamingManager;
import com.qiniu.pili.droid.streaming.demo.R;

public class AudioStreamingActivity extends StreamingBaseActivity {

    private MediaStreamingManager mMediaStreamingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaStreamingManager.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsReady = false;
        mShutterButtonPressed = false;
        mMediaStreamingManager.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaStreamingManager.destroy();
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_audio_streaming);
    }

    @Override
    protected void initStreamingManager() {
        mMediaStreamingManager = new MediaStreamingManager(this, mEncodingConfig.mCodecType);
        mMediaStreamingManager.prepare(mProfile);
        mMediaStreamingManager.setStreamingSessionListener(this);
        mMediaStreamingManager.setStreamStatusCallback(this);
        mMediaStreamingManager.setAudioSourceCallback(this);
        mMediaStreamingManager.setStreamingStateListener(this);
    }

    @Override
    protected boolean startStreaming() {
        return mMediaStreamingManager.startStreaming();
    }

    @Override
    protected boolean stopStreaming() {
        return mMediaStreamingManager.stopStreaming();
    }
}
