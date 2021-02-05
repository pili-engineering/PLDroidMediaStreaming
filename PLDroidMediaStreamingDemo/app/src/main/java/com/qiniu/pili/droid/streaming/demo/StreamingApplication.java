package com.qiniu.pili.droid.streaming.demo;

import android.app.Application;
import android.content.Intent;

import com.qiniu.pili.droid.streaming.StreamingEnv;
import com.qiniu.pili.droid.streaming.demo.service.KeepAppAliveService;
import com.qiniu.pili.droid.streaming.demo.utils.AppStateTracker;
import com.qiniu.pili.droid.streaming.demo.utils.Util;

public class StreamingApplication extends Application {

    private boolean mIsServiceAlive;
    private Intent mServiceIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * init must be called before any other func
         *
         * 注意：参数 userId 代表用户的唯一标识符，用于区分不同的用户
         */
        StreamingEnv.init(getApplicationContext(), Util.getUserId(getApplicationContext()));

        /**
         * track app background state to avoid possibly stopping microphone recording
         * in screen streaming mode on Android P+
         */
        AppStateTracker.track(this, new AppStateTracker.AppStateChangeListener() {
            @Override
            public void appTurnIntoForeground() {
                stopService();
            }

            @Override
            public void appTurnIntoBackGround() {
                startService();
            }

            @Override
            public void appDestroyed() {
                stopService();
            }
        });
    }

    /**
     * start foreground service to make process not turn to idle state
     *
     * on Android P+, it doesn't allow recording audio to protect user's privacy in idle state.
     */
    private void startService() {
        if (mServiceIntent == null) {
            mServiceIntent = new Intent(StreamingApplication.this, KeepAppAliveService.class);
        }
        startService(mServiceIntent);
        mIsServiceAlive = true;
    }

    private void stopService() {
        if (mIsServiceAlive) {
            stopService(mServiceIntent);
            mServiceIntent = null;
            mIsServiceAlive = false;
        }
    }
}
