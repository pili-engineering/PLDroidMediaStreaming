package com.qiniu.pili.droid.streaming.demo.fragment;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.qiniu.pili.droid.streaming.demo.R;

import java.io.File;

/**
 * 音视频推流界面的 UI 控制 Fragment，仅用于 demo 展示
 */
public class ControlFragment extends Fragment {
    public static final String KEY_ENCODE_ORIENTATION = "encodeOrientation";
    public static final String KEY_BEAUTY_ON = "beautyOn";
    public static final String KEY_HW_VIDEO_ENCODE_TYPE = "hwVideoEncodeType";

    private View mFragmentView;
    private Button mMuteButton;
    private Button mTorchBtn;
    private ImageButton mCameraSwitchBtn;
    private Button mCaptureFrameBtn;
    private Button mEncodingOrientationSwitcherBtn;
    private Button mFaceBeautyBtn;
    private Button mShutterButton;
    private Button mControlViewBtn;
    private SeekBar mBeautyLevelSeekBar;
    private ScrollView mControlView;
    private TextView mLogTextView;
    private TextView mStatusTextView;
    private TextView mStreamingStatsView;
    private Button mMixToggleBtn;
    private SeekBar mMixProgress;
    private ImageView mPicStreamingImageView;

    private boolean mIsHwVideoEncodeType = false;
    private boolean mShutterButtonPressed = false;
    private boolean mIsNeedFB = false;
    private boolean mIsNeedMute = false;
    private boolean mIsTorchOn = false;
    private boolean mIsEncOrientationPort = true;
    private boolean mIsControlViewVisible = false;

    private OnEventClickedListener mOnEventClickedListener;

    public interface OnEventClickedListener {
        void onMuteClicked(boolean isMute);
        void onStreamingStartClicked(boolean isNeedStart);
        void onPreviewMirrorClicked();
        void onEncodingMirrorClicked();
        boolean onTorchClicked(boolean isTorchOn);
        void onCameraSwitchClicked();
        void onPictureStreamingClicked();
        void onCaptureFrameClicked();
        boolean onOrientationChanged(boolean isPortrait);
        void onFaceBeautyClicked(boolean isBeautyOn);
        void onFaceBeautyProgressChanged(int progress);
        void onAddOverlayClicked();
        void onAudioMixFileSelectionClicked();
        void onAudioMixVolumeChanged(float volume);
        void onAudioMixPositionChanged(float position);
        void onAudioMixControllerClicked();
        void onAudioMixStopClicked();
        void onAudioMixPlaybackClicked();
        void onAudioMixLoopEnabled(boolean enabled);
        void onSendSEIClicked();
    }

    public void setOnEventClickedListener(OnEventClickedListener listener) {
        mOnEventClickedListener = listener;
    }

    public void setShutterButtonEnabled(final boolean enable) {
        mShutterButton.setFocusable(enable);
        mShutterButton.setClickable(enable);
        mShutterButton.setEnabled(enable);
    }

    public void setShutterButtonPressed(final boolean pressed) {
        mShutterButtonPressed = pressed;
        mShutterButton.setPressed(pressed);
    }

    public void setStatusText(String statusText) {
        if (mStatusTextView != null) {
            mStatusTextView.setText(statusText);
        }
    }

    public void updateLogText(String logText) {
        if (mLogTextView != null) {
            mLogTextView.setText(logText);
        }
    }

    public void setStreamStatsText(String statsText) {
        if (mStreamingStatsView != null) {
            mStreamingStatsView.setText(statsText);
        }
    }

    public void setAudioMixControllerText(String text) {
        if (mMixToggleBtn != null) {
            mMixToggleBtn.setText(text);
        }
    }

    public void updateAudioMixProgress(long progress, long duration) {
        mMixProgress.setProgress((int) progress);
        mMixProgress.setMax((int) duration);
    }

    public void setPictureImageVisible(boolean isVisible) {
        if (mPicStreamingImageView != null) {
            mPicStreamingImageView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }

    public void setPicStreamingImage(String path) {
        if (mPicStreamingImageView != null) {
            mPicStreamingImageView.setImageURI(Uri.fromFile(new File(path)));
        }
    }

    public void setPicStreamingImage(int resId) {
        if (mPicStreamingImageView != null) {
            mPicStreamingImageView.setImageResource(resId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(R.layout.fragment_control_streaming, container, false);

        mMuteButton = mFragmentView.findViewById(R.id.mute_btn);
        mTorchBtn = mFragmentView.findViewById(R.id.torch_btn);
        mCameraSwitchBtn = mFragmentView.findViewById(R.id.camera_switch_btn);
        mCaptureFrameBtn = mFragmentView.findViewById(R.id.capture_btn);
        mFaceBeautyBtn = mFragmentView.findViewById(R.id.fb_btn);
        Button previewMirrorBtn = mFragmentView.findViewById(R.id.preview_mirror_btn);
        Button encodingMirrorBtn = mFragmentView.findViewById(R.id.encoding_mirror_btn);
        Button picStreamingBtn = mFragmentView.findViewById(R.id.pic_streaming_btn);
        Button addOverlayBtn = mFragmentView.findViewById(R.id.add_overlay_btn);
        Button sendSEIBtn = mFragmentView.findViewById(R.id.send_sei_btn);
        mLogTextView = mFragmentView.findViewById(R.id.log_info);
        mStatusTextView = mFragmentView.findViewById(R.id.streamingStatus);
        mStreamingStatsView = mFragmentView.findViewById(R.id.stream_status);
        mShutterButton = mFragmentView.findViewById(R.id.toggleRecording_button);
        mControlViewBtn = mFragmentView.findViewById(R.id.control_view_btn);
        mControlView = mFragmentView.findViewById(R.id.control_view);
        mPicStreamingImageView = mFragmentView.findViewById(R.id.picture_streaming_image);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mIsNeedFB = bundle.getBoolean(KEY_BEAUTY_ON);
            mIsEncOrientationPort = bundle.getBoolean(KEY_ENCODE_ORIENTATION);
            mIsHwVideoEncodeType = bundle.getBoolean(KEY_HW_VIDEO_ENCODE_TYPE);
        }
        updateFBButtonText();
        updateOrientationBtnText();

        mShutterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnEventClickedListener != null) {
                    mOnEventClickedListener.onStreamingStartClicked(!mShutterButtonPressed);
                }
            }
        });

        mFaceBeautyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnEventClickedListener == null) {
                    return;
                }
                mIsNeedFB = !mIsNeedFB;
                mOnEventClickedListener.onFaceBeautyClicked(mIsNeedFB);
                updateFBButtonText();
                mBeautyLevelSeekBar.setVisibility(mIsNeedFB ? View.VISIBLE : View.GONE);
            }
        });

        mMuteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnEventClickedListener == null) {
                    return;
                }
                mIsNeedMute = !mIsNeedMute;
                mOnEventClickedListener.onMuteClicked(mIsNeedMute);
                updateMuteButtonText();
            }
        });

        previewMirrorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnEventClickedListener != null) {
                    mOnEventClickedListener.onPreviewMirrorClicked();
                }
            }
        });

        encodingMirrorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnEventClickedListener != null) {
                    mOnEventClickedListener.onEncodingMirrorClicked();
                }
            }
        });

        picStreamingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnEventClickedListener != null) {
                    mOnEventClickedListener.onPictureStreamingClicked();
                }
            }
        });

        sendSEIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnEventClickedListener != null) {
                    mOnEventClickedListener.onSendSEIClicked();
                }
            }
        });

        if (mIsHwVideoEncodeType) {
            addOverlayBtn.setVisibility(View.VISIBLE);
            addOverlayBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnEventClickedListener != null) {
                        mOnEventClickedListener.onAddOverlayClicked();
                    }
                }
            });
        }

        mTorchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnEventClickedListener != null) {
                    mIsTorchOn = !mIsTorchOn;
                    boolean res = mOnEventClickedListener.onTorchClicked(mIsTorchOn);
                    if (!res) {
                        mIsTorchOn = !mIsTorchOn;
                    }
                    updateTorchButtonText();
                }
            }
        });

        mCameraSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnEventClickedListener != null) {
                    mOnEventClickedListener.onCameraSwitchClicked();
                }
            }
        });

        mCaptureFrameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnEventClickedListener != null) {
                    mOnEventClickedListener.onCaptureFrameClicked();
                }
            }
        });

        mEncodingOrientationSwitcherBtn = (Button) mFragmentView.findViewById(R.id.orientation_btn);
        mEncodingOrientationSwitcherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnEventClickedListener != null) {
                    mIsEncOrientationPort = !mIsEncOrientationPort;
                    boolean res = mOnEventClickedListener.onOrientationChanged(mIsEncOrientationPort);
                    if (!res) {
                        mIsEncOrientationPort = !mIsEncOrientationPort;
                    }
                    updateOrientationBtnText();
                }
            }
        });

        mBeautyLevelSeekBar = mFragmentView.findViewById(R.id.beauty_level_seekBar);
        mBeautyLevelSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mOnEventClickedListener != null) {
                    mOnEventClickedListener.onFaceBeautyProgressChanged(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mControlViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsControlViewVisible = !mIsControlViewVisible;
                mControlViewBtn.setText(mIsControlViewVisible ? getString(R.string.invisible_control_view) : getString(R.string.visible_control_view));
                mControlView.setVisibility(mIsControlViewVisible ? View.VISIBLE : View.GONE);
            }
        });

        // AudioMix panel
        Button mixPanelBtn = mFragmentView.findViewById(R.id.mix_panel_btn);
        mixPanelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View panel = mFragmentView.findViewById(R.id.mix_panel);
                panel.setVisibility(panel.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });

        mMixProgress = mFragmentView.findViewById(R.id.mix_progress);
        mMixProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mOnEventClickedListener != null) {
                    mOnEventClickedListener.onAudioMixPositionChanged(1.0f * seekBar.getProgress() / seekBar.getMax());
                }
            }
        });

        SeekBar mixVolume = mFragmentView.findViewById(R.id.mix_volume);
        mixVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mOnEventClickedListener != null) {
                    mOnEventClickedListener.onAudioMixVolumeChanged(1.0f * seekBar.getProgress() / seekBar.getMax());
                }
            }
        });

        Button mixFileBtn = mFragmentView.findViewById(R.id.mix_file_btn);
        mixFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnEventClickedListener != null) {
                    mOnEventClickedListener.onAudioMixFileSelectionClicked();
                }
            }
        });

        mMixToggleBtn = mFragmentView.findViewById(R.id.mix_btn);
        mMixToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnEventClickedListener != null) {
                    mOnEventClickedListener.onAudioMixControllerClicked();
                }
            }
        });

        Button mixStopBtn = mFragmentView.findViewById(R.id.mix_stop_btn);
        mixStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnEventClickedListener != null) {
                    mOnEventClickedListener.onAudioMixStopClicked();
                }
            }
        });

        Button playbackToggleBtn = mFragmentView.findViewById(R.id.playback_btn);
        playbackToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnEventClickedListener != null) {
                    mOnEventClickedListener.onAudioMixPlaybackClicked();
                }
            }
        });

        CheckBox loopCheckBox = mFragmentView.findViewById(R.id.loop_btn);
        loopCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mOnEventClickedListener != null) {
                    mOnEventClickedListener.onAudioMixLoopEnabled(isChecked);
                }
            }
        });

        return mFragmentView;
    }

    private void updateFBButtonText() {
        if (mFaceBeautyBtn != null) {
            mFaceBeautyBtn.setText(mIsNeedFB ? getString(R.string.face_beauty_off) : getString(R.string.face_beauty_on));
        }
    }

    private void updateMuteButtonText() {
        if (mMuteButton != null) {
            mMuteButton.setText(mIsNeedMute ? getString(R.string.unmute_audio) : getString(R.string.mute_audio));
        }
    }

    private void updateTorchButtonText() {
        if (mTorchBtn != null) {
            mTorchBtn.setText(mIsTorchOn ? getString(R.string.flash_light_off) : getString(R.string.flash_light_on));
        }
    }

    private void updateOrientationBtnText() {
        if (mEncodingOrientationSwitcherBtn != null) {
            mEncodingOrientationSwitcherBtn.setText(mIsEncOrientationPort
                    ? getString(R.string.land_orientation_streaming)
                    : getString(R.string.port_orientation_streaming));
        }
    }
}
