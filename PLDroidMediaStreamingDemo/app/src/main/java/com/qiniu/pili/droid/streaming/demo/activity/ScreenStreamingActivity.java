package com.qiniu.pili.droid.streaming.demo.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private ImageSwitcher mImageSwitcher;
    private int mTimes = 0;
    private boolean mIsPictureStreaming = false;
    private Handler mHandlerPic;

    /**
     * switch picture during streaming
     */
    private class ImageSwitcher implements Runnable {
        @Override
        public void run() {
            if (!mIsPictureStreaming) {
                Log.d(TAG, "is not picture streaming!!!");
                return;
            }

            if (mTimes % 2 == 0) {
                if (mEncodingConfig.mPictureStreamingFilePath != null) {
                    mScreenStreamingManager.setPictureStreamingFilePath(mEncodingConfig.mPictureStreamingFilePath);
                } else {
                    mScreenStreamingManager.setPictureStreamingResourceId(R.drawable.qiniu_logo);
                }
            } else {
                mScreenStreamingManager.setPictureStreamingResourceId(R.drawable.pause_publish);
            }
            mTimes++;
            if (mHandlerPic != null && mIsPictureStreaming) {
                mHandlerPic.postDelayed(this, 1000);
            }
        }
    }

    private void togglePictureStreaming() {
        boolean isOK = mScreenStreamingManager.togglePictureStreaming();
        if (!isOK) {
            Toast.makeText(this, "toggle picture streaming failed!", Toast.LENGTH_SHORT).show();
            return;
        }

        mIsPictureStreaming = !mIsPictureStreaming;

        mTimes = 0;
        if (mIsPictureStreaming) {
            if (mImageSwitcher == null) {
                mImageSwitcher = new ImageSwitcher();
            }

            HandlerThread handlerThread = new HandlerThread(TAG);
            handlerThread.start();
            mHandlerPic = new Handler(handlerThread.getLooper());
            mHandlerPic.postDelayed(mImageSwitcher, 1000);
        } else {
            if (mHandlerPic != null) {
                mHandlerPic.getLooper().quit();
            }
        }
    }

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
        Button picStreamingBtn = (Button) findViewById(R.id.pic_streaming_btn);
        picStreamingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProfile.setPictureStreamingFps(10);
                togglePictureStreaming();
            }
        });

        mHandler.post(mUpdateTimeTvRunnable);
    }

    @Override
    protected void initStreamingManager() {
        // In screen streaming, screen size normally should equals to encoding size
        ScreenSetting screenSetting = new ScreenSetting();
        screenSetting.setSize(mEncodingConfig.mVideoSizeCustomWidth, mEncodingConfig.mVideoSizeCustomHeight);
        screenSetting.setDpi(1);

        if (mEncodingConfig.mIsPictureStreamingEnabled) {
            if (mEncodingConfig.mPictureStreamingFilePath == null) {
                mProfile.setPictureStreamingResourceId(R.drawable.pause_publish);
            } else {
                mProfile.setPictureStreamingFilePath(mEncodingConfig.mPictureStreamingFilePath);
            }
        }

        mScreenStreamingManager = new ScreenStreamingManager();
        mScreenStreamingManager.setStreamingSessionListener(this);
        mScreenStreamingManager.setStreamingStateListener(this);
        mScreenStreamingManager.setStreamStatusCallback(this);
        mScreenStreamingManager.prepare(this, screenSetting, null, mProfile);
        mScreenStreamingManager.setStreamingSessionListener(this);
        mScreenStreamingManager.setStreamStatusCallback(this);
        mScreenStreamingManager.setStreamingStateListener(this);
        mScreenStreamingManager.setNativeLoggingEnabled(false);
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
