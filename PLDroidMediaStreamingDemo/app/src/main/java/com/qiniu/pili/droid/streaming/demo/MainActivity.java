package com.qiniu.pili.droid.streaming.demo;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import com.qiniu.pili.droid.streaming.common.FileLogHelper;
import com.qiniu.pili.droid.streaming.demo.activity.AVStreamingActivity;
import com.qiniu.pili.droid.streaming.demo.activity.AudioStreamingActivity;
import com.qiniu.pili.droid.streaming.demo.activity.ImportStreamingActivity;
import com.qiniu.pili.droid.streaming.demo.activity.ScreenStreamingActivity;
import com.qiniu.pili.droid.streaming.demo.fragment.CameraConfigFragment;
import com.qiniu.pili.droid.streaming.demo.fragment.EncodingConfigFragment;
import com.qiniu.pili.droid.streaming.demo.utils.Cache;
import com.qiniu.pili.droid.streaming.demo.utils.Config;
import com.qiniu.pili.droid.streaming.demo.utils.PermissionChecker;
import com.qiniu.pili.droid.streaming.demo.utils.Util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MainActivity extends FragmentActivity {
    private static final String TAG = "MainActivity";

    private static final String GENERATE_STREAM_TEXT = "https://api-demo.qnsdk.com/v1/live/stream/";
    private static final String QN_PUBLISH_URL_PREFIX = "rtmp://pili-publish.qnsdk.com/sdk-live/";
    private static final String QN_PLAY_URL_PREFIX = "rtmp://pili-rtmp.qnsdk.com/sdk-live/";

    private static final String[] STREAM_TYPES = { "Video-Audio", "Audio", "Import", "Screen" };
    private static final Class[] ACTIVITY_CLASSES = {
            AVStreamingActivity.class,
            AudioStreamingActivity.class,
            ImportStreamingActivity.class,
            ScreenStreamingActivity.class
    };

    private TextView mInputTextTV;
    private Spinner mStreamTypeSpinner;
    private CheckBox mDebugModeCheckBox;
    private RadioButton mRtmpPushButton;
    private RadioButton mQuicPushButton;
    private RadioButton mSrtPushButton;

    private EncodingConfigFragment mEncodingConfigFragment;
    private CameraConfigFragment mCameraConfigFragment;

    private PermissionChecker mPermissionChecker = new PermissionChecker(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView versionInfo = (TextView) findViewById(R.id.version_info);
        mInputTextTV = (TextView) findViewById(R.id.input_url);
        mStreamTypeSpinner = (Spinner) findViewById(R.id.stream_types);
        mDebugModeCheckBox = (CheckBox) findViewById(R.id.debug_mode);
        mRtmpPushButton = (RadioButton) findViewById(R.id.transfer_rtmp);
        mQuicPushButton = (RadioButton) findViewById(R.id.transfer_quic);
        mSrtPushButton = (RadioButton) findViewById(R.id.transfer_srt);

        String publishUrl = Cache.retrieveURL(this);
        if (publishUrl.startsWith("srt")) {
            mSrtPushButton.setChecked(true);
        } else {
            mRtmpPushButton.setChecked(true);
        }
        mInputTextTV.setText(publishUrl);

        FragmentManager fragmentManager = getSupportFragmentManager();
        mEncodingConfigFragment = (EncodingConfigFragment) fragmentManager.findFragmentById(R.id.encoding_config_fragment);
        mCameraConfigFragment = (CameraConfigFragment) fragmentManager.findFragmentById(R.id.camera_config_fragment);

        versionInfo.setText("versionName: " + BuildConfig.VERSION_NAME + " versionCode: " + BuildConfig.VERSION_CODE);
        versionInfo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                StreamingEnv.reportLogFiles(new FileLogHelper.LogReportCallback() {
                    @Override
                    public void onReportSuccess(List<String> logNames) {
                        if (logNames.size() == 0) {
                            return;
                        }
                        for (String logName : logNames) {
                            Log.i(TAG, logName);
                        }
                        Log.i(TAG, "日志已上传");
                        Toast.makeText(MainActivity.this, "日志已上传！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onReportError(String name, String errorMsg) {
                        Toast.makeText(MainActivity.this, "日志 " + name + " 上传失败: " + errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            }
        });
        initStreamTypeSpinner();
    }

    @Override
    public void onBackPressed() {
        Fragment primaryNavigationFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q
                && isTaskRoot()
                && (primaryNavigationFragment == null || primaryNavigationFragment.getChildFragmentManager().getBackStackEntryCount() == 0)
                && getSupportFragmentManager().getBackStackEntryCount() == 0) {
            finishAfterTransition();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        mPermissionChecker.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

    public void launchStreaming(View v) {
        // API < M, no need to request permissions, so always true.
        boolean isPermissionOK = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || mPermissionChecker.checkPermission();
        if (!isPermissionOK) {
            Util.showToast(this, "请授予相关权限!!!");
            return;
        }

        String streamText = mInputTextTV.getText().toString().trim();
        if (streamText.isEmpty()) {
            Util.showToast(this, "推流地址不能为空!!!");
            return;
        }
        if ((mSrtPushButton.isChecked() && !streamText.startsWith("srt"))
                || (!mSrtPushButton.isChecked() && !streamText.startsWith("rtmp"))) {
            Util.showToast(this, "请检查推流地址和协议是否匹配!!!");
            return;
        }

        if (mDebugModeCheckBox.isChecked()) {
            StreamingEnv.setLogLevel(Log.VERBOSE);
        }

        boolean quicEnable = mQuicPushButton.isChecked();
        boolean srtEnable = mSrtPushButton.isChecked();

        int pos = mStreamTypeSpinner.getSelectedItemPosition();

        boolean isAudioStereo = ((CheckBox) findViewById(R.id.audio_channel_stereo)).isChecked();
        boolean isVoIPRecord = ((CheckBox) findViewById(R.id.VoIP_record)).isChecked();
        boolean isBluetoothScoOn = ((CheckBox) findViewById(R.id.bluetooth_sco_on)).isChecked();

        Intent intent = new Intent(this, ACTIVITY_CLASSES[pos]);
        intent.putExtra(Config.PUBLISH_URL, streamText);
        intent.putExtra(Config.TRANSFER_MODE_QUIC, quicEnable);
        intent.putExtra(Config.TRANSFER_MODE_SRT, srtEnable);
        intent.putExtras(mEncodingConfigFragment.getIntent());
        intent.putExtra(Config.AUDIO_CHANNEL_STEREO, isAudioStereo);
        intent.putExtra(Config.AUDIO_VOIP_RECORD, isVoIPRecord);
        intent.putExtra(Config.AUDIO_SCO_ON, isBluetoothScoOn);
        if (pos == 0) {
            intent.putExtras(mCameraConfigFragment.getIntent());
        }
        startActivity(intent);
    }

    public void onClickCopyPlayUrl(View v) {
        String publishUrl = mInputTextTV.getText().toString().trim();
        if (!isQNPublishUrl(publishUrl)) {
            Util.showToast(this, "仅支持复制 demo 内置推流地址对应的播放链接！");
            return;
        }
        String playUrl = QN_PLAY_URL_PREFIX + getStreamName(publishUrl);
        copyToClipboard(playUrl);
        Util.showToast(this, "播放链接 " + playUrl + " 已经复制到粘贴板！");
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

    public void onClickGenPublishURL(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String publishUrl = genPublishURL();
                if (mSrtPushButton.isChecked() && publishUrl.startsWith("rtmp://")) {
                    publishUrl = getSrtPublishUrl(publishUrl);
                }
                if (publishUrl != null) {
                    Cache.saveURL(MainActivity.this, publishUrl);
                    updateInputTextView(publishUrl);
                }
            }
        }).start();
    }

    public void scanQRCode(View v) {
        // API < M, no need to request permissions, so always true.
        boolean isPermissionOK = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || mPermissionChecker.checkPermission();
        if (!isPermissionOK) {
            Util.showToast(this, "请授予相关权限!!!");
            return;
        }
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

    /**
     * 关于服务端推流地址的生成方式，可以参考 https://developer.qiniu.com/pili/api/2767/the-rtmp-push-flow-address
     * 关于服务端 SDK，可以参考 https://developer.qiniu.com/pili/sdk/1220/server-sdk
     *
     * @return 生成的推流地址
     */
    private String genPublishURL() {
        String publishUrl = Util.syncRequest(GENERATE_STREAM_TEXT + UUID.randomUUID());
        if (publishUrl == null) {
            Util.showToast(MainActivity.this, "Get publish GENERATE_STREAM_TEXT failed !!!");
            return null;
        }
        return publishUrl;
    }

    /**
     * 自定义组装 SRT 推流地址
     *
     * 建议由您业务服务端生成符合规范的 SRT 推流地址
     * 地址规范可参考：https://github.com/Haivision/srt/blob/master/docs/features/access-control.md#general-syntax
     *
     * @param rtmpUrl rtmp 地址
     * @return 组装的 SRT 地址
     */
    private String getSrtPublishUrl(String rtmpUrl) {
        URI u;
        try {
            u = new URI(rtmpUrl);
            String path = u.getPath().substring(1);
            String query = u.getQuery();
            StringBuilder publishUrl = new StringBuilder(String.format("srt://%s?streamid=#!::h=%s,m=publish", u.getHost(), path));
            if (query != null) {
                String[] queries = query.split("&");
                for (String q : queries) {
                    publishUrl.append(",").append(q);
                }
            }
            return publishUrl.toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return "";
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

    private boolean isQNPublishUrl(String url) {
        if (url == null) {
            Log.i(TAG, "Url can not be null !");
            return false;
        }
        return url.startsWith(QN_PUBLISH_URL_PREFIX);
    }

    private String getStreamName(String publishUrl) {
        if (publishUrl == null) {
            Log.i(TAG, "Url can not be null !");
            return null;
        }
        int startIndex = publishUrl.lastIndexOf("/");
        int endIndex = publishUrl.indexOf("?");
        return publishUrl.substring(startIndex + 1, endIndex);
    }

    private void copyToClipboard(String content) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            ClipData clipData = ClipData.newPlainText("Label", content);
            cm.setPrimaryClip(clipData);
        }
    }
}
