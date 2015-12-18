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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final String URL = "your app server address.";

    private static boolean isSupportHWEncode() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    private Button mHWCodecCameraStreamingBtn;
    private Button mSWCodecCameraStreamingBtn;
    private Button mAudioStreamingBtn;

    public String requestByHttpPost() {
        try {
            HttpPost httpPost = new HttpPost(URL);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("model", android.os.Build.MODEL));
            HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            httpPost.setEntity(entity);
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResp = httpClient.execute(httpPost);
            int statusCode = httpResp.getStatusLine().getStatusCode();
            Log.i(TAG, "statusCode:" + statusCode);
            if (statusCode == 200) {
                Log.i(TAG, "HttpPost success!");
                return EntityUtils.toString(httpResp.getEntity(), HTTP.UTF_8);
            } else {
                Log.i(TAG, "HttpPost failed!");
                return null;
            }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHWCodecCameraStreamingBtn = (Button) findViewById(R.id.hw_codec_camera_streaming_btn);
        if (!isSupportHWEncode()) {
            mHWCodecCameraStreamingBtn.setVisibility(View.INVISIBLE);
        }
        mHWCodecCameraStreamingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String res = requestByHttpPost();
                            if (res == null) {
                                showToast("Stream Json Got Fail!");
                                return;
                            }
                            Intent intent = new Intent(MainActivity.this, HWCodecCameraStreamingActivity.class);
                            intent.putExtra("stream_json_str", res);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });

        mSWCodecCameraStreamingBtn = (Button) findViewById(R.id.sw_codec_camera_streaming_btn);
        mSWCodecCameraStreamingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String res = requestByHttpPost();
                            if (res == null) {
                                showToast("Stream Json Got Fail!");
                                return;
                            }
                            Intent intent = new Intent(MainActivity.this, SWCodecCameraStreamingActivity.class);
                            intent.putExtra("stream_json_str", res);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });

        mAudioStreamingBtn = (Button) findViewById(R.id.start_pure_audio_streaming_btn);
        mAudioStreamingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String res = requestByHttpPost();
                            if (res == null) {
                                showToast("Stream Json Got Fail!");
                                return;
                            }
                            Intent intent = new Intent(MainActivity.this, AudioStreamingActivity.class);
                            intent.putExtra("stream_json_str", res);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }
}
