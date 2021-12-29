package com.qiniu.pili.droid.streaming.demo.utils;

import android.content.pm.ActivityInfo;

public class Config {
    public static final boolean DEBUG_MODE = false;
    public static final boolean FILTER_ENABLED = false;
    public static final int SCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    public static final String HINT_ENCODING_ORIENTATION_CHANGED =
            "Encoding orientation had been changed. Stop streaming first and restart streaming will take effect";

    // Streaming config constant name
    public static final String NAME_ENCODING_CONFIG = "EncodingConfig";
    public static final String NAME_CAMERA_CONFIG = "CameraConfig";
    public static final String PUBLISH_URL = "PUBLISH_URL";
    public static final String TRANSFER_MODE_QUIC = "TRANSFER_MODE_QUIC";
    public static final String TRANSFER_MODE_SRT = "TRANSFER_MODE_SRT";
    public static final String AUDIO_CHANNEL_STEREO = "AUDIO_CHANNEL_STEREO";
    public static final String AUDIO_VOIP_RECORD = "AUDIO_VOIP_RECORD";
    public static final String AUDIO_SCO_ON = "AUDIO_SCO_ON";

    public static final String SP_NAME = "PLDroidMediaStreamingDemo";
    public static final String KEY_USER_ID = "userId";
}
