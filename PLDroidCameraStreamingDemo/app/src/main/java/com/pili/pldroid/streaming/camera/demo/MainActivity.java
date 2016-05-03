package com.pili.pldroid.streaming.camera.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final String url = "your app server address.";

    private static boolean isSupportHWEncode() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    private String requestStreamJson() {
        try {
            HttpURLConnection httpConn = (HttpURLConnection) new URL(url).openConnection();
            httpConn.setRequestMethod("POST");
            httpConn.setConnectTimeout(5000);
            httpConn.setReadTimeout(10000);
            int responseCode = httpConn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            int length = httpConn.getContentLength();
            if (length <= 0) {
                return null;
            }
            InputStream is = httpConn.getInputStream();
            byte[] data = new byte[length];
            int read = is.read(data);
            is.close();
            if (read <= 0) {
                return null;
            }
            return new String(data, 0, read);
        } catch (Exception e) {
            showToast("Network error!");
        }
        return null;
    }

    void showToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startStreamingActivity(final Intent intent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String resByHttp = null;

                if (!Config.DEBUG_MODE) {
                    resByHttp = requestStreamJson();
                    Log.i(TAG, "resByHttp:" + resByHttp);
                    if (resByHttp == null) {
                        showToast("Stream Json Got Fail!");
                        return;
                    }
                    intent.putExtra(Config.EXTRA_KEY_STREAM_JSON, resByHttp);
                } else {
                    showToast("Stream Json Got Fail!");
                }

                startActivity(intent);
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView mVersionInfoTextView = (TextView) findViewById(R.id.version_info);
        mVersionInfoTextView.setText("v1.6.0");

        Button mHWCodecCameraStreamingBtn = (Button) findViewById(R.id.hw_codec_camera_streaming_btn);
        if (!isSupportHWEncode()) {
            mHWCodecCameraStreamingBtn.setVisibility(View.INVISIBLE);
        }
        mHWCodecCameraStreamingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HWCodecCameraStreamingActivity.class);
                startStreamingActivity(intent);
            }
        });

        Button mSWCodecCameraStreamingBtn = (Button) findViewById(R.id.sw_codec_camera_streaming_btn);
        mSWCodecCameraStreamingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SWCodecCameraStreamingActivity.class);
                startStreamingActivity(intent);
            }
        });

        Button mAudioStreamingBtn = (Button) findViewById(R.id.start_pure_audio_streaming_btn);
        mAudioStreamingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AudioStreamingActivity.class);
                startStreamingActivity(intent);
            }
        });
    }
}
