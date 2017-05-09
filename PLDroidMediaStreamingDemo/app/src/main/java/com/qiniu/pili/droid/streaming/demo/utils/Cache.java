package com.qiniu.pili.droid.streaming.demo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Cache {
    public static void saveURL(Context context, String url) {
        SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(context).edit();
        e.putString("URL", url);
        e.commit();
    }

    public static String retrieveURL(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("URL", "");
    }
}
