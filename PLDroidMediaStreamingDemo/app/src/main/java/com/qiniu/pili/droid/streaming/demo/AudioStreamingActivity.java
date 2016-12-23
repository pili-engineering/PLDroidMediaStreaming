package com.qiniu.pili.droid.streaming.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qiniu.pili.droid.streaming.AVCodecType;
import com.qiniu.pili.droid.streaming.MediaStreamingManager;
import com.qiniu.pili.droid.streaming.StreamingProfile;

public class AudioStreamingActivity extends StreamingBaseActivity {

    private static final String TAG = "AudioStreamingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_streaming);

        mStatusTextView = (TextView) findViewById(R.id.streamingStatus);
        mShutterButton = (Button) findViewById(R.id.toggleRecording_button);

        mProfile.setAudioQuality(StreamingProfile.AUDIO_QUALITY_LOW1);

        mMediaStreamingManager = new MediaStreamingManager(this, AVCodecType.SW_AUDIO_CODEC);
        mMediaStreamingManager.prepare(mProfile);
        mMediaStreamingManager.setStreamingStateListener(this);

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
