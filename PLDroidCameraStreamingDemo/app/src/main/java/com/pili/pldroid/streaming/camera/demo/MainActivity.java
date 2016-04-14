package com.pili.pldroid.streaming.camera.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pili.Hub;
import com.pili.PiliException;
import com.pili.Stream;
import com.qiniu.Credentials;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends Activity {
    @Bind(R.id.server_info)
    EditText serverInfo;
    @Bind(R.id.stream_id)
    EditText streamID;
    @Bind(R.id.hub_name)
    EditText hubName;
    @Bind(R.id.ak)
    EditText ak;
    @Bind(R.id.sk)
    EditText sk;

    private static final String TAG = "MainActivity";

    private static final int BOTH_EMPTY = -1;
    private static final int BOTH_FULL = -2;
    private static final int LOCAL = 1;
    private static final int REMOTE = 0;
    private static final boolean HAS_SERVER = true;
    private static final boolean NO_SERVER = false;

    private static boolean isSupportHWEncode() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    private int getInput() {
        boolean isServerFull = !serverInfo.getText().toString().matches("");
        boolean isPushFull = ! (streamID.getText().toString().matches("") && hubName.getText().toString().matches("")
                && ak.getText().toString().matches("") && sk.getText().toString().matches(""));

        if (!isServerFull && !isPushFull) {
            showToast("Please fill server address or push info");
            return BOTH_EMPTY;
        }
        if (isPushFull && isServerFull) {
            showToast("Do not fill two kind of fields in the same time");
            return BOTH_FULL;
        }
        if (isPushFull) return LOCAL;
       else return REMOTE;
    }

    private String requestStreamJson(boolean hasServer) {
        if (hasServer) {
            try {
                HttpURLConnection httpConn = (HttpURLConnection) new URL(serverInfo.getText().toString()).openConnection();
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
        } else {
            Credentials credentials = new Credentials(ak.getText().toString(), sk.getText().toString());
            Hub hub = new Hub(credentials, hubName.getText().toString());
            String streamID = this.streamID.getText().toString();
            try {
                Stream stream = hub.getStream(streamID);
                return stream.toJsonString();
            } catch (PiliException e) {
                showToast("Error push info");
            }
        }

        return null;
    }

    void showToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startStreamingActivity(final Intent intent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String resByHttp = null;

                if (Config.DEBUG_MODE) {
                    showToast("Stream Json Got Fail!");
                } else {
                    int status = getInput();
                    showToast(Integer.toString(status));
                    if (status == BOTH_FULL) return;
                    if (status == BOTH_EMPTY) return;
                    if (status == LOCAL) resByHttp = requestStreamJson(NO_SERVER);
                    if (status== REMOTE) resByHttp = requestStreamJson(HAS_SERVER);

                    Log.i(TAG, "resByHttp:" + resByHttp);
                    if (resByHttp == null) {
                        showToast("Stream Json Got Fail!");
                        return;
                    }
                    intent.putExtra(Config.EXTRA_KEY_STREAM_JSON, resByHttp);
                }
                startActivity(intent);
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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
