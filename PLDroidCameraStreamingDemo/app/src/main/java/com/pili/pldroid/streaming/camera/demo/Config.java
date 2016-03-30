package com.pili.pldroid.streaming.camera.demo;

import android.content.pm.ActivityInfo;

import com.pili.pldroid.streaming.StreamingProfile;

/**
 * Created by jerikc on 15/12/8.
 */
public class Config {
    public static final boolean DEBUG_MODE = false;
    public static final boolean FILTER_ENABLED = false;
    public static final int ENCODING_LEVEL = StreamingProfile.VIDEO_ENCODING_HEIGHT_480;
    public static final int SCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    public static final String EXTRA_KEY_STREAM_JSON = "stream_json_str";

    public static final String HINT_ENCODING_ORIENTATION_CHANGED =
            "Encoding orientation had been changed. Stop streaming first and restart streaming will take effect";
}
