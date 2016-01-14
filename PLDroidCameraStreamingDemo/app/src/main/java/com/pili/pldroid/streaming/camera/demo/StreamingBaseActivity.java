package com.pili.pldroid.streaming.camera.demo;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pili.pldroid.streaming.CameraStreamingManager;
import com.pili.pldroid.streaming.SharedLibraryNameHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by jerikc on 15/7/6.
 */
public class StreamingBaseActivity extends Activity implements
        CameraStreamingManager.StreamingSessionListener,
        CameraStreamingManager.StreamingStateListener {

    private static final String TAG = "StreamingBaseActivity";

    protected Button mShutterButton;
    protected boolean mShutterButtonPressed = false;

    protected static final int MSG_START_STREAMING = 0;
    protected static final int MSG_STOP_STREAMING = 1;

    protected String mStatusMsgContent;
    protected TextView mSatusTextView;
    protected String mLogContent = "\n";

    protected CameraStreamingManager mCameraStreamingManager;

    protected JSONObject mJSONObject;
    protected boolean isEncOrientationPort = true;

    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START_STREAMING:
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
                    break;
                case MSG_STOP_STREAMING:
                    // disable the shutter button before stopStreaming
                    setShutterButtonEnabled(false);
                    boolean res = mCameraStreamingManager.stopStreaming();
                    if (!res) {
                        mShutterButtonPressed = true;
                        setShutterButtonEnabled(true);
                    }
                    setShutterButtonPressed(mShutterButtonPressed);
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

        if (Config.SCREEN_ORIENTATION == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            isEncOrientationPort = true;
        } else if (Config.SCREEN_ORIENTATION == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            isEncOrientationPort = false;
        }
        setRequestedOrientation(Config.SCREEN_ORIENTATION);
//
//        SharedLibraryNameHelper.getInstance().renameSharedLibrary(
//                SharedLibraryNameHelper.PLSharedLibraryType.PL_SO_TYPE_AAC,
//                getApplicationInfo().nativeLibraryDir + "/libpldroid_streaming_aac_encoder_v7a.so");
//
//        SharedLibraryNameHelper.getInstance().renameSharedLibrary(
//                SharedLibraryNameHelper.PLSharedLibraryType.PL_SO_TYPE_CORE, "pldroid_streaming_core");
//
//        SharedLibraryNameHelper.getInstance().renameSharedLibrary(
//                SharedLibraryNameHelper.PLSharedLibraryType.PL_SO_TYPE_H264, "pldroid_streaming_h264_encoder_v7a");

        String streamJsonStrFromServer = getIntent().getStringExtra("stream_json_str");

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
                startStreaming();
                break;
            case CameraStreamingManager.STATE.CONNECTING:
                mStatusMsgContent = getString(R.string.string_state_connecting);
                break;
            case CameraStreamingManager.STATE.STREAMING:
                mStatusMsgContent = getString(R.string.string_state_streaming);
                setShutterButtonEnabled(true);
                setShutterButtonPressed(true);
                break;
            case CameraStreamingManager.STATE.SHUTDOWN:
                mStatusMsgContent = getString(R.string.string_state_ready);
                setShutterButtonEnabled(true);
                setShutterButtonPressed(false);
                break;
            case CameraStreamingManager.STATE.IOERROR:
                mLogContent += "IOERROR\n";
                mStatusMsgContent = getString(R.string.string_state_ready);
                setShutterButtonEnabled(true);
                break;
            case CameraStreamingManager.STATE.NETBLOCKING:
                mLogContent += "NETBLOCKING\n";
                mStatusMsgContent = getString(R.string.string_state_netblocking);
                break;
            case CameraStreamingManager.STATE.CONNECTION_TIMEOUT:
                mLogContent += "CONNECTION_TIMEOUT\n";
                mStatusMsgContent = getString(R.string.string_state_con_timeout);
                break;
            case CameraStreamingManager.STATE.UNKNOWN:
                mStatusMsgContent = getString(R.string.string_state_ready);
                break;
            case CameraStreamingManager.STATE.SENDING_BUFFER_EMPTY:
                break;
            case CameraStreamingManager.STATE.SENDING_BUFFER_FULL:
                break;
            case CameraStreamingManager.STATE.AUDIO_RECORDING_FAIL:
                break;
            case CameraStreamingManager.STATE.OPEN_CAMERA_FAIL:
                Log.e(TAG, "Open Camera Fail. id:" + extra);
                break;
            case CameraStreamingManager.STATE.DISCONNECTED:
                mLogContent += "DISCONNECTED\n";
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

    protected void startStreaming() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_START_STREAMING), 50);
    }

    protected void stopStreaming() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_STOP_STREAMING), 50);
    }

    @Override
    public boolean onRecordAudioFailedHandled(int err) {
        mCameraStreamingManager.updateEncodingType(CameraStreamingManager.EncodingType.SW_VIDEO_CODEC);
        mCameraStreamingManager.startStreaming();
        return true;
    }

    @Override
    public boolean onRestartStreamingHandled(int err) {
        Log.i(TAG, "onRestartStreamingHandled");
        return mCameraStreamingManager.startStreaming();
    }

    @Override
    public Camera.Size onPreviewSizeSelected(List<Camera.Size> list) {
        if (list != null) {
            for (Camera.Size s : list) {
                Log.i(TAG, "w:" + s.width + ", h:" + s.height);
            }
//            return "your choice";
        }
        return null;
    }
}
