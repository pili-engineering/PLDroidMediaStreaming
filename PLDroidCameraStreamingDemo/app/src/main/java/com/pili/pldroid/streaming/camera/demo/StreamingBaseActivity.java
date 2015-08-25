package com.pili.pldroid.streaming.camera.demo;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.pili.pldroid.streaming.CameraStreamingManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jerikc on 15/7/6.
 */
public class StreamingBaseActivity extends Activity implements CameraStreamingManager.StreamingStateListener {

    private static final String TAG = "StreamingBaseActivity";

    protected Button mShutterButton;
    protected boolean mShutterButtonPressed = false;

    protected static final int MSG_UPDATE_SHUTTER_BUTTON_STATE = 0;
    protected String mStatusMsgContent;
    protected TextView mSatusTextView;

    protected CameraStreamingManager mCameraStreamingManager;

    protected JSONObject mJSONObject;

    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_SHUTTER_BUTTON_STATE:
                    if (!mShutterButtonPressed) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // disable the shutter button before startStreaming
                                setShutterButtonEnabled(false);
                                boolean res = mCameraStreamingManager.startStreaming();
                                mShutterButtonPressed = true;
                                Log.i(TAG, "res:" + res);
                                if (!res) {
                                    mShutterButtonPressed = false;
                                    setShutterButtonEnabled(true);
                                }
                                setShutterButtonPressed(mShutterButtonPressed);
                            }
                        }).start();
                    } else {
                        // disable the shutter button before stopStreaming
                        setShutterButtonEnabled(false);
                        mCameraStreamingManager.stopStreaming();
                        setShutterButtonPressed(false);
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

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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
        String streamJsonStrFromServer = "stream json string from your server";

        try {
            mJSONObject = new JSONObject(streamJsonStrFromServer);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraStreamingManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mShutterButtonPressed = false;
        mCameraStreamingManager.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCameraStreamingManager.onDestroy();
    }

    @Override
    public void onStateChanged(final int state, Object extra) {
        Log.i(TAG, "onStateChanged state:" + state);
        switch (state) {
            case CameraStreamingManager.STATE.PREPARING:
                mStatusMsgContent = getString(R.string.string_state_preparing);
                break;
            case CameraStreamingManager.STATE.READY:
                mStatusMsgContent = getString(R.string.string_state_ready);
                // start streaming when READY
                onShutterButtonClick();
                break;
            case CameraStreamingManager.STATE.CONNECTING:
                mStatusMsgContent = getString(R.string.string_state_connecting);
                break;
            case CameraStreamingManager.STATE.STREAMING:
                mStatusMsgContent = getString(R.string.string_state_streaming);
                setShutterButtonEnabled(true);
                break;
            case CameraStreamingManager.STATE.SHUTDOWN:
                mStatusMsgContent = getString(R.string.string_state_ready);
                setShutterButtonEnabled(true);
                setShutterButtonPressed(false);
                break;
            case CameraStreamingManager.STATE.IOERROR:
                mStatusMsgContent = getString(R.string.string_state_ready);
                setShutterButtonEnabled(true);
                break;
            case CameraStreamingManager.STATE.NETBLOCKING:
                mStatusMsgContent = getString(R.string.string_state_netblocking);
                break;
            case CameraStreamingManager.STATE.CONNECTION_TIMEOUT:
                mStatusMsgContent = getString(R.string.string_state_con_timeout);
                break;
            case CameraStreamingManager.STATE.UNKNOWN:
                mStatusMsgContent = getString(R.string.string_state_ready);
                break;
            case CameraStreamingManager.STATE.SENDING_BUFFER_EMPTY:
                break;
            case CameraStreamingManager.STATE.SENDING_BUFFER_FULL:
                break;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSatusTextView.setText(mStatusMsgContent);
            }
        });
    }

    @Override
    public boolean onStateHandled(final int state, Object extra) {
        Log.i(TAG, "onStateHandled state:" + state);
        return false;
    }

    protected void setShutterButtonPressed(final boolean pressed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mShutterButtonPressed = pressed;
                mShutterButton.setPressed(pressed);
            }
        });
    }

    protected void setShutterButtonEnabled(final boolean enable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mShutterButton.setFocusable(enable);
                mShutterButton.setClickable(enable);
                mShutterButton.setEnabled(enable);
            }
        });
    }

    protected void onShutterButtonClick() {
        mHandler.removeMessages(MSG_UPDATE_SHUTTER_BUTTON_STATE);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_UPDATE_SHUTTER_BUTTON_STATE), 50);
    }
}
