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
    public static final String AUDIO_CHANNEL_STEREO = "AUDIO_CHANNEL_STEREO";
}
