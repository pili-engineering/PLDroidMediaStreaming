package com.qiniu.pili.droid.streaming.demo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final String url = "Your app server url which get StreamJson";
    private static final String url2 = "Your app server url which get PublishUrl";

    private static final String INPUT_TYPE_STREAM_JSON      = "StreamJson";
    private static final String INPUT_TYPE_AUTHORIZED_URL   = "AuthorizedUrl";
    private static final String INPUT_TYPE_UNAUTHORIZED_URL = "UnauthorizedUrl";

    private static final String[] mInputTypeList = {
            "Please select input type of publish url:",
            INPUT_TYPE_STREAM_JSON,
            INPUT_TYPE_AUTHORIZED_URL,
            INPUT_TYPE_UNAUTHORIZED_URL
    };

    private Button mHwCodecCameraStreamingBtn;
    private Button mSwCodecCameraStreamingBtn;
    private Button mAudioStreamingBtn;
    private Button mScreenRecorderStreamingBtn;
    private Button mExtCapStreamingBtn;

    private EditText mInputUrlEditText;

    private String mSelectedInputType = null;

    private boolean mPermissionEnabled = false;

    private static boolean isSupportHWEncode() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    private static boolean isSupportScreenCapture() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    private boolean isPermissionOK() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mPermissionEnabled = true;
            return true;
        }
        else {
            return checkPermission();
        }
    }

    /**
     * Check that all given permissions have been granted by verifying that each entry in the
     * given array is of the value {@link PackageManager#PERMISSION_GRANTED}.
     *
     * @see Activity#onRequestPermissionsResult(int, String[], int[])
     */
    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if(grantResults.length < 1){
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkPermission() {
        boolean ret = true;

        List<String> permissionsNeeded = new ArrayList<String>();
        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.CAMERA)) {
            permissionsNeeded.add("CAMERA");
        }
        if (!addPermission(permissionsList, Manifest.permission.RECORD_AUDIO)) {
            permissionsNeeded.add("MICROPHONE");
        }
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionsNeeded.add("Write external storage");
        }

        if (permissionsNeeded.size() > 0) {
            // Need Rationale
            String message = "You need to grant access to " + permissionsNeeded.get(0);
            for (int i = 1; i < permissionsNeeded.size(); i++) {
                message = message + ", " + permissionsNeeded.get(i);
            }
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permissionsList.get(0))) {
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
            }
            else {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            }
            ret = false;
        }

        return ret;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        boolean ret = true;
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            ret = false;
        }
        return ret;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                if (verifyPermissions(grantResults)) {
                    // All Permissions Granted
                    mPermissionEnabled = true;
                } else {
                    // Permission Denied
                    mPermissionEnabled = false;
                    showToast("Some Permission is Denied");
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private String requestStream(String appServerUrl) {
        try {
            HttpURLConnection httpConn = (HttpURLConnection) new URL(appServerUrl).openConnection();
            httpConn.setRequestMethod("POST");
            httpConn.setConnectTimeout(5000);
            httpConn.setReadTimeout(10000);
            int responseCode = httpConn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            int length = httpConn.getContentLength();
            if (length <= 0) {
                length = 16*1024;
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
        if (!isPermissionOK()) {
            return;
        }

        final String inputUrl = mInputUrlEditText.getText().toString().trim();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String publishUrl = null;
                Log.i(TAG, "mSelectedInputType:" + mSelectedInputType + ",inputUrl:" + inputUrl);
                if (!"".equalsIgnoreCase(inputUrl)) {
                    publishUrl = Config.EXTRA_PUBLISH_URL_PREFIX + inputUrl;
                } else {
                    if (mSelectedInputType != null) {
                        if (INPUT_TYPE_STREAM_JSON.equalsIgnoreCase(mSelectedInputType)) {
                            publishUrl = requestStream(url);
                            if (publishUrl != null) {
                                publishUrl = Config.EXTRA_PUBLISH_JSON_PREFIX + publishUrl;
                            }
                        } else if (INPUT_TYPE_AUTHORIZED_URL.equalsIgnoreCase(mSelectedInputType)) {
                            publishUrl = requestStream(url2);
                            if (publishUrl != null) {
                                publishUrl = Config.EXTRA_PUBLISH_URL_PREFIX + publishUrl;
                            }
                        } else if (INPUT_TYPE_UNAUTHORIZED_URL.equalsIgnoreCase(mSelectedInputType)) {
                            // just for test
                            publishUrl = requestStream(url2);
                            try {
                                URI u = new URI(publishUrl);
                                publishUrl = Config.EXTRA_PUBLISH_URL_PREFIX + String.format("rtmp://401.qbox.net%s?%s", u.getPath(), u.getRawQuery());
                            } catch (Exception e) {
                                e.printStackTrace();
                                publishUrl = null;
                            }
                        } else {
                            throw new IllegalArgumentException("Illegal input type");
                        }
                    }
                }

                if (publishUrl == null) {
                    showToast("Publish Url Got Fail!");
                    return;
                }
                intent.putExtra(Config.EXTRA_KEY_PUB_URL, publishUrl);
                startActivity(intent);
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView mVersionInfoTextView = (TextView) findViewById(R.id.version_info);
        mVersionInfoTextView.setText(Config.VERSION_HINT);

        Spinner inputTypeSpinner = (Spinner) findViewById(R.id.spinner_input_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mInputTypeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputTypeSpinner.setAdapter(adapter);
        inputTypeSpinner.setOnItemSelectedListener(new SpinnerSelectedListener());
        inputTypeSpinner.setVisibility(View.VISIBLE);

        mInputUrlEditText = (EditText) findViewById(R.id.input_url);

        mHwCodecCameraStreamingBtn = (Button) findViewById(R.id.hw_codec_camera_streaming_btn);
        mHwCodecCameraStreamingBtn.setVisibility(View.GONE);
        mHwCodecCameraStreamingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHwCodecCameraStreamingBtn.setClickable(false);
                Intent intent = new Intent(MainActivity.this, HWCodecCameraStreamingActivity.class);
                startStreamingActivity(intent);
            }
        });

        mSwCodecCameraStreamingBtn = (Button) findViewById(R.id.sw_codec_camera_streaming_btn);
        mSwCodecCameraStreamingBtn.setVisibility(View.GONE);
        mSwCodecCameraStreamingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSwCodecCameraStreamingBtn.setClickable(false);
                Intent intent = new Intent(MainActivity.this, SWCodecCameraStreamingActivity.class);
                startStreamingActivity(intent);
            }
        });

        mAudioStreamingBtn = (Button) findViewById(R.id.start_pure_audio_streaming_btn);
        mAudioStreamingBtn.setVisibility(View.GONE);
        mAudioStreamingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAudioStreamingBtn.setClickable(false);
                Intent intent = new Intent(MainActivity.this, AudioStreamingActivity.class);
                startStreamingActivity(intent);
            }
        });

        mScreenRecorderStreamingBtn = (Button) findViewById(R.id.start_screen_recorder_streaming_btn);
        mScreenRecorderStreamingBtn.setVisibility(View.GONE);
        mScreenRecorderStreamingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mScreenRecorderStreamingBtn.setClickable(false);
                Intent intent = new Intent(MainActivity.this, ScreenRecorderActivity.class);
                startStreamingActivity(intent);
            }
        });

        mExtCapStreamingBtn = (Button) findViewById(R.id.start_ext_cap_streaming_btn);
        mExtCapStreamingBtn.setVisibility(View.GONE);
        mExtCapStreamingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mExtCapStreamingBtn.setClickable(false);
                Intent intent = new Intent(MainActivity.this, ExtCapStreamingActivity.class);
                startStreamingActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mHwCodecCameraStreamingBtn != null) {
            mHwCodecCameraStreamingBtn.setClickable(true);
        }
        if (mSwCodecCameraStreamingBtn != null) {
            mSwCodecCameraStreamingBtn.setClickable(true);
        }
        if (mAudioStreamingBtn != null) {
            mAudioStreamingBtn.setClickable(true);
        }
        if (mScreenRecorderStreamingBtn != null) {
            mScreenRecorderStreamingBtn.setClickable(true);
        }
        if (mExtCapStreamingBtn != null) {
            mExtCapStreamingBtn.setClickable(true);
        }
    }

    private class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            if (arg2 != 0) {
                mSelectedInputType = mInputTypeList[arg2];
                if (isSupportHWEncode()) {
                    mHwCodecCameraStreamingBtn.setVisibility(View.VISIBLE);
                }
                mSwCodecCameraStreamingBtn.setVisibility(View.VISIBLE);
                mAudioStreamingBtn.setVisibility(View.VISIBLE);
                if (isSupportScreenCapture()) {
                    mScreenRecorderStreamingBtn.setVisibility(View.VISIBLE);
                }
                mExtCapStreamingBtn.setVisibility(View.VISIBLE);
            }
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

}
