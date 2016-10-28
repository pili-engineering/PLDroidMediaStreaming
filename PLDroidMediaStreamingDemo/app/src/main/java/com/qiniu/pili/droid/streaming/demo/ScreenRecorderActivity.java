package com.qiniu.pili.droid.streaming.demo;

/**
 * Created by jerikc on 16/7/5.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.qiniu.pili.droid.streaming.ScreenSetting;
import com.qiniu.pili.droid.streaming.ScreenStreamingManager;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;

import org.json.JSONObject;

import java.net.URISyntaxException;

/**
 * Created by jerikc on 16/6/21.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ScreenRecorderActivity extends Activity implements
        StreamingStateChangedListener {

    private static final String TAG = "ScreenRecorderActivity";

    private ScreenStreamingManager screenStreamingManager;

    private TextView mTimeTv;
    private Button mStreamingBtn;

    private boolean mStartStreaming = false;

    private static final int SAMPLE_RATE = 44100;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mUpdateTimeTvRunnable = new Runnable() {
        @Override
        public void run() {
            mTimeTv.setText("currentTimeMillis:" + System.currentTimeMillis());
            mHandler.postDelayed(mUpdateTimeTvRunnable, 100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.screen_recoder);

        mTimeTv = (TextView) findViewById(R.id.time_tv);
        mStreamingBtn = (Button) findViewById(R.id.streaming_on_off_btn);
        mStreamingBtn.setText("Start Streaming");
        mHandler.post(mUpdateTimeTvRunnable);

        String publishUrlFromServer = getIntent().getStringExtra(Config.EXTRA_KEY_PUB_URL);
        Log.i(TAG, "streamUrlFromServer:" + publishUrlFromServer);

        boolean isEncOrientationPort;
        if (Config.SCREEN_ORIENTATION == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            isEncOrientationPort = true;
        } else if (Config.SCREEN_ORIENTATION == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            isEncOrientationPort = false;
        }
        setRequestedOrientation(Config.SCREEN_ORIENTATION);

        int width, height;
        if (isEncOrientationPort) {
            width = 480;
            height = 640;
        } else {
            width = 640;
            height = 480;
        }

        // Get the display size and density.
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        ScreenSetting screenSetting = new ScreenSetting();
        screenSetting.setSize(width, height);
        screenSetting.setDpi(metrics.densityDpi);

        StreamingProfile.AudioProfile aProfile = new StreamingProfile.AudioProfile(SAMPLE_RATE, 96 * 1024);
        StreamingProfile.VideoProfile vProfile = new StreamingProfile.VideoProfile(12, 1000 * 1024, 90);
        StreamingProfile.AVProfile avProfile = new StreamingProfile.AVProfile(vProfile, aProfile);

        final StreamingProfile streamingProfile = new StreamingProfile();

        if (publishUrlFromServer.startsWith(Config.EXTRA_PUBLISH_URL_PREFIX)) {
            // publish url
            try {
                streamingProfile.setPublishUrl(publishUrlFromServer.substring(Config.EXTRA_PUBLISH_URL_PREFIX.length()));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else if (publishUrlFromServer.startsWith(Config.EXTRA_PUBLISH_JSON_PREFIX)) {
            try {
                JSONObject jsonObject = new JSONObject(publishUrlFromServer.substring(Config.EXTRA_PUBLISH_JSON_PREFIX.length()));
                StreamingProfile.Stream stream = new StreamingProfile.Stream(jsonObject);
                streamingProfile.setStream(stream);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Invalid Publish Url", Toast.LENGTH_LONG).show();
        }

        streamingProfile.setAudioQuality(StreamingProfile.AUDIO_QUALITY_MEDIUM1)
                .setAVProfile(avProfile)
                .setPreferredVideoEncodingSize(width, height)
                .setVideoQuality(StreamingProfile.VIDEO_QUALITY_HIGH2);

        screenStreamingManager = new ScreenStreamingManager(this);
        screenStreamingManager.setStreamingStateListener(this);

        mStreamingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStartStreaming) {
                    stopScreenCapture();
                } else {
                    startScreenCapture();
                }
            }
        });

        screenStreamingManager.prepare(screenSetting, null, streamingProfile);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        screenStreamingManager.destroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onStateChanged(StreamingState streamingState, Object extra) {
        Log.i(TAG, "streamingState:" + streamingState);
        switch (streamingState) {
            case READY:
                startScreenCapture();
                break;
            case SHUTDOWN:
                mStartStreaming = false;
                break;
        }
    }

    private void stopScreenCapture() {
        mStartStreaming = !screenStreamingManager.stopStreaming();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!mStartStreaming) {
                    mStreamingBtn.setText("Start Streaming");
                }
            }
        });
    }

    private void startScreenCapture() {
        mStartStreaming = screenStreamingManager.startStreaming();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mStartStreaming) {
                    mStreamingBtn.setText("Stop Streaming");
                }
            }
        });
    }
}
