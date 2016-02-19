package com.pili.pldroid.streaming.camera.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pili.pldroid.streaming.CameraStreamingManager;
import com.pili.pldroid.streaming.CameraStreamingManager.EncodingType;
import com.pili.pldroid.streaming.StreamingProfile;

public class AudioStreamingActivity extends StreamingBaseActivity {

    private static final String TAG = "AudioStreamingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_streaming);

        mSatusTextView = (TextView) findViewById(R.id.streamingStatus);
        mShutterButton = (Button) findViewById(R.id.toggleRecording_button);

        StreamingProfile.Stream stream = new StreamingProfile.Stream(mJSONObject);
        StreamingProfile profile = new StreamingProfile();
        profile.setStream(stream)
                .setAudioQuality(StreamingProfile.AUDIO_QUALITY_LOW1);

        mCameraStreamingManager = new CameraStreamingManager(this, EncodingType.SW_AUDIO_CODEC);
        mCameraStreamingManager.prepare(profile);
        mCameraStreamingManager.setStreamingStateListener(this);

        mShutterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mShutterButtonPressed) {
                    stopStreaming();
                } else {
                    startStreaming();
                }
            }
        });
    }
}
