package com.pili.pldroid.streaming.camera.demo;

import android.content.pm.ActivityInfo;

/**
 * Created by jerikc on 15/12/8.
 */
public class Config {
    public static final boolean DEBUG_MODE = true;
    public static final int SCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    public static final String HINT_ENCODING_ORIENTATION_CHANGED =
            "Encoding orientation had been changed. Stop streaming first and restart streaming will take effect";
}
