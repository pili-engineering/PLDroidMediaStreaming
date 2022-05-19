package com.qiniu.pili.droid.streaming.demo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.qiniu.pili.droid.streaming.AVCodecType;
import com.qiniu.pili.droid.streaming.AudioSourceCallback;
import com.qiniu.pili.droid.streaming.CameraStreamingSetting;
import com.qiniu.pili.droid.streaming.FrameCapturedCallback;
import com.qiniu.pili.droid.streaming.MediaStreamingManager;
import com.qiniu.pili.droid.streaming.MicrophoneStreamingSetting;
import com.qiniu.pili.droid.streaming.StreamStatusCallback;
import com.qiniu.pili.droid.streaming.StreamingPreviewCallback;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.StreamingSessionListener;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;
import com.qiniu.pili.droid.streaming.SurfaceTextureCallback;
import com.qiniu.pili.droid.streaming.WatermarkSetting;
import com.qiniu.pili.droid.streaming.av.common.PLFourCC;
import com.qiniu.pili.droid.streaming.demo.R;
import com.qiniu.pili.droid.streaming.demo.fragment.ControlFragment;
import com.qiniu.pili.droid.streaming.demo.gles.FBO;
import com.qiniu.pili.droid.streaming.demo.plain.CameraConfig;
import com.qiniu.pili.droid.streaming.demo.plain.EncodingConfig;
import com.qiniu.pili.droid.streaming.demo.ui.CameraPreviewFrameView;
import com.qiniu.pili.droid.streaming.demo.ui.RotateLayout;
import com.qiniu.pili.droid.streaming.demo.utils.Cache;
import com.qiniu.pili.droid.streaming.demo.utils.Config;
import com.qiniu.pili.droid.streaming.demo.utils.Util;
import com.qiniu.pili.droid.streaming.microphone.AudioMixer;
import com.qiniu.pili.droid.streaming.microphone.OnAudioMixListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.List;

public class AVStreamingActivity extends Activity implements
        CameraPreviewFrameView.Listener,
        ControlFragment.OnEventClickedListener {
    private static final String TAG = "AVStreamingActivity";

    private RotateLayout mRotateLayout;

    private String mStatusMsgContent;
    private String mLogContent = "\n";

    private boolean mIsReady;
    private boolean mIsPreviewMirror = false;
    private boolean mIsEncodingMirror = false;
    private boolean mIsPlayingback = false;
    private boolean mIsStreaming = false;

    private volatile boolean mIsSupportTorch = false;

    private int mCurrentZoom = 0;
    private int mMaxZoom = 0;
    private boolean mOrientationChanged = false;
    private int mCurrentCamFacingIndex;
    private int mFrameWidth = 100;
    private int mFrameHeight = 100;

    private ControlFragment mControlFragment;

    // 用作演示自定义美颜实现逻辑
    private FBO mFBO = new FBO();

    protected EncodingConfig mEncodingConfig;
    private CameraConfig mCameraConfig;

    private String mPublishUrl;
    private boolean mIsQuicEnabled;
    private boolean mIsSrtEnabled;
    private String mPicStreamingFilePath;

    // 推流操作管理类实例
    private MediaStreamingManager mMediaStreamingManager;
    // 推流编码配置类实例
    private StreamingProfile mProfile;
    // 推流采集配置类实例
    private CameraStreamingSetting mCameraStreamingSetting;
    // 推流水印配置类实例
    private WatermarkSetting mWatermarkSetting;
    // 推流混音管理类实例
    private AudioMixer mAudioMixer;
    private String mAudioFile;

    // 用于处理子线程操作
    private Handler mSubThreadHandler;

    // 用于处理图片推流延时切换图片
    private Handler mMainThreadHandler;
    private ImageSwitcher mImageSwitcher;
    private int mTimes = 0;
    private boolean mIsPictureStreaming = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取推流编码配置信息
        mEncodingConfig = (EncodingConfig) getIntent().getSerializableExtra(Config.NAME_ENCODING_CONFIG);
        // 获取相机采集配置信息
        mCameraConfig = (CameraConfig) getIntent().getSerializableExtra(Config.NAME_CAMERA_CONFIG);

        Intent intent = getIntent();
        mPublishUrl = intent.getStringExtra(Config.PUBLISH_URL);
        mIsQuicEnabled = intent.getBooleanExtra(Config.TRANSFER_MODE_QUIC, false);
        mIsSrtEnabled = intent.getBooleanExtra(Config.TRANSFER_MODE_SRT, false);

        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mSubThreadHandler = new Handler(handlerThread.getLooper());

        // 初始化视图控件
        initView();
        // 初始化 CameraStreamingSetting，CameraStreamingSetting 相关配置可参考 https://developer.qiniu.com/pili/sdk/3719/PLDroidMediaStreaming-function-using#2
        initCameraStreamingSetting();
        // 初始化 WatermarkSetting，WatermarkSetting 相关配置可参考 https://developer.qiniu.com/pili/sdk/3719/PLDroidMediaStreaming-function-using#5
        initWatermarkSetting();
        // 初始化 StreamingProfile，StreamingProfile 为推流相关的配置类，详情可参考 https://developer.qiniu.com/pili/sdk/3719/PLDroidMediaStreaming-function-using#4
        initEncodingProfile();
        // 初始化 MediaStreamingManager，使用姿势可参考 https://developer.qiniu.com/pili/sdk/3719/PLDroidMediaStreaming-function-using#6
        initStreamingManager();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 如果开启了图片推流，demo 当前的逻辑是退后台时不会停止推流，也无需打开摄像头，所以无需重新 resume
        if (!mIsPictureStreaming) {
            // 打开摄像头
            mMediaStreamingManager.resume();
        } else {
            Toast.makeText(this, "当前正在图片推流！！！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 如果当前正在图片推流，则退后台不会终止推流
        if (isFinishing() || !mIsPictureStreaming) {
            mIsReady = false;
            if (mIsStreaming) {
                Toast.makeText(this, "推流已停止！！！", Toast.LENGTH_SHORT).show();
            }
            mMediaStreamingManager.pause();
        } else {
            Toast.makeText(this, "当前正在图片推流！！！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubThreadHandler != null) {
            mSubThreadHandler.getLooper().quit();
            mSubThreadHandler = null;
        }
        // 销毁推流 Manager 的资源
        mMediaStreamingManager.destroy();
    }

    /**
     * 初始化推流 demo 相关的视图控件以及 ControlFragment
     */
    private void initView() {
        mIsPreviewMirror = mCameraConfig.mPreviewMirror;
        mIsEncodingMirror = mCameraConfig.mEncodingMirror;
        mCurrentCamFacingIndex = mCameraConfig.mFrontFacing ? 1 : 0;
        boolean isNeedFB = mCameraConfig.mIsFaceBeautyEnabled;
        boolean isEncOrientationPort = mEncodingConfig.mVideoOrientationPortrait;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(isEncOrientationPort ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_av_streaming);

        // 初始化推流控制面板
        mControlFragment = new ControlFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ControlFragment.KEY_BEAUTY_ON, isNeedFB);
        bundle.putBoolean(ControlFragment.KEY_ENCODE_ORIENTATION, isEncOrientationPort);
        bundle.putBoolean(ControlFragment.KEY_HW_VIDEO_ENCODE_TYPE, isHwVideoEncodeType());
        mControlFragment.setArguments(bundle);
        mControlFragment.setOnEventClickedListener(this);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.control_fragment_container, mControlFragment);
        ft.commit();
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
            // 当图像尺寸和编码尺寸不一样时，设置 SDK 缩放算法。该配置仅作用于软编和 YUV 硬编。缩放速度由快到慢，图像质量由低到高的顺序为：None、Linear、Bilinear、Box
            mProfile.setYuvFilterMode(mEncodingConfig.mYuvFilterMode);
            // 设置码率调整模式，如果开启自适应码率，则需指定自适应码率的上下限（当前仅支持 150kbps ~ 2000kbps 区间内的设置，设置区间外的数据不会生效）。
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
                mPicStreamingFilePath = mEncodingConfig.mPictureStreamingFilePath;
                mProfile.setPictureStreamingFilePath(mPicStreamingFilePath);
            }
        }

        // 其他配置项
        mProfile.setDnsManager(Util.getMyDnsManager())
                .setStreamStatusConfig(new StreamingProfile.StreamStatusConfig(3))
                .setSendingBufferProfile(new StreamingProfile.SendingBufferProfile(0.2f, 0.8f, 3.0f, 20 * 1000));
    }

    /**
     * 初始化相机采集配置
     */
    private void initCameraStreamingSetting() {
        mCameraStreamingSetting = new CameraStreamingSetting();
        mCameraStreamingSetting.setCameraId(mCameraConfig.mFrontFacing ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK)
                .setCameraPrvSizeLevel(mCameraConfig.mSizeLevel)
                .setCameraPrvSizeRatio(mCameraConfig.mSizeRatio)
                .setFocusMode(mCameraConfig.mFocusMode)
                .setContinuousFocusModeEnabled(mCameraConfig.mContinuousAutoFocus)
                .setFrontCameraPreviewMirror(mCameraConfig.mPreviewMirror)
                .setFrontCameraMirror(mCameraConfig.mEncodingMirror).setRecordingHint(false)
                .setResetTouchFocusDelayInMs(3000)
                .setBuiltInFaceBeautyEnabled(!mCameraConfig.mIsCustomFaceBeauty)
                .setFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(0.6f, 0.6f, 0.6f));

        if (mCameraConfig.mIsFaceBeautyEnabled) {
            mCameraStreamingSetting.setVideoFilter(CameraStreamingSetting.VIDEO_FILTER_TYPE.VIDEO_FILTER_BEAUTY);
        } else {
            mCameraStreamingSetting.setVideoFilter(CameraStreamingSetting.VIDEO_FILTER_TYPE.VIDEO_FILTER_NONE);
        }
    }

    /**
     * 仅支持 32 位 png (ARGB)
     */
    private void initWatermarkSetting() {
        if (!mEncodingConfig.mIsWatermarkEnabled) {
            return;
        }
        mWatermarkSetting = new WatermarkSetting(this);
        mWatermarkSetting.setResourceId(R.drawable.qiniu_logo);
        mWatermarkSetting.setAlpha(mEncodingConfig.mWatermarkAlpha);
        mWatermarkSetting.setSize(mEncodingConfig.mWatermarkSize);
        if (mEncodingConfig.mWatermarkCustomWidth != 0 || mEncodingConfig.mWatermarkCustomHeight != 0) {
            mWatermarkSetting.setCustomSize(mEncodingConfig.mWatermarkCustomWidth, mEncodingConfig.mWatermarkCustomHeight);
        }
        if (mEncodingConfig.mIsWatermarkLocationPreset) {
            mWatermarkSetting.setLocation(mEncodingConfig.mWatermarkLocationPreset);
        } else {
            mWatermarkSetting.setCustomPosition(mEncodingConfig.mWatermarkLocationCustomX, mEncodingConfig.mWatermarkLocationCustomY);
        }
    }

    /**
     * 初始化推流管理类
     */
    private void initStreamingManager() {
        CameraPreviewFrameView cameraPreviewFrameView = (CameraPreviewFrameView) findViewById(R.id.cameraPreview_surfaceView);
        mMediaStreamingManager = new MediaStreamingManager(this, cameraPreviewFrameView, mEncodingConfig.mCodecType);

        Intent intent = getIntent();
        boolean isAudioStereoEnabled = intent.getBooleanExtra(Config.AUDIO_CHANNEL_STEREO, false);
        boolean isVoIPRecordEnabled = intent.getBooleanExtra(Config.AUDIO_VOIP_RECORD, false);
        boolean isBluetoothScoEnabled = intent.getBooleanExtra(Config.AUDIO_SCO_ON, false);

        // 初始化 MicrophoneStreamingSetting
        MicrophoneStreamingSetting microphoneStreamingSetting = new MicrophoneStreamingSetting();
        if (isAudioStereoEnabled) {
            /**
             * 注意 !!! {@link AudioFormat#CHANNEL_IN_STEREO} 并不能保证在所有设备上都可以正常运行.
             */
            microphoneStreamingSetting.setChannelConfig(AudioFormat.CHANNEL_IN_STEREO);
        }
        if (isVoIPRecordEnabled) {
            microphoneStreamingSetting.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
        }
        microphoneStreamingSetting.setBluetoothSCOEnabled(isBluetoothScoEnabled);
        mMediaStreamingManager.prepare(mCameraStreamingSetting, microphoneStreamingSetting, mWatermarkSetting, mProfile);
        mMediaStreamingManager.setAutoRefreshOverlay(true);
        cameraPreviewFrameView.setListener(this);

        // 设置推流所需监听器
        mMediaStreamingManager.setStreamingSessionListener(mStreamingSessionListener);
        mMediaStreamingManager.setStreamStatusCallback(mStreamStatusCallback);
        mMediaStreamingManager.setAudioSourceCallback(mAudioSourceCallback);
        mMediaStreamingManager.setStreamingStateListener(mStreamingStateChangedListener);
        mMediaStreamingManager.setStreamingPreviewCallback(mStreamingPreviewCallback);
        if (mCameraConfig.mIsCustomFaceBeauty) {
            mMediaStreamingManager.setSurfaceTextureCallback(mSurfaceTextureCallback);
        }
        mMediaStreamingManager.setCameraErrorCallback(new Camera.ErrorCallback() {
            @Override
            public void onError(int errorCode, Camera camera) {
                // 摄像头被其他应用抢占时触发
                if (errorCode == Camera.CAMERA_ERROR_EVICTED) {
                    Log.e(TAG, "camera was evicted by the other app!");
                }
            }
        });

        mAudioMixer = mMediaStreamingManager.getAudioMixer();
        mAudioMixer.setOnAudioMixListener(new OnAudioMixListener() {
            @Override
            public void onStatusChanged(MixStatus mixStatus) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AVStreamingActivity.this, "mix finished", Toast.LENGTH_LONG).show();
                        updateMixBtnText();
                    }
                });
            }

            @Override
            public void onProgress(long progress, long duration) {
                if (mControlFragment != null) {
                    mControlFragment.updateAudioMixProgress(progress, duration);
                }
            }
        });
        mAudioFile = Cache.getAudioFile(this);
        if (mAudioFile != null) {
            try {
                mAudioMixer.setFile(mAudioFile, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 点击静音按钮触发，用于控制推流静音效果
     *
     * @param isMute 是否静音
     */
    @Override
    public void onMuteClicked(boolean isMute) {
        if (mMediaStreamingManager != null) {
            // 静音或者取消静音
            mMediaStreamingManager.mute(isMute);
        }
    }

    /**
     * 点击推流按钮触发，用于控制是否推流
     * 注意：开始推流的操作要在收到 OnStateChanged.READY 状态之后执行！！！
     *
     * @param isNeedStart 是否需要推流
     * @return 推流操作是否成功执行，是否推流成功需要依赖 OnStateChanged.STREAMING 状态
     */
    @Override
    public void onStreamingStartClicked(boolean isNeedStart) {
        if (isNeedStart) {
            // 开始推流
            startStreamingInternal();
        } else {
            // 停止推流
            stopStreamingInternal();
        }
    }

    /**
     * 点击预览镜像按钮触发，用于控制预览镜像效果
     */
    @Override
    public void onPreviewMirrorClicked() {
        if (isPictureStreaming()) {
            return;
        }
        if (mMediaStreamingManager != null) {
            // 设置预览镜像（仅对预览生效）
            mIsPreviewMirror = !mIsPreviewMirror;
            boolean res = mMediaStreamingManager.setPreviewMirror(mIsPreviewMirror);
            if (res) {
                Toast.makeText(AVStreamingActivity.this, "镜像成功", Toast.LENGTH_SHORT).show();
            } else {
                mIsPreviewMirror = !mIsPreviewMirror;
            }
        }
    }

    /**
     * 点击编码镜像按钮触发，用于控制编码镜像效果
     */
    @Override
    public void onEncodingMirrorClicked() {
        if (isPictureStreaming()) {
            return;
        }
        if (mMediaStreamingManager != null) {
            // 设置编码镜像（仅对编码生效，预览无影响）
            mIsEncodingMirror = !mIsEncodingMirror;
            boolean res = mMediaStreamingManager.setEncodingMirror(mIsEncodingMirror);
            if (res) {
                Toast.makeText(AVStreamingActivity.this, "镜像成功", Toast.LENGTH_SHORT).show();
            } else {
                mIsEncodingMirror = !mIsEncodingMirror;
            }
        }
    }

    /**
     * 点击闪光灯控制按钮触发，用于操控设备闪光灯的开关
     *
     * @param isTorchOn 打开/关闭
     * @return 是否操作成功
     */
    @Override
    public boolean onTorchClicked(boolean isTorchOn) {
        if (!mIsSupportTorch) {
            Toast.makeText(AVStreamingActivity.this, "当前摄像头不支持闪光灯！", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mMediaStreamingManager != null) {
            if (isTorchOn) {
                // 开启闪光灯
                return mMediaStreamingManager.turnLightOn();
            } else {
                // 关闭闪光灯
                return mMediaStreamingManager.turnLightOff();
            }
        }
        return false;
    }

    /**
     * 点击切换摄像头按钮触发，用于切换摄像头
     */
    @Override
    public void onCameraSwitchClicked() {
        if (isPictureStreaming()) {
            return;
        }
        mCurrentCamFacingIndex = (mCurrentCamFacingIndex + 1) % CameraStreamingSetting.getNumberOfCameras();
        CameraStreamingSetting.CAMERA_FACING_ID facingId;
        if (mCurrentCamFacingIndex == CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_BACK.ordinal()) {
            facingId = CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_BACK;
        } else if (mCurrentCamFacingIndex == CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT.ordinal()) {
            facingId = CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT;
        } else {
            facingId = CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_3RD;
        }
        Log.i(TAG, "switchCamera:" + facingId);
        // 切换到指定摄像头
        mMediaStreamingManager.switchCamera(facingId);

        mIsEncodingMirror = mCameraConfig.mEncodingMirror;
        mIsPreviewMirror = facingId == CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT && mCameraConfig.mPreviewMirror;
    }

    /**
     * 移动混音进度条时触发，用于 seek 混音的进度
     *
     * @param position seek 的位置
     */
    @Override
    public void onAudioMixPositionChanged(float position) {
        mAudioMixer.seek(position);
    }

    /**
     * 移动混音音量进度条时触发，用于调整混音音乐的音量
     *
     * @param volume 目标音量
     */
    @Override
    public void onAudioMixVolumeChanged(float volume) {
        mAudioMixer.setVolume(1.0f, volume);
    }

    /**
     * 点击混音文件选择按钮时触发，用于处理混音文件的选择
     */
    @Override
    public void onAudioMixFileSelectionClicked() {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.STORAGE_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = new String[]{"mp3"};

        FilePickerDialog dialog = new FilePickerDialog(AVStreamingActivity.this, properties);
        dialog.setTitle("Select a File");
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                if (files == null || files.length == 0) {
                    Toast.makeText(AVStreamingActivity.this, "Choose an audio please!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String filePath = files[0];
                try {
                    mAudioMixer.setFile(filePath, true);
                    Cache.setAudioFile(AVStreamingActivity.this, filePath);
                    Toast.makeText(AVStreamingActivity.this, "setup mix file " + filePath + " success. duration:" + mAudioMixer.getDuration(), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(AVStreamingActivity.this, "setup mix file " + filePath + " failed !!!", Toast.LENGTH_LONG).show();
                }
            }
        });
        dialog.show();
    }

    /**
     * 点击混音 Play/Pause 按钮时触发，用于控制混音的开始或暂停
     */
    @Override
    public void onAudioMixControllerClicked() {
        if (mAudioMixer != null) {
            String text;
            if (mAudioMixer.isRunning()) {
                boolean s = mAudioMixer.pause();
                text = s ? "mixing pause success" : "mixing pause failed !!!";
            } else {
                boolean s = mAudioMixer.play();
                text = s ? "mixing play success" : "mixing play failed !!!";
            }
            Toast.makeText(AVStreamingActivity.this, text, Toast.LENGTH_LONG).show();

            updateMixBtnText();
        }
    }

    /**
     * 点击混音 Stop 按钮时触发，用于停止混音操作，停止后再次开始将从音频文件最开始处执行混音操作
     */
    @Override
    public void onAudioMixStopClicked() {
        if (mAudioMixer != null) {
            boolean stopSuccess = mAudioMixer.stop();
            String text = stopSuccess ? "mixing stop success" : "mixing stop failed !!!";
            Toast.makeText(AVStreamingActivity.this, text, Toast.LENGTH_LONG).show();
            if (stopSuccess) {
                updateMixBtnText();
            }
        }
    }

    /**
     * 点击混音返听按钮时触发，用于控制混音的返听功能
     */
    @Override
    public void onAudioMixPlaybackClicked() {
        if (mIsPlayingback) {
            mMediaStreamingManager.stopPlayback();
        } else {
            mMediaStreamingManager.startPlayback();
        }
        mIsPlayingback = !mIsPlayingback;
    }

    /**
     * 点击图片推流按钮时触发，用于控制图片推流
     */
    @Override
    public void onPictureStreamingClicked() {
        mProfile.setPictureStreamingFps(10);

        // 切换图片推流模式，如果当前已经是图片推流，则调用该接口会切换回正常音视频推流
        boolean isOK = mMediaStreamingManager.togglePictureStreaming();
        if (!isOK) {
            Toast.makeText(AVStreamingActivity.this, "toggle picture streaming failed!", Toast.LENGTH_SHORT).show();
            return;
        }

        mIsPictureStreaming = !mIsPictureStreaming;
        if (mIsPictureStreaming) {
            if (mPicStreamingFilePath == null) {
                mControlFragment.setPicStreamingImage(R.drawable.pause_publish);
            } else {
                mControlFragment.setPicStreamingImage(mPicStreamingFilePath);
            }
            mControlFragment.setPictureImageVisible(true);
        } else {
            mControlFragment.setPictureImageVisible(false);
        }

        // 模拟推流图片的切换，仅供 demo 演示用
        mTimes = 0;
        if (mIsPictureStreaming) {
            if (mImageSwitcher == null) {
                mImageSwitcher = new ImageSwitcher();
            }

            mMainThreadHandler = new Handler(getMainLooper());
            mMainThreadHandler.postDelayed(mImageSwitcher, 2000);
        }
    }

    /**
     * 点击截图按钮时触发，用于截帧的操作
     */
    @Override
    public void onCaptureFrameClicked() {
        if (isPictureStreaming()) {
            return;
        }
        final String fileName = "PLStreaming_" + System.currentTimeMillis() + ".jpg";
        // 截帧，会保存当前预览的画面，并通过回调将保存的 Bitmap 回调上来
        mMediaStreamingManager.captureFrame(mFrameWidth, mFrameHeight, new FrameCapturedCallback() {
            private Bitmap bitmap;
            @Override
            public void onFrameCaptured(Bitmap bmp) {
                if (bmp == null) {
                    return;
                }
                bitmap = bmp;
                // 将 bitmap 保存到本地
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            saveToSDCard(fileName, bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (bitmap != null) {
                                bitmap.recycle();
                                bitmap = null;
                            }
                        }
                    }
                }).start();
            }
        });
    }

    /**
     * 点击改变推流 Orientation 的按钮时触发，用于切换推流的横竖屏
     *
     * @param isPortrait 是否是竖屏
     * @return 是否操作成功
     */
    @Override
    public boolean onOrientationChanged(boolean isPortrait) {
        if (isPictureStreaming()) {
            return false;
        }
        Log.i(TAG, "isPortrait : " + isPortrait);
        if (mIsStreaming) {
            mOrientationChanged = true;
        }
        mProfile.setEncodingOrientation(isPortrait ? StreamingProfile.ENCODING_ORIENTATION.PORT : StreamingProfile.ENCODING_ORIENTATION.LAND);

        // 更新 StreamingProfile 的时候，需要重新推流才可以生效!!!
        mMediaStreamingManager.setStreamingProfile(mProfile);
        stopStreamingInternal();

        setRequestedOrientation(isPortrait ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mMediaStreamingManager.notifyActivityOrientationChanged();
        Toast.makeText(AVStreamingActivity.this, Config.HINT_ENCODING_ORIENTATION_CHANGED,
                Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onOrientationChanged -");
        return true;
    }

    /**
     * 点击美颜按钮时触发，用于控制是否开启美颜
     *
     * @param isBeautyOn 是否开启美颜
     */
    @Override
    public void onFaceBeautyClicked(boolean isBeautyOn) {
        mMediaStreamingManager.setVideoFilterType(isBeautyOn
                ? CameraStreamingSetting.VIDEO_FILTER_TYPE.VIDEO_FILTER_BEAUTY
                : CameraStreamingSetting.VIDEO_FILTER_TYPE.VIDEO_FILTER_NONE);
    }

    /**
     * 更新美颜参数
     *
     * @param progress 美颜级别
     */
    @Override
    public void onFaceBeautyProgressChanged(int progress) {
        if (mCameraStreamingSetting == null) {
            return;
        }
        CameraStreamingSetting.FaceBeautySetting fbSetting = mCameraStreamingSetting.getFaceBeautySetting();
        fbSetting.beautyLevel = progress / 100.0f;
        fbSetting.whiten = progress / 100.0f;
        fbSetting.redden = progress / 100.0f;

        mMediaStreamingManager.updateFaceBeautySetting(fbSetting);
    }

    /**
     * 点击添加贴图按钮时触发，用于添加图片贴纸
     */
    @Override
    public void onAddOverlayClicked() {
        ImageView imageOverlay = new ImageView(AVStreamingActivity.this);
        imageOverlay.setImageResource(R.drawable.qiniu_logo);
        imageOverlay.setOnTouchListener(new ViewTouchListener(imageOverlay));
        ((FrameLayout) findViewById(R.id.content)).addView(imageOverlay, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));

        mMediaStreamingManager.addOverlay(imageOverlay);
        Toast.makeText(AVStreamingActivity.this, "双击删除贴图!", Toast.LENGTH_LONG).show();
    }

    /**
     * 点击发送 SEI 按钮时触发
     *
     * 注意：发送的信息需要在播放端解码查看，因此，该功能需要搭配支持解码 SEI 的播放器使用才可验证
     */
    @Override
    public void onSendSEIClicked() {
        showSEIDialog();
    }

    /**
     * 点击预览窗口时触发，用于手动对焦
     *
     * @param e 手势事件
     * @return 是否拦截并处理事件
     */
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.i(TAG, "onSingleTapUp X:" + e.getX() + ",Y:" + e.getY());
        if (mIsReady) {
            setFocusAreaIndicator();
            mMediaStreamingManager.doSingleTapUp((int) e.getX(), (int) e.getY());
            return true;
        }
        return false;
    }

    /**
     * 手势缩放时触发，用于进行画面的缩放
     *
     * @param factor 缩放因子
     * @return
     */
    @Override
    public boolean onZoomValueChanged(float factor) {
        if (mIsReady && mMediaStreamingManager.isZoomSupported()) {
            mCurrentZoom = (int) (mMaxZoom * factor);
            mCurrentZoom = Math.min(mCurrentZoom, mMaxZoom);
            mCurrentZoom = Math.max(0, mCurrentZoom);
            Log.d(TAG, "zoom ongoing, scale: " + mCurrentZoom + ",factor:" + factor + ",maxZoom:" + mMaxZoom);
            // 设置缩放值
            mMediaStreamingManager.setZoomValue(mCurrentZoom);
        }
        return false;
    }

    /**
     * 推流状态改变时的回调
     */
    private StreamingStateChangedListener mStreamingStateChangedListener = new StreamingStateChangedListener() {
        @Override
        public void onStateChanged(StreamingState streamingState, Object extra) {
            Log.i(TAG, "onStateChanged : " + streamingState.name());
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
                    mMaxZoom = mMediaStreamingManager.getMaxZoom();
                    break;
                case CONNECTING:
                    mStatusMsgContent = getString(R.string.string_state_connecting);
                    break;
                case STREAMING:
                    mStatusMsgContent = getString(R.string.string_state_streaming);
                    mIsStreaming = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mControlFragment.setShutterButtonPressed(true);
                        }
                    });
                    break;
                case SHUTDOWN:
                    if (mOrientationChanged && mIsStreaming) {
                        mOrientationChanged = false;
                        startStreamingInternal();
                    }
                    mIsStreaming = false;
                    mStatusMsgContent = getString(R.string.string_state_ready);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mControlFragment.setShutterButtonPressed(false);
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
                    mIsStreaming = false;
                    mStatusMsgContent = getString(R.string.string_state_ready);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mControlFragment.setShutterButtonPressed(false);
                            Toast.makeText(AVStreamingActivity.this, "编码器错误！！！", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case IOERROR:
                    /**
                     * 在 `startStreaming` 时，如果网络不可用，则会回调此状态
                     * 您可以在适当延时后重新推流或者就此停止推流
                     */
                    mLogContent += "IOERROR\n";
                    mStatusMsgContent = getString(R.string.string_state_ready);
                    mIsStreaming = false;
                    startStreamingInternal(2000);
                    break;
                case DISCONNECTED:
                    /**
                     * 网络连接断开时触发，收到此回调后，您可以在 `onRestartStreamingHandled` 回调里处理重连逻辑
                     */
                    mLogContent += "DISCONNECTED\n";
                    break;
                case OPEN_CAMERA_FAIL:
                    Log.e(TAG, "Open Camera Fail. id:" + extra);
                    break;
                case CAMERA_SWITCHED:
                    if (extra != null) {
                        Log.i(TAG, "current camera id:" + (Integer) extra);
                    }
                    Log.i(TAG, "camera switched");
                    break;
                case TORCH_INFO:
                    if (extra != null) {
                        mIsSupportTorch = (Boolean) extra;
                        Log.i(TAG, "isSupportedTorch=" + mIsSupportTorch);
                    }
                    break;
                case UNKNOWN:
                    mStatusMsgContent = getString(R.string.string_state_ready);
                    break;
                case SENDING_BUFFER_EMPTY:
                case SENDING_BUFFER_FULL:
                case AUDIO_RECORDING_FAIL:
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
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mControlFragment != null) {
                        mControlFragment.updateLogText(mLogContent);
                        mControlFragment.setStatusText(mStatusMsgContent);
                    }
                }
            });
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
            startStreamingInternal(2000);
            return true;
        }

        /**
         * 相机支持的采集分辨率回调，以升序的方式回调 Camera 支持的分辨率列表，您可以选择期望的分辨率 size 并返回
         * 如果通过 CameraStreamingSetting.setCameraPrvSizeRatio 设置了期望的分辨率比例，则回调的 list 是经过 ratio 过滤的
         *
         * 建议您选择一个合适的采集分辨率以避免缩放
         * 例如: 如果推流编码分辨率为 1280 x 720, 您应该选择一个大于 1280 x 720 的采集分辨率
         *
         * @param list 支持的采集分辨率列表
         * @return 期望的采集分辨率
         */
        @Override
        public Camera.Size onPreviewSizeSelected(List<Camera.Size> list) {
            Camera.Size size = null;
            if (list != null) {
                StreamingProfile.VideoEncodingSize encodingSize = mProfile.getVideoEncodingSize(mCameraConfig.mSizeRatio);
                for (Camera.Size s : list) {
                    if (s.width >= encodingSize.width && s.height >= encodingSize.height) {
                        if (mEncodingConfig.mIsVideoSizePreset) {
                            size = s;
                            Log.d(TAG, "selected size :" + size.width + "x" + size.height);
                        }
                        break;
                    }
                }
            }
            return size;
        }

        /**
         * 相机支持的采集帧率列表
         * 注意：列表的元素是 Camera 支持的帧率范围数组，代表不同档位的帧率范围。数值是帧率乘以 1000 并以整数形式体现的。
         * 例如，如果帧率为 26.623 帧/秒，则回调出来的值为 26623。
         *
         * @param list 支持的采集帧率范围列表
         * @return 期望的采集帧率范围 index。【注意：如果期望 SDK 内部自行处理，则需返回 -1】
         */
        @Override
        public int onPreviewFpsSelected(List<int[]> list) {
            return -1;
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
                    mControlFragment.setStreamStatsText("bitrate:" + streamStatus.totalAVBitrate / 1024 + " kbps"
                            + "\naudio:" + streamStatus.audioFps + " fps"
                            + "\nvideo:" + streamStatus.videoFps + " fps");
                }
            });
        }
    };

    /**
     * 音频采集数据的回调，您可以在此回调中处理音频数据，如变声等。
     */
    private AudioSourceCallback mAudioSourceCallback = new AudioSourceCallback() {
        /**
         * 音频 buffer 回调
         *
         * @param srcBuffer 音频数据
         * @param size  音频数据的大小
         * @param tsInNanoTime 时间戳，单位：ns
         * @param isEof 是否是流结尾
         */
        @Override
        public void onAudioSourceAvailable(ByteBuffer srcBuffer, int size, long tsInNanoTime, boolean isEof) {

        }
    };

    /**
     * 采集数据的字节数组回调，回调数据为 NV21 的 buffer
     *
     * 软编模式下，接入自定义美颜需要注册此回调并处理 YUV 的数据
     */
    private StreamingPreviewCallback mStreamingPreviewCallback = new StreamingPreviewCallback() {
        @Override
        public boolean onPreviewFrame(byte[] bytes, int width, int height, int rotation, int fmt, long tsInNanoTime) {
            Log.d(TAG, "onPreviewFrame " + width + "x" + height + ",fmt:" + (fmt == PLFourCC.FOURCC_I420 ? "I420" : "NV21") + ",ts:" + tsInNanoTime + ",rotation:" + rotation);
            // 用于指定截帧的宽高，仅供 demo 演示
            if (rotation == 90 || rotation == 270) {
                mFrameWidth = height;
                mFrameHeight = width;
            } else {
                mFrameWidth = width;
                mFrameHeight = height;
            }
            /**
             * 软编场景下，使用自定义美颜算法时，需要在这个回调里改变 bytes 数组里的值
             * 例如: byte[] beauties = readPixelsFromGPU();
             * System.arraycopy(beauties, 0, bytes, 0, bytes.length);
             */
            return true;
        }
    };

    /**
     * 预览视频帧的纹理回调，您可以在这个回调里接入第三方美颜的处理
     *
     * 注意：
     * 1. 硬编模式下，接入第三方美颜处理，仅需要处理此回调即可
     * 2. 软编模式下，接入第三方美颜处理，此接口仅用于预览，编码部分还需要处理 StreamingPreviewCallback.onPreviewFrame 回调
     */
    private SurfaceTextureCallback mSurfaceTextureCallback = new SurfaceTextureCallback() {
        /**
         * Surface 创建时的回调，可用于执行第三方美颜的初始化等操作
         */
        @Override
        public void onSurfaceCreated() {
            Log.i(TAG, "onSurfaceCreated");
            mFBO.initialize(AVStreamingActivity.this);
        }

        /**
         * Surface 改变时的回调
         *
         * @param width 宽度
         * @param height 高度
         */
        @Override
        public void onSurfaceChanged(int width, int height) {
            Log.i(TAG, "onSurfaceChanged width:" + width + ",height:" + height);
            mFBO.updateSurfaceSize(width, height);
        }

        /**
         * Surface 销毁时的回调，可用于执行第三方美颜的销毁等操作
         */
        @Override
        public void onSurfaceDestroyed() {
            Log.i(TAG, "onSurfaceDestroyed");
            mFBO.release();
        }

        /**
         * 视频帧的回调，回调纹理数据，可以送入第三方美颜的处理引擎
         *
         * @param texId 视频帧纹理
         * @param width 纹理宽度
         * @param height 纹理高度
         * @param transformMatrix 纹理变换矩阵
         * @return 处理后的纹理
         */
        @Override
        public int onDrawFrame(int texId, int width, int height, float[] transformMatrix) {
            Log.i(TAG, "onDrawFrame : " + Thread.currentThread().getId());
            return mFBO.drawFrame(texId, width, height);
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
                if (mPicStreamingFilePath != null) {
                    mMediaStreamingManager.setPictureStreamingFilePath(mPicStreamingFilePath);
                    mControlFragment.setPicStreamingImage(mPicStreamingFilePath);
                } else {
                    mMediaStreamingManager.setPictureStreamingResourceId(R.drawable.qiniu_logo);
                    mControlFragment.setPicStreamingImage(R.drawable.qiniu_logo);
                }
            } else {
                mMediaStreamingManager.setPictureStreamingResourceId(R.drawable.pause_publish);
                mControlFragment.setPicStreamingImage(R.drawable.pause_publish);
            }
            mTimes++;
            // 演示周期性切换推流的图片
            if (mMainThreadHandler != null) {
                mMainThreadHandler.postDelayed(this, 2000);
            }
        }
    }

    private class ViewTouchListener implements View.OnTouchListener {
        private float lastTouchRawX;
        private float lastTouchRawY;
        private boolean scale;
        private View mView;

        public ViewTouchListener(View view) {
            mView = view;
        }

        GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                ((FrameLayout) findViewById(R.id.content)).removeView(mView);
                mMediaStreamingManager.removeOverlay(mView);
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                return true;
            }
        };

        final GestureDetector gestureDetector = new GestureDetector(AVStreamingActivity.this, simpleOnGestureListener);

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (gestureDetector.onTouchEvent(event)) {
                return true;
            }

            int action = event.getAction();
            float touchRawX = event.getRawX();
            float touchRawY = event.getRawY();
            float touchX = event.getX();
            float touchY = event.getY();

            if (action == MotionEvent.ACTION_DOWN) {
                boolean xOK = touchX >= v.getWidth() * 3 / 4 && touchX <= v.getWidth();
                boolean yOK = touchY >= v.getHeight() * 2 / 4 && touchY <= v.getHeight();
                scale = xOK && yOK;
            }

            if (action == MotionEvent.ACTION_MOVE) {
                float deltaRawX = touchRawX - lastTouchRawX;
                float deltaRawY = touchRawY - lastTouchRawY;

                if (scale) {
                    // rotate
                    float centerX = v.getX() + (float) v.getWidth() / 2;
                    float centerY = v.getY() + (float) v.getHeight() / 2;
                    double angle = Math.atan2(touchRawY - centerY, touchRawX - centerX) * 180 / Math.PI;
                    v.setRotation((float) angle - 45);

                    // scale
                    float xx = (touchRawX >= centerX ? deltaRawX : -deltaRawX);
                    float yy = (touchRawY >= centerY ? deltaRawY : -deltaRawY);
                    float sf = (v.getScaleX() + xx / v.getWidth() + v.getScaleY() + yy / v.getHeight()) / 2;
                    v.setScaleX(sf);
                    v.setScaleY(sf);
                } else {
                    // translate
                    v.setTranslationX(v.getTranslationX() + deltaRawX);
                    v.setTranslationY(v.getTranslationY() + deltaRawY);
                }
            }

            if (action == MotionEvent.ACTION_UP) {
//                当 mMediaStreamingManager.setAutoRefreshOverlay(false) 时自动刷新关闭，建议在 UP 事件里进行手动刷新。
//                mMediaStreamingManager.refreshOverlay(v, false);
            }

            lastTouchRawX = touchRawX;
            lastTouchRawY = touchRawY;
            return true;
        }
    }

    private void startStreamingInternal() {
        startStreamingInternal(0);
    }

    private void startStreamingInternal(long delayMillis) {
        if (mMediaStreamingManager == null) {
            return;
        }
        // startStreaming 为耗时操作，建议放到子线程执行
        if (mSubThreadHandler != null) {
            mSubThreadHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    final boolean res = mMediaStreamingManager.startStreaming();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mControlFragment.setShutterButtonPressed(res);
                        }
                    });
                }
            }, delayMillis);
        }
    }

    private void stopStreamingInternal() {
        if (mMediaStreamingManager == null || !mIsStreaming) {
            return;
        }
        if (mSubThreadHandler != null) {
            mSubThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    final boolean res = mMediaStreamingManager.stopStreaming();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mControlFragment.setShutterButtonPressed(!res);
                        }
                    });
                }
            });
        }
    }

    private boolean isPictureStreaming() {
        if (mIsPictureStreaming) {
            Toast.makeText(AVStreamingActivity.this, "is picture streaming, operation failed!", Toast.LENGTH_SHORT).show();
        }
        return mIsPictureStreaming;
    }

    private boolean isFrontFacing() {
        return mCurrentCamFacingIndex == CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT.ordinal();
    }

    private void saveToSDCard(String filename, Bitmap bmp) throws IOException {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory(), filename);
            BufferedOutputStream bos = null;
            try {
                bos = new BufferedOutputStream(new FileOutputStream(file));
                bmp.compress(Bitmap.CompressFormat.PNG, 90, bos);
                bmp.recycle();
                bmp = null;
            } finally {
                if (bos != null) {
                    bos.close();
                }
            }

            final String info = "Save frame to:" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AVStreamingActivity.this, info, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void updateMixBtnText() {
        if (mAudioMixer != null && mAudioMixer.isRunning()) {
            mControlFragment.setAudioMixControllerText("Pause");
        } else {
            mControlFragment.setAudioMixControllerText("Play");
        }
    }

    protected void setFocusAreaIndicator() {
        if (mRotateLayout == null) {
            mRotateLayout = (RotateLayout) findViewById(R.id.focus_indicator_rotate_layout);
            mMediaStreamingManager.setFocusAreaIndicator(mRotateLayout,
                    mRotateLayout.findViewById(R.id.focus_indicator));
        }
    }

    private boolean isHwVideoEncodeType() {
        return mEncodingConfig.mCodecType == AVCodecType.HW_VIDEO_SURFACE_AS_INPUT_WITH_HW_AUDIO_CODEC ||
                mEncodingConfig.mCodecType == AVCodecType.HW_VIDEO_SURFACE_AS_INPUT_WITH_SW_AUDIO_CODEC ||
                mEncodingConfig.mCodecType == AVCodecType.HW_VIDEO_WITH_HW_AUDIO_CODEC ||
                mEncodingConfig.mCodecType == AVCodecType.HW_VIDEO_CODEC;
    }

    private void showSEIDialog() {
        final EditText editText = new EditText(AVStreamingActivity.this);
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(AVStreamingActivity.this);
        inputDialog.setTitle("请输入 SEI 信息").setView(editText);
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String seiMsg = editText.getText().toString();
                        if ("".equals(seiMsg)) {
                            Util.showToast(AVStreamingActivity.this, "请输入有效信息！");
                            return;
                        }
                        if (mMediaStreamingManager != null && mIsStreaming) {
                            mMediaStreamingManager.sendSEIMessage(editText.getText().toString(), 5);
                            Util.showToast(AVStreamingActivity.this, "SEI 已发送，该功能需搭配支持 SEI 的播放器方可验证！", Toast.LENGTH_LONG);
                        } else {
                            Util.showToast(AVStreamingActivity.this, "请先开始推流！");
                        }
                    }
                }).show();
    }
}
