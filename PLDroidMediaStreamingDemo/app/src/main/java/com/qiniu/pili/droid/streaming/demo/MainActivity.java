package com.qiniu.pili.droid.streaming.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.qiniu.pili.droid.streaming.PLAuthenticationResultCallback;
import com.qiniu.pili.droid.streaming.StreamingEnv;
import com.qiniu.pili.droid.streaming.demo.activity.AVStreamingActivity;
import com.qiniu.pili.droid.streaming.demo.activity.AudioStreamingActivity;
import com.qiniu.pili.droid.streaming.demo.activity.ImportStreamingActivity;
import com.qiniu.pili.droid.streaming.demo.activity.ScreenStreamingActivity;
import com.qiniu.pili.droid.streaming.demo.activity.StreamingBaseActivity;
import com.qiniu.pili.droid.streaming.demo.fragment.CameraConfigFragment;
import com.qiniu.pili.droid.streaming.demo.fragment.EncodingConfigFragment;
import com.qiniu.pili.droid.streaming.demo.utils.Cache;
import com.qiniu.pili.droid.streaming.demo.utils.PermissionChecker;
import com.qiniu.pili.droid.streaming.demo.utils.Util;

import java.net.URI;
import java.util.Arrays;
import java.util.UUID;

public class MainActivity extends FragmentActivity {
    private static final String TAG = "MainActivity";

    private static final String GENERATE_STREAM_TEXT = "https://api-demo.qnsdk.com/v1/live/stream/";

    private static final String[] INPUT_TYPES = { "Authorized", "Unauthorized" };
    private static final String[] STREAM_TYPES = { "Video-Audio", "Audio", "Import", "Screen" };
    private static final Class[] ACTIVITY_CLASSES = {
            AVStreamingActivity.class,
            AudioStreamingActivity.class,
            ImportStreamingActivity.class,
            ScreenStreamingActivity.class
    };

    private TextView mInputTextTV;
    private Spinner mInputTypeSpinner;
    private Spinner mStreamTypeSpinner;
    private CheckBox mDebugModeCheckBox;
    private RadioButton mQuicPushButton;

    private EncodingConfigFragment mEncodingConfigFragment;
    private CameraConfigFragment mCameraConfigFragment;

    private PermissionChecker mPermissionChecker = new PermissionChecker(this);

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        mPermissionChecker.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean isInputTypeUnauthorized() {
        return mInputTypeSpinner.getSelectedItemPosition() == 1;
    }

    private void updateInputTextView(final String url) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mInputTextTV.setText(url);
                Cache.saveURL(MainActivity.this, url);
            }
        });
    }

    public void launchStreaming(View v) {
        // API < M, no need to request permissions, so always true.
        boolean isPermissionOK = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || mPermissionChecker.checkPermission();
        if (!isPermissionOK) {
            Util.showToast(this, "Some permissions is not approved !!!");
            return;
        }

        if (mDebugModeCheckBox.isChecked()) {
            StreamingEnv.setLogLevel(Log.VERBOSE);
        }

        boolean quicEnable = mQuicPushButton.isChecked();
        String streamText = mInputTextTV.getText().toString().trim();

        int pos = mStreamTypeSpinner.getSelectedItemPosition();
        Intent intent = new Intent(this, ACTIVITY_CLASSES[pos]);
        intent.putExtra(StreamingBaseActivity.INPUT_TEXT, streamText);
        intent.putExtra(StreamingBaseActivity.TRANSFER_MODE_QUIC, quicEnable);
        intent.putExtras(mEncodingConfigFragment.getIntent());
        boolean bAudioStereo = ((CheckBox) findViewById(R.id.audio_channel_stereo)).isChecked();
        if (bAudioStereo) {
            intent.putExtra(StreamingBaseActivity.AUDIO_CHANNEL_STEREO, bAudioStereo);
        }
        if (pos == 0) {
            intent.putExtras(mCameraConfigFragment.getIntent());
        }
        startActivity(intent);
    }

    public void onClickCheckAuth(View v) {
        StreamingEnv.checkAuthentication(new PLAuthenticationResultCallback() {
            @Override
            public void onAuthorizationResult(int result) {
                String authState;
                if (result == PLAuthenticationResultCallback.UnCheck) {
                    authState = "UnCheck";
                } else if (result == PLAuthenticationResultCallback.UnAuthorized) {
                    authState = "UnAuthorized";
                } else {
                    authState = "Authorized";
                }
                Toast.makeText(MainActivity.this, "auth : " + authState, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initInputTypeSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, INPUT_TYPES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mInputTypeSpinner.setAdapter(adapter);
    }

    private void initStreamTypeSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                Util.isSupportScreenCapture() ? STREAM_TYPES: Arrays.copyOf(STREAM_TYPES, 3));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mStreamTypeSpinner.setAdapter(adapter);
        mStreamTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mEncodingConfigFragment.enableAudioOnly(position == 1);
                mEncodingConfigFragment.enableWatermark(position == 0);
                mEncodingConfigFragment.enablePictureStreaming(position == 0);
                // Import & Screen streaming must specify custom video encoding size
                mEncodingConfigFragment.forceCustomVideoEncodingSize(position == 2 || position == 3);

                View cameraConfigPanel = findViewById(R.id.camera_config_panel);
                cameraConfigPanel.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView versionInfo = (TextView) findViewById(R.id.version_info);
        mInputTextTV = (TextView) findViewById(R.id.input_url);
        mInputTypeSpinner = (Spinner) findViewById(R.id.stream_input_types);
        mStreamTypeSpinner = (Spinner) findViewById(R.id.stream_types);
        mDebugModeCheckBox = (CheckBox) findViewById(R.id.debug_mode);
        mQuicPushButton = (RadioButton) findViewById(R.id.transfer_quic);

        mInputTextTV.setText(Cache.retrieveURL(this));

        FragmentManager fragmentManager = getSupportFragmentManager();
        mEncodingConfigFragment = (EncodingConfigFragment) fragmentManager.findFragmentById(R.id.encoding_config_fragment);
        mCameraConfigFragment = (CameraConfigFragment) fragmentManager.findFragmentById(R.id.camera_config_fragment);

        versionInfo.setText("versionName: " + BuildConfig.VERSION_NAME + " versionCode: " + BuildConfig.VERSION_CODE);
        initInputTypeSpinner();
        initStreamTypeSpinner();
    }

    public void onClickGenPublishURL(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String publishUrl = genPublishURL();
                if (publishUrl != null) {
                    Cache.saveURL(MainActivity.this, publishUrl);
                    updateInputTextView(publishUrl);
                }
            }
        }).start();
    }

    public void scanQRCode(View v) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setOrientationLocked(true);
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.initiateScan();
    }

    public void toggleEncodingConfigFragment(View v) {
        toggleFragment(mEncodingConfigFragment);
    }

    public void toggleCameraConfigFragment(View v) {
        toggleFragment(mCameraConfigFragment);
    }

    private void toggleFragment(Fragment fragment) {
        View v = fragment.getView();
        if (v != null) {
            v.setVisibility(v.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode ==  IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(result != null) {
                if(result.getContents() == null) {
                    Toast.makeText(this, "扫码取消！", Toast.LENGTH_SHORT).show();
                } else {
                    mInputTextTV.setText(result.getContents());
                }
            }
        }
    }

    /**
     * Generate the publish URL, You can request the URL from your app server
     * @return the publish URL
     */
    private String genPublishURL() {
        String publishUrl = Util.syncRequest(GENERATE_STREAM_TEXT + UUID.randomUUID());
        if (publishUrl == null) {
            Util.showToast(MainActivity.this, "Get publish GENERATE_STREAM_TEXT failed !!!");
            return null;
        }
        if (isInputTypeUnauthorized()) {
            // make an unauthorized GENERATE_STREAM_TEXT for effect
            try {
                URI u = new URI(publishUrl);
                publishUrl = String.format("rtmp://401.qbox.net%s?%s", u.getPath(), u.getRawQuery());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return publishUrl;
    }
}
