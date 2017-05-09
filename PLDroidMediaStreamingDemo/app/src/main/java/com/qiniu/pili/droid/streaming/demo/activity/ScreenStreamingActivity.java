package com.qiniu.pili.droid.streaming.demo.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.qiniu.pili.droid.streaming.ScreenSetting;
import com.qiniu.pili.droid.streaming.ScreenStreamingManager;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.demo.utils.Config;
import com.qiniu.pili.droid.streaming.demo.R;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ScreenStreamingActivity extends StreamingBaseActivity {

    private static final String TAG = "ScreenStreamingActivity";

    private TextView mTimeTv;
    private Handler mHandler = new Handler();

    private ScreenStreamingManager mScreenStreamingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(Config.SCREEN_ORIENTATION);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScreenStreamingManager.destroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_screen_streaming);
        mTimeTv = (TextView) findViewById(R.id.time_tv);
        mHandler.post(mUpdateTimeTvRunnable);
    }

    @Override
    protected void initStreamingManager() {
        // In screen streaming, screen size normally should equals to encoding size
        ScreenSetting screenSetting = new ScreenSetting();
        screenSetting.setSize(mEncodingConfig.mVideoSizeCustomWidth, mEncodingConfig.mVideoSizeCustomHeight);
        screenSetting.setDpi(1);

        mScreenStreamingManager = new ScreenStreamingManager();
        mScreenStreamingManager.setStreamingSessionListener(this);
        mScreenStreamingManager.setStreamingStateListener(this);
        mScreenStreamingManager.setStreamStatusCallback(this);
        mScreenStreamingManager.prepare(this, screenSetting, null, mProfile);
        mScreenStreamingManager.setStreamingSessionListener(this);
        mScreenStreamingManager.setStreamStatusCallback(this);
        mScreenStreamingManager.setStreamingStateListener(this);
    }

    @Override
    protected boolean startStreaming() {
        return mScreenStreamingManager.startStreaming();
    }

    @Override
    protected boolean stopStreaming() {
        return mScreenStreamingManager.stopStreaming();
    }

    @Override
    public void onStateChanged(StreamingState streamingState, Object extra) {
        super.onStateChanged(streamingState, extra);
        switch (streamingState) {
            case REQUEST_SCREEN_CAPTURING_FAIL:
                Toast.makeText(this, "Request screen capturing fail", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private Runnable mUpdateTimeTvRunnable = new Runnable() {
        @Override
        public void run() {
            mTimeTv.setText("currentTimeMillis:" + System.currentTimeMillis());
            mHandler.postDelayed(mUpdateTimeTvRunnable, 100);
        }
    };
}
