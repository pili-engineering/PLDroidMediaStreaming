package com.pili.pldroid.streaming.camera.demo;

import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera_streaming);

        AspectFrameLayout afl = (AspectFrameLayout) findViewById(R.id.cameraPreview_afl);
        GLSurfaceView glSurfaceView = (GLSurfaceView) findViewById(R.id.cameraPreview_surfaceView);
        mShutterButton = (Button) findViewById(R.id.toggleRecording_button);

        mSatusTextView = (TextView) findViewById(R.id.streamingStatus);
        mTorchBtn = (Button) findViewById(R.id.torch_btn);

        Stream stream = new Stream(mJSONObject);
        StreamingProfile profile = new StreamingProfile();
        profile.setQuality(StreamingProfile.QUALITY_LOW1)
                .setStreamAndPublishhost(stream, mPublishHost);

        CameraStreamingSetting setting = new CameraStreamingSetting();
        setting.setCameraId(Camera.CameraInfo.CAMERA_FACING_BACK)
                .setContinuousFocusModeEnabled(true)
                .setStreamingProfile(profile)
                .setCameraPrvSizeLevel(CameraStreamingSetting.PREVIEW_SIZE_LEVEL.SMALL)
                .setCameraPrvSizeRatio(CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_4_3);

        mCameraStreamingManager = new CameraStreamingManager(this, afl, glSurfaceView);
        mCameraStreamingManager.onPrepare(setting);
        mCameraStreamingManager.setStreamingStateListener(this);

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
}
