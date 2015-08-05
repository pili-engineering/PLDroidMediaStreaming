package com.pili.pldroid.streaming.camera.demo;

import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pili.pldroid.streaming.CameraStreamingManager;
import com.pili.pldroid.streaming.CameraStreamingSetting;
import com.pili.pldroid.streaming.StreamingProfile;
import com.pili.pldroid.streaming.StreamingProfile.Stream;
import com.pili.pldroid.streaming.widget.AspectFrameLayout;

/**
 * Created by jerikc on 15/6/16.
 */
public class CameraStreamingActivity extends StreamingBaseActivity {
    private static final String TAG = "CameraStreamingActivity";

    private Button mTorchBtn;
    private boolean mIsTorchOn = false;
    private Button mCameraSwitchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera_streaming);

        AspectFrameLayout afl = (AspectFrameLayout) findViewById(R.id.cameraPreview_afl);
        GLSurfaceView glSurfaceView = (GLSurfaceView) findViewById(R.id.cameraPreview_surfaceView);
        mShutterButton = (Button) findViewById(R.id.toggleRecording_button);

        mSatusTextView = (TextView) findViewById(R.id.streamingStatus);
        mTorchBtn = (Button) findViewById(R.id.torch_btn);
        mCameraSwitchBtn = (Button) findViewById(R.id.camera_switch_btn);

        Stream stream = new Stream(mJSONObject);
        StreamingProfile profile = new StreamingProfile();
        profile.setVideoQuality(StreamingProfile.VIDEO_QUALITY_MEDIUM1)
                .setAudioQuality(StreamingProfile.AUDIO_QUALITY_HIGH2)
                .setStream(stream);

        CameraStreamingSetting setting = new CameraStreamingSetting();
        setting.setCameraId(Camera.CameraInfo.CAMERA_FACING_BACK)
                .setContinuousFocusModeEnabled(true)
                .setStreamingProfile(profile)
                .setCameraPrvSizeLevel(CameraStreamingSetting.PREVIEW_SIZE_LEVEL.MEDIUM)
                .setCameraPrvSizeRatio(CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_4_3);

        mCameraStreamingManager = new CameraStreamingManager(this, afl, glSurfaceView);
        mCameraStreamingManager.onPrepare(setting);
        mCameraStreamingManager.setStreamingStateListener(this);
//        mCameraStreamingManager.setNativeLoggingEnabled(false);

        mShutterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShutterButtonClick();
            }
        });

        mTorchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (!mIsTorchOn) {
                            mIsTorchOn = true;
                            mCameraStreamingManager.turnLightOn();
                        } else {
                            mIsTorchOn = false;
                            mCameraStreamingManager.turnLightOff();
                        }
                        setTorchEnabled(mIsTorchOn);
                    }
                }).start();
            }
        });

        mCameraSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mCameraStreamingManager.switchCamera();
//                    }
//                }).start();
                mHandler.removeCallbacks(mSwitcher);
                mHandler.postDelayed(mSwitcher, 100);
            }
        });
    }

    private Switcher mSwitcher = new Switcher();
    private class Switcher implements Runnable {
        @Override
        public void run() {
            mCameraStreamingManager.switchCamera();
        }
    }

    private void setTorchEnabled(final boolean enabled) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String flashlight = enabled ? getString(R.string.flash_light_off) : getString(R.string.flash_light_on);
                mTorchBtn.setText(flashlight);
            }
        });
    }

    @Override
    public void onStateChanged(final int state, Object extra) {
        super.onStateChanged(state, extra);
        switch (state) {
            case CameraStreamingManager.STATE.CAMERA_SWITCHED:
                mShutterButtonPressed = false;
                if (extra != null) {
                    Log.i(TAG, "current camera id:" + (Integer)extra);
                }
                Log.i(TAG, "camera switched");
                break;
            case CameraStreamingManager.STATE.TORCH_INFO:
                if (extra != null) {
                    boolean isSupportedTorch = (Boolean) extra;
                    Log.i(TAG, "isSupportedTorch=" + isSupportedTorch);
                    if (isSupportedTorch) {
                        mTorchBtn.setVisibility(View.VISIBLE);
                    } else {
                        mTorchBtn.setVisibility(View.GONE);
                    }
                }
                break;
        }
    }

}
