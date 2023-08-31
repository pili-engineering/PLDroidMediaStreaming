package com.qiniu.pili.droid.streaming.demo.activity;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.qiniu.pili.droid.streaming.AVCodecType;
import com.qiniu.pili.droid.streaming.PLVideoEncodeType;
import com.qiniu.pili.droid.streaming.StreamStatusCallback;
import com.qiniu.pili.droid.streaming.StreamingManager;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.StreamingSessionListener;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;
import com.qiniu.pili.droid.streaming.demo.plain.EncodingConfig;
import com.qiniu.pili.droid.streaming.demo.utils.Config;
import com.qiniu.pili.droid.streaming.demo.R;
import com.qiniu.pili.droid.streaming.demo.core.ExtAudioCapture;
import com.qiniu.pili.droid.streaming.demo.core.ExtVideoCapture;
import com.qiniu.pili.droid.streaming.demo.utils.ToastUtils;
import com.qiniu.pili.droid.streaming.demo.utils.Util;

import java.net.URISyntaxException;
import java.util.List;

public class ImportStreamingActivity extends Activity {
    private static final String TAG = "ImportStreamingActivity";

    private SurfaceView mSurfaceView;
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

    private ExtAudioCapture mExtAudioCapture;
    private ExtVideoCapture mExtVideoCapture;

    private EncodingConfig mEncodingConfig;

    private StreamingManager mStreamingManager;
    private StreamingProfile mProfile;

    // 用于处理子线程操作
    private Handler mSubThreadHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 获取推流编码配置信息
        mEncodingConfig = (EncodingConfig) getIntent().getSerializableExtra(Config.NAME_ENCODING_CONFIG);

        Intent intent = getIntent();
        mPublishUrl = intent.getStringExtra(Config.PUBLISH_URL);
        mIsQuicEnabled = intent.getBooleanExtra(Config.TRANSFER_MODE_QUIC, false);
        mIsSrtEnabled = intent.getBooleanExtra(Config.TRANSFER_MODE_SRT, false);

        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mSubThreadHandler = new Handler(handlerThread.getLooper());

        // 初始化视图控件
        initView();
        // 初始化外部采集对象实例
        initExtCapture();
        // 初始化 StreamingProfile，StreamingProfile 为推流相关的配置类，详情可参考 https://developer.qiniu.com/pili/sdk/3719/PLDroidMediaStreaming-function-using#4
        initEncodingProfile();
        // 初始化 MediaStreamingManager，使用姿势可参考 https://developer.qiniu.com/pili/sdk/3719/PLDroidMediaStreaming-function-using#6
        initStreamingManager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mExtAudioCapture.startCapture();
        mExtAudioCapture.setOnAudioFrameCapturedListener(mOnAudioFrameCapturedListener);
        mStreamingManager.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mExtAudioCapture.stopCapture();
        mStreamingManager.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubThreadHandler != null) {
            mSubThreadHandler.getLooper().quit();
        }
        // 销毁推流 Manager 的资源
        mStreamingManager.destroy();
    }

    /**
     * 初始化外部导入推流 demo 相关的视图控件
     */
    public void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(Config.SCREEN_ORIENTATION);
        setContentView(R.layout.activity_import_streaming);
        mSurfaceView = (SurfaceView) findViewById(R.id.ext_camerapreview_surfaceview);
        mLogTextView = (TextView) findViewById(R.id.log_info);
        mStatusTextView = (TextView) findViewById(R.id.streamingStatus);
        mStatView = (TextView) findViewById(R.id.stream_status);
        mShutterButton = (Button) findViewById(R.id.toggleRecording_button);

        mShutterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIsReady) {
                    Toast.makeText(ImportStreamingActivity.this, "需要在 READY 状态后才可以开始推流！！！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mShutterButtonPressed) {
                    stopStreamingInternal();
                } else {
                    startStreamingInternal();
                }
            }
        });
    }

    /**
     * 初始化外部采集实例
     */
    private void initExtCapture() {
        mExtVideoCapture = new ExtVideoCapture(mSurfaceView);
        mExtVideoCapture.setOnPreviewFrameCallback(mOnPreviewFrameCallback);
        mExtAudioCapture = new ExtAudioCapture();
        mExtAudioCapture.setOnAudioFrameCapturedListener(mOnAudioFrameCapturedListener);
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
        // 设置视频编码格式（H.264/H.265）
        mProfile.setVideoEncodeType(mEncodingConfig.mVideoEncodeType);

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

        // 其他配置项
        mProfile.setDnsManager(Util.getMyDnsManager())
                .setStreamStatusConfig(new StreamingProfile.StreamStatusConfig(3))
                .setSendingBufferProfile(new StreamingProfile.SendingBufferProfile(0.2f, 0.8f, 3.0f, 20 * 1000));
    }

    /**
     * 初始化推流管理类
     */
    private void initStreamingManager() {
        mStreamingManager = new StreamingManager(this, AVCodecType.HW_VIDEO_YUV_AS_INPUT_WITH_HW_AUDIO_CODEC);
        mStreamingManager.prepare(mProfile);
        mStreamingManager.setStreamingSessionListener(mStreamingSessionListener);
        mStreamingManager.setStreamStatusCallback(mStreamStatusCallback);
        mStreamingManager.setStreamingStateListener(mStreamingStateChangedListener);
    }

    private ExtVideoCapture.OnPreviewFrameCallback mOnPreviewFrameCallback = new ExtVideoCapture.OnPreviewFrameCallback() {
        @Override
        public void onPreviewFrameCaptured(byte[] data, int width, int height, int orientation, boolean mirror, int fmt, long tsInNanoTime) {
            mStreamingManager.inputVideoFrame(data, width, height, orientation, false, fmt, tsInNanoTime);
        }
    };

    private ExtAudioCapture.OnAudioFrameCapturedListener mOnAudioFrameCapturedListener = new ExtAudioCapture.OnAudioFrameCapturedListener() {
        @Override
        public void onAudioFrameCaptured(byte[] audioData) {
            long timestamp = System.nanoTime();
            mStreamingManager.inputAudioFrame(audioData, timestamp, false);
        }
    };

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
                            + "\nvideo:" + streamStatus.videoFps + " fps"
                            + "\ndropped:" + streamStatus.droppedVideoFrames);
                }
            });
        }
    };

    /**
     * 推流状态改变时的回调
     */
    private StreamingStateChangedListener mStreamingStateChangedListener = new StreamingStateChangedListener() {
        @Override
        public void onStateChanged(StreamingState streamingState, final Object extra) {
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
                case VIDEO_ENCODER_READY:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.s(getApplicationContext(),
                                    "编码器初始化完成：" + ((PLVideoEncodeType) extra).name());
                        }
                    });
                    break;
                case START_VIDEO_ENCODER_FAIL:
                case VIDEO_ENCODER_ERROR:
                case START_AUDIO_ENCODER_FAIL:
                case AUDIO_ENCODER_ERROR:
                    /**
                     * 当回调 START_VIDEO_ENCODER_FAIL、VIDEO_ENCODER_ERROR、START_AUDIO_ENCODER_FAIL、
                     * AUDIO_ENCODER_ERROR 等状态时，代表编码器出现异常，推流已停止，相应资源也已释放，
                     * 如果需要，可以基于报错的编码器进行重新配置，更新 {@link AVCodecType} 之后，重新开启推流
                     */
                    mStatusMsgContent = getString(R.string.string_state_ready);
                    setShutterButtonEnabled(true);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ImportStreamingActivity.this, "编码器错误！！！", Toast.LENGTH_SHORT).show();
                        }
                    });
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

    /**
     * 开始推流
     * 注意：开始推流的操作一定要在 onStateChanged.READY 状态回调后执行！！！
     */
    private void startStreamingInternal() {
        startStreamingInternal(0);
    }

    private void startStreamingInternal(long delayMillis) {
        if (mStreamingManager == null) {
            return;
        }
        setShutterButtonEnabled(false);
        // startStreaming 为耗时操作，建议放到子线程执行
        if (mSubThreadHandler != null) {
            mSubThreadHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    final boolean res = mStreamingManager.startStreaming();
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
                    final boolean res = mStreamingManager.stopStreaming();
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
