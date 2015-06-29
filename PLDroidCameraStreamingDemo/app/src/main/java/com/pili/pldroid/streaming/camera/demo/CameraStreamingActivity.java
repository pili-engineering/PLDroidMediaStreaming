package com.pili.pldroid.streaming.camera.demo;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.pili.pldroid.streaming.CameraStreamingManager;
import com.pili.pldroid.streaming.CameraStreamingManager.STATE;
import com.pili.pldroid.streaming.CameraStreamingSetting;
import com.pili.pldroid.streaming.StreamingProfile;
import com.pili.pldroid.streaming.StreamingProfile.Stream;
import com.pili.pldroid.streaming.widget.AspectFrameLayout;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jerikc on 15/6/16.
 */
public class CameraStreamingActivity extends Activity implements CameraStreamingManager.StreamingStateListener {
    private static final String TAG = "CameraStreamingActivity";

    private CameraStreamingManager mCameraStreamingManager;
    private TextView mSatusTextView;
    private Button mShutterButton;
    private boolean mShutterButtonPressed = false;

    private String mStatusMsgContent;

    private static final int MSG_UPDATE_SHUTTER_BUTTON_STATE = 0;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_SHUTTER_BUTTON_STATE:
                    mShutterButtonPressed = !mShutterButtonPressed;
                    mShutterButton.setPressed(mShutterButtonPressed);
                    if (mShutterButtonPressed) {
                        boolean res = mCameraStreamingManager.startStreaming();
                        Log.i(TAG, "res:" + res);
                        if (!res) {
                            mShutterButtonPressed = !mShutterButtonPressed;
                            mShutterButton.setPressed(mShutterButtonPressed);
                        }
                    } else {
                        mCameraStreamingManager.stopStreaming();
                    }
                    break;
                default:
                    Log.e(TAG, "Invalid message");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_camera_streaming);

        AspectFrameLayout afl = (AspectFrameLayout) findViewById(R.id.cameraPreview_afl);
        GLSurfaceView glSurfaceView = (GLSurfaceView) findViewById(R.id.cameraPreview_surfaceView);
        mShutterButton = (Button) findViewById(R.id.toggleRecording_button);

        mSatusTextView = (TextView) findViewById(R.id.streamingStatus);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Get publish host from your server
        String publishHost = "publish host from server";  // such as "f9zdwh.pub.z1.pili.qiniup.com"
       /*
        * You should get the streamJson from your server, maybe like this:
        *
        * Step 1: Get streamJsonStrFromServer from server
        * URL url = new URL(yourURL);
        * URLConnection conn = url.openConnection();
        *
        * HttpURLConnection httpConn = (HttpURLConnection) conn;
        * httpConn.setAllowUserInteraction(false);
        * httpConn.setInstanceFollowRedirects(true);
        * httpConn.setRequestMethod("GET");
        * httpConn.connect();
        *
        * InputStream is = httpConn.getInputStream();
        * streamJsonStrFromServer = convertInputStreamToString(is);
        *
        * Step 2: Instantiate streamJson object
        * JSONObject streamJson = new JSONObject(streamJsonStrFromServer);
        *
        *
        * Then you can use streamJson to instantiate stream object
        * Stream stream = new Stream(streamJson);
        *
        * */
        String streamJsonStrFromServer = "{"
                + "\"id\"             :\"z1.live.55910c19fb16df0cbf00af8e\", "
                + "\"hub\"            :\"live\", "
                + "\"title\"          :\"55910c19fb16df0cbf00af8e\", "
                + "\"publishKey\"     :\"b06c7427b454762c\", "
                + "\"publishSecurity\":\"dynamic\", "
                + "..."
                + "}";

        JSONObject streamJson = null;
        try {
            streamJson = new JSONObject(streamJsonStrFromServer);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Stream stream = new Stream(streamJson);
        StreamingProfile profile = new StreamingProfile();
        profile.setQuality(StreamingProfile.QUALITY_LOW1)
                .setStreamAndPublishhost(stream, publishHost);

        CameraStreamingSetting setting = new CameraStreamingSetting();
        setting.setCameraId(Camera.CameraInfo.CAMERA_FACING_BACK)
                .setStreamingProfile(profile)
                .setCameraPreviewSize(1280, 720);

        mCameraStreamingManager = new CameraStreamingManager(this, afl, glSurfaceView);
        mCameraStreamingManager.onPrepare(setting);
        mCameraStreamingManager.setStreamingStateListener(this);

        mShutterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShutterButtonClick();
            }
        });
    }

    private void onShutterButtonClick() {
        mHandler.removeMessages(MSG_UPDATE_SHUTTER_BUTTON_STATE);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_UPDATE_SHUTTER_BUTTON_STATE), 50);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraStreamingManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraStreamingManager.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCameraStreamingManager.onDestroy();
    }

    private void setShutterButtonPressed(final boolean pressed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mShutterButtonPressed = pressed;
                mShutterButton.setPressed(pressed);
            }
        });
    }

    private void setShutterButtonEnabled(final boolean enable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mShutterButton.setFocusable(enable);
                mShutterButton.setClickable(enable);
                mShutterButton.setEnabled(enable);
            }
        });
    }

    @Override
    public void onStateChanged(final int state) {
        Log.i(TAG, "onStateChanged state:" + state);
        switch (state) {
            case STATE.PREPARING:
                mStatusMsgContent = getString(R.string.string_state_preparing);
                break;
            case STATE.READY:
                mStatusMsgContent = getString(R.string.string_state_ready);
                // start streaming when READY
                onShutterButtonClick();
                break;
            case STATE.CONNECTING:
                mStatusMsgContent = getString(R.string.string_state_connecting);
                break;
            case STATE.STREAMING:
                mStatusMsgContent = getString(R.string.string_state_streaming);
                break;
            case STATE.SHUTDOWN:
                mStatusMsgContent = getString(R.string.string_state_ready);
                setShutterButtonPressed(false);
                setShutterButtonEnabled(true);
                break;
            case STATE.IOERROR:
                onShutterButtonClick();
                mStatusMsgContent = getString(R.string.string_state_ready);
                break;
            case STATE.NETBLOCKING:
                mStatusMsgContent = getString(R.string.string_state_netblocking);
                setShutterButtonEnabled(false);
                break;
            case STATE.UNKNOWN:
                mStatusMsgContent = getString(R.string.string_state_ready);
                break;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSatusTextView.setText(mStatusMsgContent);
            }
        });
    }
}
