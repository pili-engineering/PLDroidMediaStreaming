package com.qiniu.pili.droid.streaming.demo.activity;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.os.Build;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.qiniu.pili.droid.streaming.AVCodecType;
import com.qiniu.pili.droid.streaming.CameraStreamingSetting;
import com.qiniu.pili.droid.streaming.FrameCapturedCallback;
import com.qiniu.pili.droid.streaming.MediaStreamingManager;
import com.qiniu.pili.droid.streaming.MicrophoneStreamingSetting;
import com.qiniu.pili.droid.streaming.StreamingPreviewCallback;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.SurfaceTextureCallback;
import com.qiniu.pili.droid.streaming.WatermarkSetting;
import com.qiniu.pili.droid.streaming.av.common.PLFourCC;
import com.qiniu.pili.droid.streaming.demo.R;
import com.qiniu.pili.droid.streaming.demo.gles.FBO;
import com.qiniu.pili.droid.streaming.demo.plain.CameraConfig;
import com.qiniu.pili.droid.streaming.demo.ui.CameraPreviewFrameView;
import com.qiniu.pili.droid.streaming.demo.ui.RotateLayout;
import com.qiniu.pili.droid.streaming.demo.utils.Cache;
import com.qiniu.pili.droid.streaming.demo.utils.Config;
import com.qiniu.pili.droid.streaming.microphone.AudioMixer;
import com.qiniu.pili.droid.streaming.microphone.OnAudioMixListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class AVStreamingActivity extends StreamingBaseActivity implements
        StreamingPreviewCallback,
        CameraPreviewFrameView.Listener,
        SurfaceTextureCallback {
    private static final String TAG = "AVStreamingActivity";

    private CameraStreamingSetting mCameraStreamingSetting;
    private CameraConfig mCameraConfig;

    private Button mMuteButton;
    private Button mTorchBtn;
    private Button mCameraSwitchBtn;
    private Button mCaptureFrameBtn;
    private Button mEncodingOrientationSwitcherBtn;
    private Button mFaceBeautyBtn;
    private RotateLayout mRotateLayout;

    private Button mMixToggleBtn;
    private SeekBar mMixProgress;

    private boolean mIsTorchOn = false;
    private boolean mIsNeedMute = false;
    private boolean mIsNeedFB = false;
    private boolean mIsPreviewMirror = false;
    private boolean mIsEncodingMirror = false;
    private boolean mIsPlayingback = false;

    private int mCurrentZoom = 0;
    private int mMaxZoom = 0;
    private boolean mOrientationChanged = false;
    private int mCurrentCamFacingIndex;

    private FBO mFBO = new FBO();

    private ScreenShooter mScreenShooter = new ScreenShooter();
    private Switcher mSwitcher = new Switcher();
    private EncodingOrientationSwitcher mEncodingOrientationSwitcher = new EncodingOrientationSwitcher();
    private ImageSwitcher mImageSwitcher;

    private MediaStreamingManager mMediaStreamingManager;
    private AudioMixer mAudioMixer;
    private String mAudioFile;

    private Handler mHandler;
    private int mTimes = 0;
    private boolean mIsPictureStreaming = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaStreamingManager.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        normalPause();
    }

    private void normalPause() {
        mIsReady = false;
        mShutterButtonPressed = false;
        mIsPictureStreaming = false;
        if (mHandler != null) {
            mHandler.getLooper().quit();
        }
        mMediaStreamingManager.pause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaStreamingManager.destroy();
    }

    @Override
    protected void initStreamingManager() {
        CameraPreviewFrameView cameraPreviewFrameView = (CameraPreviewFrameView) findViewById(R.id.cameraPreview_surfaceView);
        mMediaStreamingManager = new MediaStreamingManager(this, cameraPreviewFrameView, mEncodingConfig.mCodecType);
        if (mEncodingConfig.mIsPictureStreamingEnabled) {
            if (mEncodingConfig.mPictureStreamingFilePath == null) {
                mProfile.setPictureStreamingResourceId(R.drawable.pause_publish);
            } else {
                mProfile.setPictureStreamingFilePath(mEncodingConfig.mPictureStreamingFilePath);
            }
        }
        MicrophoneStreamingSetting microphoneStreamingSetting = null;
        if (mAudioStereoEnable) {
            /**
             * Notice !!! {@link AudioFormat#CHANNEL_IN_STEREO} is NOT guaranteed to work on all devices.
             */
            microphoneStreamingSetting = new MicrophoneStreamingSetting();
            microphoneStreamingSetting.setChannelConfig(AudioFormat.CHANNEL_IN_STEREO);
        }
        mMediaStreamingManager.prepare(mCameraStreamingSetting, microphoneStreamingSetting, buildWatermarkSetting(), mProfile);
        mMediaStreamingManager.setAutoRefreshOverlay(true);
        if (mCameraConfig.mIsCustomFaceBeauty) {
            mMediaStreamingManager.setSurfaceTextureCallback(this);
        }
        cameraPreviewFrameView.setListener(this);
        mMediaStreamingManager.setStreamingSessionListener(this);
        mMediaStreamingManager.setStreamStatusCallback(this);
        mMediaStreamingManager.setAudioSourceCallback(this);
        mMediaStreamingManager.setStreamingStateListener(this);

        mAudioMixer = mMediaStreamingManager.getAudioMixer();
        mAudioMixer.setOnAudioMixListener(new OnAudioMixListener() {
            @Override
            public void onStatusChanged(MixStatus mixStatus) {
                mMixToggleBtn.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AVStreamingActivity.this, "mix finished", Toast.LENGTH_LONG).show();
                        updateMixBtnText();
                    }
                });
            }

            @Override
            public void onProgress(long l, long l1) {
                mMixProgress.setProgress((int) l);
                mMixProgress.setMax((int) l1);
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

    @Override
    protected boolean startStreaming() {
        return mMediaStreamingManager.startStreaming();
    }

    @Override
    protected boolean stopStreaming() {
        return mMediaStreamingManager.stopStreaming();
    }

    private class EncodingOrientationSwitcher implements Runnable {
        @Override
        public void run() {
            Log.i(TAG, "mIsEncOrientationPort:" + mIsEncOrientationPort);
            mOrientationChanged = true;
            mIsEncOrientationPort = !mIsEncOrientationPort;
            mProfile.setEncodingOrientation(mIsEncOrientationPort ? StreamingProfile.ENCODING_ORIENTATION.PORT : StreamingProfile.ENCODING_ORIENTATION.LAND);
            mMediaStreamingManager.setStreamingProfile(mProfile);
            stopStreamingInternal();
            setRequestedOrientation(mIsEncOrientationPort ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mMediaStreamingManager.notifyActivityOrientationChanged();
            updateOrientationBtnText();
            Toast.makeText(AVStreamingActivity.this, Config.HINT_ENCODING_ORIENTATION_CHANGED,
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "EncodingOrientationSwitcher -");
        }
    }

    private class Switcher implements Runnable {
        @Override
        public void run() {
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
            mMediaStreamingManager.switchCamera(facingId);

            mIsEncodingMirror = mCameraConfig.mEncodingMirror;
            mIsPreviewMirror = facingId == CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT ? mCameraConfig.mPreviewMirror : false;
        }
    }

    private class ScreenShooter implements Runnable {
        @Override
        public void run() {
            final String fileName = "PLStreaming_" + System.currentTimeMillis() + ".jpg";
            mMediaStreamingManager.captureFrame(100, 100, new FrameCapturedCallback() {
                private Bitmap bitmap;
                @Override
                public void onFrameCaptured(Bitmap bmp) {
                    if (bmp == null) {
                        return;
                    }
                    bitmap = bmp;
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
    }

    /**
     * switch picture during streaming
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
                    mMediaStreamingManager.setPictureStreamingFilePath(mEncodingConfig.mPictureStreamingFilePath);
                } else {
                    mMediaStreamingManager.setPictureStreamingResourceId(R.drawable.qiniu_logo);
                }
            } else {
                mMediaStreamingManager.setPictureStreamingResourceId(R.drawable.pause_publish);
            }
            mTimes++;
            if (mHandler != null && mIsPictureStreaming) {
                mHandler.postDelayed(this, 1000);
            }
        }
    }

    private boolean isPictureStreaming() {
        if (mIsPictureStreaming) {
            Toast.makeText(AVStreamingActivity.this, "is picture streaming, operation failed!", Toast.LENGTH_SHORT).show();
        }
        return mIsPictureStreaming;
    }

    private void togglePictureStreaming() {
        boolean isOK = mMediaStreamingManager.togglePictureStreaming();
        if (!isOK) {
            Toast.makeText(AVStreamingActivity.this, "toggle picture streaming failed!", Toast.LENGTH_SHORT).show();
            return;
        }

        mIsPictureStreaming = !mIsPictureStreaming;

        mTimes = 0;
        if (mIsPictureStreaming) {
            if (mImageSwitcher == null) {
                mImageSwitcher = new ImageSwitcher();
            }

            HandlerThread handlerThread = new HandlerThread(TAG);
            handlerThread.start();
            mHandler = new Handler(handlerThread.getLooper());
            mHandler.postDelayed(mImageSwitcher, 1000);
        } else {
            if (mHandler != null) {
                mHandler.getLooper().quit();
            }
        }
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
                if (bos != null) bos.close();
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

    /**
     * Accept only 32 bit png (ARGB)
     * @return
     */
    private WatermarkSetting buildWatermarkSetting() {
        if (!mEncodingConfig.mIsWatermarkEnabled) {
            return null;
        }
        WatermarkSetting watermarkSetting = new WatermarkSetting(this);
        watermarkSetting.setResourceId(R.drawable.qiniu_logo);
        watermarkSetting.setAlpha(mEncodingConfig.mWatermarkAlpha);
        watermarkSetting.setSize(mEncodingConfig.mWatermarkSize);
        if (mEncodingConfig.mWatermarkCustomWidth != 0 || mEncodingConfig.mWatermarkCustomHeight != 0) {
            watermarkSetting.setCustomSize(mEncodingConfig.mWatermarkCustomWidth, mEncodingConfig.mWatermarkCustomHeight);
        }
        if (mEncodingConfig.mIsWatermarkLocationPreset) {
            watermarkSetting.setLocation(mEncodingConfig.mWatermarkLocationPreset);
        } else {
            watermarkSetting.setCustomPosition(mEncodingConfig.mWatermarkLocationCustomX, mEncodingConfig.mWatermarkLocationCustomY);
        }

        return watermarkSetting;
    }

    private CameraStreamingSetting buildCameraStreamingSetting() {
        mCameraConfig = (CameraConfig) getIntent().getSerializableExtra("CameraConfig");

        CameraStreamingSetting cameraStreamingSetting = new CameraStreamingSetting();
        cameraStreamingSetting.setCameraId(mCameraConfig.mFrontFacing ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK)
                .setCameraPrvSizeLevel(mCameraConfig.mSizeLevel)
                .setCameraPrvSizeRatio(mCameraConfig.mSizeRatio)
                .setFocusMode(mCameraConfig.mFocusMode)
                .setContinuousFocusModeEnabled(mCameraConfig.mContinuousAutoFocus)
                .setFrontCameraPreviewMirror(mCameraConfig.mPreviewMirror)
                .setFrontCameraMirror(mCameraConfig.mEncodingMirror).setRecordingHint(false)
                .setResetTouchFocusDelayInMs(3000)
                .setBuiltInFaceBeautyEnabled(!mCameraConfig.mIsCustomFaceBeauty)
                .setFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(1.0f, 1.0f, 0.8f));

        if (mCameraConfig.mIsFaceBeautyEnabled) {
            cameraStreamingSetting.setVideoFilter(CameraStreamingSetting.VIDEO_FILTER_TYPE.VIDEO_FILTER_BEAUTY);
        } else {
            cameraStreamingSetting.setVideoFilter(CameraStreamingSetting.VIDEO_FILTER_TYPE.VIDEO_FILTER_NONE);
        }

        return cameraStreamingSetting;
    }

    @Override
    public Camera.Size onPreviewSizeSelected(List<Camera.Size> list) {
        /**
         * You should choose a suitable size to avoid image scale
         * eg: If streaming size is 1280 x 720, you should choose a camera preview size >= 1280 x 720
         */
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

    @Override
    public void initView() {
        mCameraStreamingSetting = buildCameraStreamingSetting();
        mIsEncOrientationPort = mEncodingConfig.mVideoOrientationPortrait;
        mIsNeedFB = mCameraConfig.mIsFaceBeautyEnabled;
        mIsPreviewMirror = mCameraConfig.mPreviewMirror;
        mIsEncodingMirror = mCameraConfig.mEncodingMirror;
        mCurrentCamFacingIndex = mCameraConfig.mFrontFacing ? 1 : 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        } else {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setRequestedOrientation(mIsEncOrientationPort ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_av_streaming);

        mMuteButton = (Button) findViewById(R.id.mute_btn);
        mTorchBtn = (Button) findViewById(R.id.torch_btn);
        mCameraSwitchBtn = (Button) findViewById(R.id.camera_switch_btn);
        mCaptureFrameBtn = (Button) findViewById(R.id.capture_btn);
        mFaceBeautyBtn = (Button) findViewById(R.id.fb_btn);
        Button previewMirrorBtn = (Button) findViewById(R.id.preview_mirror_btn);
        Button encodingMirrorBtn = (Button) findViewById(R.id.encoding_mirror_btn);
        Button picStreamingBtn = (Button) findViewById(R.id.pic_streaming_btn);
        Button addOverlayBtn = (Button) findViewById(R.id.add_overlay_btn);

        mFaceBeautyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsNeedFB = !mIsNeedFB;
                mMediaStreamingManager.setVideoFilterType(mIsNeedFB ?
                        CameraStreamingSetting.VIDEO_FILTER_TYPE.VIDEO_FILTER_BEAUTY
                        : CameraStreamingSetting.VIDEO_FILTER_TYPE.VIDEO_FILTER_NONE);
                updateFBButtonText();
            }
        });

        mMuteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsNeedMute = !mIsNeedMute;
                mMediaStreamingManager.mute(mIsNeedMute);
                updateMuteButtonText();
            }
        });

        previewMirrorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPictureStreaming()) {
                    return;
                }

                mIsPreviewMirror = !mIsPreviewMirror;
                mMediaStreamingManager.setPreviewMirror(mIsPreviewMirror);
                Toast.makeText(AVStreamingActivity.this, "镜像成功", Toast.LENGTH_SHORT).show();
            }
        });

        encodingMirrorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPictureStreaming()) {
                    return;
                }

                mIsEncodingMirror = !mIsEncodingMirror;
                mMediaStreamingManager.setEncodingMirror(mIsEncodingMirror);
                Toast.makeText(AVStreamingActivity.this, "镜像成功", Toast.LENGTH_SHORT).show();
            }
        });

        picStreamingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProfile.setPictureStreamingFps(10);
                togglePictureStreaming();
            }
        });

        if (mEncodingConfig.mCodecType == AVCodecType.HW_VIDEO_SURFACE_AS_INPUT_WITH_HW_AUDIO_CODEC ||
                mEncodingConfig.mCodecType == AVCodecType.HW_VIDEO_SURFACE_AS_INPUT_WITH_SW_AUDIO_CODEC ||
                mEncodingConfig.mCodecType == AVCodecType.HW_VIDEO_WITH_HW_AUDIO_CODEC ||
                mEncodingConfig.mCodecType == AVCodecType.HW_VIDEO_CODEC) {
            addOverlayBtn.setVisibility(View.VISIBLE);
            addOverlayBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView imageOverlay = new ImageView(AVStreamingActivity.this);
                    imageOverlay.setImageResource(R.drawable.qiniu_logo);
                    imageOverlay.setOnTouchListener(new ViewTouchListener(imageOverlay));
                    ((FrameLayout) findViewById(R.id.content)).addView(imageOverlay, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));

                    mMediaStreamingManager.addOverlay(imageOverlay);
                    Toast.makeText(AVStreamingActivity.this, "双击删除贴图!", Toast.LENGTH_LONG).show();
                }
            });
        }

        mTorchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPictureStreaming()) {
                    return;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (mIsTorchOn) {
                            mMediaStreamingManager.turnLightOff();
                        } else {
                            mMediaStreamingManager.turnLightOn();
                        }
                        mIsTorchOn = !mIsTorchOn;
                        setTorchEnabled(mIsTorchOn);
                    }
                }).start();
            }
        });

        mCameraSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPictureStreaming()) {
                    return;
                }

                mCameraSwitchBtn.removeCallbacks(mSwitcher);
                mCameraSwitchBtn.postDelayed(mSwitcher, 100);
            }
        });

        mCaptureFrameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPictureStreaming()) {
                    return;
                }

                mCaptureFrameBtn.removeCallbacks(mScreenShooter);
                mCaptureFrameBtn.postDelayed(mScreenShooter, 100);
            }
        });

        mEncodingOrientationSwitcherBtn = (Button) findViewById(R.id.orientation_btn);
        mEncodingOrientationSwitcherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPictureStreaming()) {
                    return;
                }

                mEncodingOrientationSwitcherBtn.removeCallbacks(mEncodingOrientationSwitcher);
                mEncodingOrientationSwitcherBtn.postDelayed(mEncodingOrientationSwitcher, 100);
            }
        });

        SeekBar seekBarBeauty = (SeekBar) findViewById(R.id.beautyLevel_seekBar);
        seekBarBeauty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                CameraStreamingSetting.FaceBeautySetting fbSetting = mCameraStreamingSetting.getFaceBeautySetting();
                fbSetting.beautyLevel = progress / 100.0f;
                fbSetting.whiten = progress / 100.0f;
                fbSetting.redden = progress / 100.0f;

                mMediaStreamingManager.updateFaceBeautySetting(fbSetting);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        initButtonText();
        initAudioMixerPanel();
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

    private void initButtonText() {
        updateFBButtonText();
        updateCameraSwitcherButtonText(mCameraStreamingSetting.getReqCameraId());
        mCaptureFrameBtn.setText("Capture");
        updateFBButtonText();
        updateMuteButtonText();
        updateOrientationBtnText();
    }

    private void initAudioMixerPanel() {
        Button mixPanelBtn = (Button) findViewById(R.id.mix_panel_btn);
        mixPanelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View panel = findViewById(R.id.mix_panel);
                panel.setVisibility(panel.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });

        mMixProgress = (SeekBar) findViewById(R.id.mix_progress);
        mMixProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mAudioMixer != null) {
                    mAudioMixer.seek(1.0f * seekBar.getProgress() / seekBar.getMax());
                }
            }
        });

        SeekBar mixVolume = (SeekBar) findViewById(R.id.mix_volume);
        mixVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mAudioMixer != null) {
                    mAudioMixer.setVolume(1.0f, 1.0f * seekBar.getProgress() / seekBar.getMax());
                }
            }
        });

        Button mixFileBtn = (Button) findViewById(R.id.mix_file_btn);
        mixFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

        mMixToggleBtn = (Button) findViewById(R.id.mix_btn);
        mMixToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

        Button mixStopBtn = (Button) findViewById(R.id.mix_stop_btn);
        mixStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAudioMixer != null) {
                    boolean stopSuccess = mAudioMixer.stop();
                    String text = stopSuccess ? "mixing stop success" : "mixing stop failed !!!";
                    Toast.makeText(AVStreamingActivity.this, text, Toast.LENGTH_LONG).show();
                    if (stopSuccess) {
                        updateMixBtnText();
                    }
                }
            }
        });

        Button playbackToggleBtn = (Button) findViewById(R.id.playback_btn);
        playbackToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsPlayingback) {
                    mMediaStreamingManager.stopPlayback();
                } else {
                    mMediaStreamingManager.startPlayback();
                }
                mIsPlayingback = !mIsPlayingback;
            }
        });

        updateMixBtnText();
    }

    private void updateMixBtnText() {
        if (mAudioMixer != null && mAudioMixer.isRunning()) {
            mMixToggleBtn.setText("Pause");
        } else {
            mMixToggleBtn.setText("Play");
        }
    }

    @Override
    public void onStateChanged(StreamingState streamingState, Object extra) {
        /**
         * general states are handled in the `StreamingBaseActivity`
         */
        super.onStateChanged(streamingState, extra);
        switch (streamingState) {
            case READY:
                mMaxZoom = mMediaStreamingManager.getMaxZoom();
                break;
            case SHUTDOWN:
                if (mOrientationChanged) {
                    mOrientationChanged = false;
                    startStreamingInternal();
                }
                break;
            case OPEN_CAMERA_FAIL:
                Log.e(TAG, "Open Camera Fail. id:" + extra);
                break;
            case CAMERA_SWITCHED:
                if (extra != null) {
                    Log.i(TAG, "current camera id:" + (Integer) extra);
                }
                Log.i(TAG, "camera switched");
                final int currentCamId = (Integer) extra;
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateCameraSwitcherButtonText(currentCamId);
                    }
                });
                break;
            case TORCH_INFO:
                if (extra != null) {
                    final boolean isSupportedTorch = (Boolean) extra;
                    Log.i(TAG, "isSupportedTorch=" + isSupportedTorch);
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isSupportedTorch) {
                                mTorchBtn.setVisibility(View.VISIBLE);
                            } else {
                                mTorchBtn.setVisibility(View.GONE);
                            }
                        }
                    });
                }
                break;
        }
    }

    protected void setFocusAreaIndicator() {
        if (mRotateLayout == null) {
            mRotateLayout = (RotateLayout) findViewById(R.id.focus_indicator_rotate_layout);
            mMediaStreamingManager.setFocusAreaIndicator(mRotateLayout,
                    mRotateLayout.findViewById(R.id.focus_indicator));
        }
    }

    private void setTorchEnabled(final boolean enabled) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String flashlight = enabled ? getString(R.string.flash_light_off) : getString(R.string.flash_light_on);
                mTorchBtn.setText(flashlight);
            }
        });
    }

    private void updateOrientationBtnText() {
        if (mIsEncOrientationPort) {
            mEncodingOrientationSwitcherBtn.setText("Land");
        } else {
            mEncodingOrientationSwitcherBtn.setText("Port");
        }
    }

    private void updateFBButtonText() {
        if (mFaceBeautyBtn != null) {
            mFaceBeautyBtn.setText(mIsNeedFB ? "FB Off" : "FB On");
        }
    }

    private void updateMuteButtonText() {
        if (mMuteButton != null) {
            mMuteButton.setText(mIsNeedMute ? "Unmute" : "Mute");
        }
    }

    private void updateCameraSwitcherButtonText(int camId) {
        if (mCameraSwitchBtn == null) {
            return;
        }
        if (camId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mCameraSwitchBtn.setText("Back");
        } else {
            mCameraSwitchBtn.setText("Front");
        }
    }

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

    @Override
    public boolean onZoomValueChanged(float factor) {
        if (mIsReady && mMediaStreamingManager.isZoomSupported()) {
            mCurrentZoom = (int) (mMaxZoom * factor);
            mCurrentZoom = Math.min(mCurrentZoom, mMaxZoom);
            mCurrentZoom = Math.max(0, mCurrentZoom);
            Log.d(TAG, "zoom ongoing, scale: " + mCurrentZoom + ",factor:" + factor + ",maxZoom:" + mMaxZoom);
            mMediaStreamingManager.setZoomValue(mCurrentZoom);
        }
        return false;
    }

    @Override
    public void onSurfaceCreated() {
        Log.i(TAG, "onSurfaceCreated");
        /**
         * only used in custom beauty algorithm case
         */
        mFBO.initialize(this);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        Log.i(TAG, "onSurfaceChanged width:" + width + ",height:" + height);
        /**
         * only used in custom beauty algorithm case
         */
        mFBO.updateSurfaceSize(width, height);
    }

    @Override
    public void onSurfaceDestroyed() {
        Log.i(TAG, "onSurfaceDestroyed");
        /**
         * only used in custom beauty algorithm case
         */
        mFBO.release();
    }

    @Override
    public int onDrawFrame(int texId, int texWidth, int texHeight, float[] transformMatrix) {
        /**
         * When using custom beauty algorithm, you should return a new texId from the SurfaceTexture.
         * newTexId should not equal with texId, Otherwise, there is no filter effect.
         */
        int newTexId = mFBO.drawFrame(texId, texWidth, texHeight);
        return newTexId;
    }

    @Override
    public boolean onPreviewFrame(byte[] bytes, int width, int height, int rotation, int fmt, long tsInNanoTime) {
        Log.i(TAG, "onPreviewFrame " + width + "x" + height + ",fmt:" + (fmt == PLFourCC.FOURCC_I420 ? "I420" : "NV21") + ",ts:" + tsInNanoTime + ",rotation:" + rotation);
        /**
         * When using custom beauty algorithm in sw encode mode, you should change the bytes array's values here
         * eg: byte[] beauties = readPixelsFromGPU();
         * System.arraycopy(beauties, 0, bytes, 0, bytes.length);
         */
        return true;
    }
}
