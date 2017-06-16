package com.qiniu.pili.droid.streaming.demo;

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
import android.widget.Spinner;
import android.widget.TextView;

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

public class MainActivity extends FragmentActivity {
    private static final String TAG = "MainActivity";

    private static final String GENERATE_STREAM_TEXT_V1 = "Your app server url which get StreamJson";
    private static final String GENERATE_STREAM_TEXT_V2 = "Your app server url which get PublishUrl";

    private static final String[] INPUT_TYPES = { "Authorized", "Unauthorized", "JSON" };
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

    private EncodingConfigFragment mEncodingConfigFragment;
    private CameraConfigFragment mCameraConfigFragment;

    private PermissionChecker mPermissionChecker = new PermissionChecker(this);

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        mPermissionChecker.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean isInputTypeJSON() {
        return mInputTypeSpinner.getSelectedItemPosition() == 2;
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

        String streamText = mInputTextTV.getText().toString().trim();
        int streamType = isInputTypeJSON()
                ? StreamingBaseActivity.INPUT_TYPE_JSON
                : StreamingBaseActivity.INPUT_TYPE_URL;

        int pos = mStreamTypeSpinner.getSelectedItemPosition();
        Intent intent = new Intent(this, ACTIVITY_CLASSES[pos]);
        intent.putExtra(StreamingBaseActivity.INPUT_TYPE, streamType);
        intent.putExtra(StreamingBaseActivity.INPUT_TEXT, streamText);
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

    /**
     * Generate the publish URL, You can request the URL from your app server
     * @return the publish URL
     */
    private String genPublishURL() {
        String publishUrl = Util.syncRequest(isInputTypeJSON() ? GENERATE_STREAM_TEXT_V1 : GENERATE_STREAM_TEXT_V2);
        if (publishUrl == null) {
            Util.showToast(MainActivity.this, "Get publish GENERATE_STREAM_TEXT_V1 failed !!!");
            return null;
        }
        if (isInputTypeUnauthorized()) {
            // make an unauthorized GENERATE_STREAM_TEXT_V1 for effect
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
