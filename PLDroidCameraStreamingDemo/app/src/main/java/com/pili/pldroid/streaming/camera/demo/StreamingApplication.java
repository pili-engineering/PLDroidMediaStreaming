package com.pili.pldroid.streaming.camera.demo;

import android.app.Application;

import com.pili.pldroid.streaming.StreamingEnv;

/**
 * Created by jerikc on 16/4/14.
 */
public class StreamingApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        StreamingEnv.init(getApplicationContext());
    }
}
