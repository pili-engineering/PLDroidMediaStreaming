package com.qiniu.pili.droid.streaming.demo.activity;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.qiniu.pili.droid.streaming.ScreenSetting;
import com.qiniu.pili.droid.streaming.ScreenStreamingManager;
import com.qiniu.pili.droid.streaming.StreamStatusCallback;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.StreamingSessionListener;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;
import com.qiniu.pili.droid.streaming.demo.plain.EncodingConfig;
import com.qiniu.pili.droid.streaming.demo.utils.Config;
import com.qiniu.pili.droid.streaming.demo.R;
import com.qiniu.pili.droid.streaming.demo.utils.Util;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.app.Notification.VISIBILITY_PRIVATE;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ScreenStreamingActivity extends Activity {

    private static final String TAG = "ScreenStreamingActivity";
    private static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss:SSS";

    private final int NOTIFICATION_ID = 1010100;

    private TextView mTimeTv;
    private TextView mLogTextView;
    private TextView mStatusTextView;
    private TextView mStatView;
    private Button mShutterButton;

    private String mStatusMsgContent;
    private String mLogContent = "\n";

    private boolean mShutterButtonPressed = false;
    private String mPublishUrl;
    private boolean mIsQuicEnabled;
    private boolean mIsSrtEnabled;
    private boolean mIsReady;

    private SimpleDateFormat mDateFormat;

    private Handler mHandler = new Handler();

    private EncodingConfig mEncodingConfig;

    private ScreenStreamingManager mScreenStreamingManager;
    private StreamingProfile mProfile;

    private ImageSwitcher mImageSwitcher;
    private int mTimes = 0;
    private boolean mIsPictureStreaming = false;

    // 用于处理子线程操作
    private Handler mSubThreadHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(Config.SCREEN_ORIENTATION);

        // 获取推流编码配置信息
        mEncodingConfig = (EncodingConfig) getIntent().getSerializableExtra(Config.NAME_ENCODING_CONFIG);

        Intent intent = getIntent();
        mPublishUrl = intent.getStringExtra(Config.PUBLISH_URL);
        mIsQuicEnabled = intent.getBooleanExtra(Config.TRANSFER_MODE_QUIC, false);
        mIsSrtEnabled = intent.getBooleanExtra(Config.TRANSFER_MODE_SRT, false);

        mDateFormat = new SimpleDateFormat(TIME_PATTERN);

        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mSubThreadHandler = new Handler(handlerThread.getLooper());

        // 初始化视图控件
        initView();
        // 初始化 StreamingProfile，StreamingProfile 为推流相关的配置类，详情可参考 https://developer.qiniu.com/pili/sdk/3719/PLDroidMediaStreaming-function-using#4
        initEncodingProfile();
        // 初始化 MediaStreamingManager，使用姿势可参考 https://developer.qiniu.com/pili/sdk/3719/PLDroidMediaStreaming-function-using#6
        initStreamingManager();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubThreadHandler != null) {
            mSubThreadHandler.getLooper().quit();
        }
        // 销毁推流 Manager 的资源
        mScreenStreamingManager.destroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 初始化录屏推流 demo 相关的视图控件
     */
    protected void initView() {
        setContentView(R.layout.activity_screen_streaming);
        mTimeTv = (TextView) findViewById(R.id.time_tv);
        Button picStreamingBtn = (Button) findViewById(R.id.pic_streaming_btn);
        mLogTextView = (TextView) findViewById(R.id.log_info);
        mLogTextView.setTextColor(Color.WHITE);
        mStatusTextView = (TextView) findViewById(R.id.streamingStatus);
        mStatView = (TextView) findViewById(R.id.stream_status);
        mStatView.setTextColor(Color.WHITE);
        mShutterButton = (Button) findViewById(R.id.toggleRecording_button);

        mShutterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIsReady) {
                    Toast.makeText(ScreenStreamingActivity.this, "需要在 READY 状态后才可以开始推流！！！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mShutterButtonPressed) {
                    stopStreamingInternal();
                } else {
                    startStreamingInternal();
                }
            }
        });
        picStreamingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProfile.setPictureStreamingFps(10);
                togglePictureStreaming();
            }
        });

        mHandler.post(mUpdateTimeTvRunnable);
    }

    /**
     * 初始化编码配置项 {@link StreamingProfile}
     */
    private void initEncodingProfile() {
        mProfile = new StreamingProfile();
        // 设置推流地址
        try {
            mProfile.setPublishUrl(mPublishUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        // 是否开启 QUIC 推流。
        // QUIC 是基于 UDP 开发的可靠传输协议，在弱网下拥有更好的推流效果，相比于 TCP 拥有更低的延迟，可抵抗更高的丢包率。
        mProfile.setQuicEnable(mIsQuicEnabled);
        mProfile.setSrtEnabled(mIsSrtEnabled);

        // 自定义配置音频的采样率、码率以及声道数的对象，如果使用预设配置，则无需实例化
        StreamingProfile.AudioProfile aProfile = null;
        // 自定义配置视频的帧率、码率、GOP 以及 H264 Profile 的对象，如果使用预设配置，则无需实例化
        StreamingProfile.VideoProfile vProfile = null;

        if (!mEncodingConfig.mIsAudioOnly) {
            // 设置视频质量参数
            if (mEncodingConfig.mIsVideoQualityPreset) {
                // 使用预设的视频质量等级
                // 预设等级可以参考 https://developer.qiniu.com/pili/sdk/3719/PLDroidMediaStreaming-function-using#4 的 4.2 小节
                mProfile.setVideoQuality(mEncodingConfig.mVideoQualityPreset);
            } else {
                // 使用自定义视频质量参数配置，自定义配置优先级高于预设等级配置
                vProfile = new StreamingProfile.VideoProfile(
                        mEncodingConfig.mVideoQualityCustomFPS,
                        mEncodingConfig.mVideoQualityCustomBitrate * 1024,
                        mEncodingConfig.mVideoQualityCustomMaxKeyFrameInterval,
                        mEncodingConfig.mVideoQualityCustomProfile
                );
            }

            // 设置推流编码尺寸
            if (mEncodingConfig.mIsVideoSizePreset) {
                // 使用预设的视频尺寸
                // 预设尺寸可以参考 https://developer.qiniu.com/pili/sdk/3719/PLDroidMediaStreaming-function-using#4 的 4.7 小节
                mProfile.setEncodingSizeLevel(mEncodingConfig.mVideoSizePreset);
            } else {
                // 使用自定义视频编码尺寸，自定义配置优先级高于预设等级配置
                mProfile.setPreferredVideoEncodingSize(mEncodingConfig.mVideoSizeCustomWidth, mEncodingConfig.mVideoSizeCustomHeight);
            }

            // 设置推流 Orientation
            mProfile.setEncodingOrientation(mEncodingConfig.mVideoOrientationPortrait ? StreamingProfile.ENCODING_ORIENTATION.PORT : StreamingProfile.ENCODING_ORIENTATION.LAND);
            // 软编场景下设置码流控制方式
            // QUALITY_PRIORITY 场景下为了保证推流质量，实际码率可能会高于目标码率
            // BITRATE_PRIORITY 场景下，会优先保证目标码率的稳定性
            mProfile.setEncoderRCMode(mEncodingConfig.mVideoRateControlQuality ? StreamingProfile.EncoderRCModes.QUALITY_PRIORITY : StreamingProfile.EncoderRCModes.BITRATE_PRIORITY);
            // 设置是否开启帧率控制
            mProfile.setFpsControllerEnable(mEncodingConfig.mVideoFPSControl);
            mProfile.setYuvFilterMode(mEncodingConfig.mYuvFilterMode);
            // 设置码率调整模式，如果开启自适应码率，则需指定自适应码率的上下限（当前仅支持 150kbps ~ 2000kbps 区间内的设置）。
            mProfile.setBitrateAdjustMode(mEncodingConfig.mBitrateAdjustMode);
            if (mEncodingConfig.mBitrateAdjustMode == StreamingProfile.BitrateAdjustMode.Auto) {
                mProfile.setVideoAdaptiveBitrateRange(mEncodingConfig.mAdaptiveBitrateMin * 1024, mEncodingConfig.mAdaptiveBitrateMax * 1024);
            }
        }

        // 设置音频质量参数
        if (mEncodingConfig.mIsAudioQualityPreset) {
            // 使用预设的音频质量等级
            // 预设等级可以参考 https://developer.qiniu.com/pili/sdk/3719/PLDroidMediaStreaming-function-using#4 的 4.3 小节
            mProfile.setAudioQuality(mEncodingConfig.mAudioQualityPreset);
        } else {
            // 使用自定义音频质量参数
            aProfile = new StreamingProfile.AudioProfile(
                    mEncodingConfig.mAudioQualityCustomSampleRate,
                    mEncodingConfig.mAudioQualityCustomBitrate * 1024
            );
        }

        // 传入自定义音视频质量配置
        if (aProfile != null || vProfile != null) {
            StreamingProfile.AVProfile avProfile = new StreamingProfile.AVProfile(vProfile, aProfile);
            mProfile.setAVProfile(avProfile);
        }

        // 设置图片推流的图片地址
        if (mEncodingConfig.mIsPictureStreamingEnabled) {
            if (mEncodingConfig.mPictureStreamingFilePath == null) {
                mProfile.setPictureStreamingResourceId(R.drawable.pause_publish);
            } else {
                mProfile.setPictureStreamingFilePath(mEncodingConfig.mPictureStreamingFilePath);
            }
        }

        // 其他配置项
        mProfile.setDnsManager(Util.getMyDnsManager(this))
                .setStreamStatusConfig(new StreamingProfile.StreamStatusConfig(3))
                .setSendingBufferProfile(new StreamingProfile.SendingBufferProfile(0.2f, 0.8f, 3.0f, 20 * 1000));
    }

    /**
     * 初始化推流管理类
     */
    protected void initStreamingManager() {
        ScreenSetting screenSetting = new ScreenSetting();
        screenSetting.setSize(mEncodingConfig.mVideoSizeCustomWidth, mEncodingConfig.mVideoSizeCustomHeight);
        screenSetting.setDpi(1);

        mScreenStreamingManager = new ScreenStreamingManager();
        mScreenStreamingManager.setStreamingSessionListener(mStreamingSessionListener);
        mScreenStreamingManager.setStreamingStateListener(mStreamingStateChangedListener);
        mScreenStreamingManager.setStreamStatusCallback(mStreamStatusCallback);
        mScreenStreamingManager.setNativeLoggingEnabled(false);
        mScreenStreamingManager.setNotification(NOTIFICATION_ID, createNotification());
        mScreenStreamingManager.prepare(this, screenSetting, null, mProfile);
    }

    /**
     * 某些特定推流事件的回调接口
     */
    private StreamingSessionListener mStreamingSessionListener = new StreamingSessionListener() {
        /**
         * 音频采集失败时回调此接口
         *
         * @param code 错误码
         * @return true 表示您已处理该事件，反之则表示未处理
         */
        @Override
        public boolean onRecordAudioFailedHandled(int code) {
            return false;
        }

        /**
         * 重连提示回调，当收到此回调时，您可以在这里进行重连的操作
         *
         * 当网络不可达时，首先会回调 StreamingState#DISCONNECTED 状态，当重连环境准备好时会回调此方法
         *
         * @param code 错误码
         * @return true 表示您已处理该事件，反之则表示未处理，未处理则会触发 StreamingState#SHUTDOWN 状态回调
         */
        @Override
        public boolean onRestartStreamingHandled(int code) {
            Log.i(TAG, "onRestartStreamingHandled");
            return false;
        }

        /**
         * 相机支持的采集分辨率回调，外部导入推流无需处理
         */
        @Override
        public Camera.Size onPreviewSizeSelected(List<Camera.Size> list) {
            return null;
        }

        /**
         * 相机支持的采集帧率列表，外部导入推流无需处理
         */
        @Override
        public int onPreviewFpsSelected(List<int[]> list) {
            return 0;
        }
    };

    /**
     * 码流信息回调，回调当前推流的音视频码率、帧率等信息
     *
     * 注意：回调在非 UI 线程，UI 操作需要做特殊处理！！！
     */
    private StreamStatusCallback mStreamStatusCallback = new StreamStatusCallback() {
        @Override
        public void notifyStreamStatusChanged(final StreamingProfile.StreamStatus streamStatus) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mStatView.setText("bitrate:" + streamStatus.totalAVBitrate / 1024 + " kbps"
                            + "\naudio:" + streamStatus.audioFps + " fps"
                            + "\nvideo:" + streamStatus.videoFps + " fps");
                }
            });
        }
    };

    /**
     * 推流状态改变时的回调
     */
    private StreamingStateChangedListener mStreamingStateChangedListener = new StreamingStateChangedListener() {
        @Override
        public void onStateChanged(StreamingState streamingState, Object extra) {
            Log.i(TAG, "StreamingState streamingState:" + streamingState + ",extra:" + extra);
            switch (streamingState) {
                case PREPARING:
                    mStatusMsgContent = getString(R.string.string_state_preparing);
                    break;
                case READY:
                    /**
                     * 注意：开启推流的操作需要在 READY 状态后！！！
                     */
                    mIsReady = true;
                    mStatusMsgContent = getString(R.string.string_state_ready);
                    break;
                case CONNECTING:
                    mStatusMsgContent = getString(R.string.string_state_connecting);
                    break;
                case STREAMING:
                    mStatusMsgContent = getString(R.string.string_state_streaming);
                    setShutterButtonEnabled(true);
                    setShutterButtonPressed(true);
                    break;
                case SHUTDOWN:
                    mStatusMsgContent = getString(R.string.string_state_ready);
                    setShutterButtonEnabled(true);
                    setShutterButtonPressed(false);
                    break;
                case IOERROR:
                    /**
                     * 在 `startStreaming` 时，如果网络不可用，则会回调此状态
                     * 您可以在适当延时后重新推流或者就此停止推流
                     */
                    mLogContent += "IOERROR\n";
                    mStatusMsgContent = getString(R.string.string_state_ready);
                    setShutterButtonEnabled(true);
                    startStreamingInternal(2000);
                    break;
                case DISCONNECTED:
                    /**
                     * 网络连接断开时触发，收到此回调后，您可以在 `onRestartStreamingHandled` 回调里处理重连逻辑
                     */
                    mLogContent += "DISCONNECTED\n";
                    break;
                case UNKNOWN:
                    mStatusMsgContent = getString(R.string.string_state_ready);
                    break;
                case SENDING_BUFFER_EMPTY:
                case SENDING_BUFFER_FULL:
                case AUDIO_RECORDING_FAIL:
                    break;
                case INVALID_STREAMING_URL:
                    Log.e(TAG, "Invalid streaming url:" + extra);
                    break;
                case UNAUTHORIZED_STREAMING_URL:
                    Log.e(TAG, "Unauthorized streaming url:" + extra);
                    mLogContent += "Unauthorized Url\n";
                    break;
                case UNAUTHORIZED_PACKAGE:
                    mLogContent += "Unauthorized package\n";
                    break;
                case REQUEST_SCREEN_CAPTURING_FAIL:
                    Toast.makeText(ScreenStreamingActivity.this, "Request screen capturing fail", Toast.LENGTH_LONG).show();
                    break;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mLogTextView != null) {
                        mLogTextView.setText(mLogContent);
                    }
                    mStatusTextView.setText(mStatusMsgContent);
                }
            });
        }
    };

    private Notification createNotification() {
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this.getApplicationContext(), "screenRecorder");
        } else {
            builder = new Notification.Builder(this.getApplicationContext());
        }
        Intent intent = new Intent(this, ScreenStreamingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_UPDATE_CURRENT);
        builder.setSmallIcon(R.drawable.qiniu_logo)
                .setContentTitle("七牛推流")
                .setContentText("正在录屏ing")
                .setContentIntent(pendingIntent)
                .setShowWhen(true)
                .setVisibility(VISIBILITY_PRIVATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("screenRecorder", "screenRecorder", NotificationManager.IMPORTANCE_HIGH);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        return builder.build();
    }

    private Runnable mUpdateTimeTvRunnable = new Runnable() {
        @Override
        public void run() {
            mTimeTv.setText(mDateFormat.format(new Date()));
            mHandler.postDelayed(mUpdateTimeTvRunnable, 100);
        }
    };

    /**
     * 在图片推流过程中切换图片，仅供 demo 演示，您可以根据产品定义自行实现
     */
    private class ImageSwitcher implements Runnable {
        @Override
        public void run() {
            if (!mIsPictureStreaming) {
                Log.d(TAG, "is not picture streaming!!!");
                return;
            }

            if (mTimes % 2 == 0) {
                if (mEncodingConfig.mPictureStreamingFilePath != null) {
                    mScreenStreamingManager.setPictureStreamingFilePath(mEncodingConfig.mPictureStreamingFilePath);
                } else {
                    mScreenStreamingManager.setPictureStreamingResourceId(R.drawable.qiniu_logo);
                }
            } else {
                mScreenStreamingManager.setPictureStreamingResourceId(R.drawable.pause_publish);
            }
            mTimes++;
            if (mSubThreadHandler != null) {
                mSubThreadHandler.postDelayed(this, 1000);
            }
        }
    }

    /**
     * 切换图片推流
     *
     * 注意：该场景下图片推流为耗时操作，需要放到子线程执行
     */
    private void togglePictureStreaming() {
        mSubThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                boolean isOK = mScreenStreamingManager.togglePictureStreaming();
                if (!isOK) {
                    Toast.makeText(ScreenStreamingActivity.this, "toggle picture streaming failed!", Toast.LENGTH_SHORT).show();
                    return;
                }

                mIsPictureStreaming = !mIsPictureStreaming;

                mTimes = 0;
                if (mIsPictureStreaming) {
                    if (mImageSwitcher == null) {
                        mImageSwitcher = new ImageSwitcher();
                    }

                    mSubThreadHandler.postDelayed(mImageSwitcher, 1000);
                }
            }
        });
    }

    /**
     * 开始推流
     * 注意：开始推流的操作一定要在 onStateChanged.READY 状态回调后执行！！！
     */
    private void startStreamingInternal() {
        startStreamingInternal(0);
    }

    private void startStreamingInternal(long delayMillis) {
        if (mScreenStreamingManager == null) {
            return;
        }
        setShutterButtonEnabled(false);
        // startStreaming 为耗时操作，建议放到子线程执行
        if (mSubThreadHandler != null) {
            mSubThreadHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    final boolean res = mScreenStreamingManager.startStreaming();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setShutterButtonPressed(res);
                            if (!res) {
                                setShutterButtonEnabled(true);
                            }
                        }
                    });
                }
            }, delayMillis);
        }
    }

    /**
     * 停止推流
     */
    private void stopStreamingInternal() {
        if (mShutterButtonPressed && mSubThreadHandler != null) {
            // disable the shutter button before stopStreaming
            setShutterButtonEnabled(false);
            mSubThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    final boolean res = mScreenStreamingManager.stopStreaming();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!res) {
                                mShutterButtonPressed = true;
                                setShutterButtonEnabled(true);
                            }
                            setShutterButtonPressed(mShutterButtonPressed);
                        }
                    });
                }
            });
        }
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

    private void setShutterButtonPressed(final boolean pressed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mShutterButtonPressed = pressed;
                mShutterButton.setPressed(pressed);
            }
        });
    }
}
