package com.qiniu.pili.droid.streaming.demo.fragment;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.qiniu.pili.droid.streaming.AVCodecType;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.WatermarkSetting;
import com.qiniu.pili.droid.streaming.demo.R;
import com.qiniu.pili.droid.streaming.demo.plain.EncodingConfig;

import java.io.File;

import static com.qiniu.pili.droid.streaming.StreamingProfile.AUDIO_QUALITY_HIGH1;
import static com.qiniu.pili.droid.streaming.StreamingProfile.AUDIO_QUALITY_HIGH2;
import static com.qiniu.pili.droid.streaming.StreamingProfile.AUDIO_QUALITY_LOW1;
import static com.qiniu.pili.droid.streaming.StreamingProfile.AUDIO_QUALITY_LOW2;
import static com.qiniu.pili.droid.streaming.StreamingProfile.AUDIO_QUALITY_MEDIUM1;
import static com.qiniu.pili.droid.streaming.StreamingProfile.AUDIO_QUALITY_MEDIUM2;
import static com.qiniu.pili.droid.streaming.StreamingProfile.VIDEO_QUALITY_HIGH1;
import static com.qiniu.pili.droid.streaming.StreamingProfile.VIDEO_QUALITY_HIGH2;
import static com.qiniu.pili.droid.streaming.StreamingProfile.VIDEO_QUALITY_HIGH3;
import static com.qiniu.pili.droid.streaming.StreamingProfile.VIDEO_QUALITY_LOW1;
import static com.qiniu.pili.droid.streaming.StreamingProfile.VIDEO_QUALITY_LOW2;
import static com.qiniu.pili.droid.streaming.StreamingProfile.VIDEO_QUALITY_LOW3;
import static com.qiniu.pili.droid.streaming.StreamingProfile.VIDEO_QUALITY_MEDIUM1;
import static com.qiniu.pili.droid.streaming.StreamingProfile.VIDEO_QUALITY_MEDIUM2;
import static com.qiniu.pili.droid.streaming.StreamingProfile.VIDEO_QUALITY_MEDIUM3;

public class EncodingConfigFragment extends ConfigFragment {
    private static final String[] VIDEO_SIZE_PRESETS = {
            "240p(320x240 (4:3), 424x240 (16:9))",
            "480p(640x480 (4:3), 848x480 (16:9))",
            "544p(720x544 (4:3), 960x544 (16:9))",
            "720p(960x720 (4:3), 1280x720 (16:9))",
            "1080p(1440x1080 (4:3), 1920x1080 (16:9))"
    };

    private static final String[] VIDEO_QUALITY_PRESETS = {
            "LOW1(FPS:12, Bitrate:150kbps)",
            "LOW2(FPS:15, Bitrate:264kbps)",
            "LOW3(FPS:15, Bitrate:350kbps)",
            "MEDIUM1(FPS:30, Bitrate:512kbps)",
            "MEDIUM2(FPS:30, Bitrate:800kbps)",
            "MEDIUM3(FPS:30, Bitrate:1000kbps)",
            "HIGH1(FPS:30, Bitrate:1200kbps)",
            "HIGH2(FPS:30, Bitrate:1500kbps)",
            "HIGH3(FPS:30, Bitrate:2000kbps)"
    };

    private static final int[] VIDEO_QUALITY_PRESETS_MAPPING = {
            VIDEO_QUALITY_LOW1,
            VIDEO_QUALITY_LOW2,
            VIDEO_QUALITY_LOW3,
            VIDEO_QUALITY_MEDIUM1,
            VIDEO_QUALITY_MEDIUM2,
            VIDEO_QUALITY_MEDIUM3,
            VIDEO_QUALITY_HIGH1,
            VIDEO_QUALITY_HIGH2,
            VIDEO_QUALITY_HIGH3
    };

    private static final String[] VIDEO_QUALITY_PROFILES = {
            "HIGH",
            "MAIN",
            "BASELINE"
    };

    private static final StreamingProfile.H264Profile[] VIDEO_QUALITY_PROFILES_MAPPING = {
            StreamingProfile.H264Profile.HIGH,
            StreamingProfile.H264Profile.MAIN,
            StreamingProfile.H264Profile.BASELINE
    };

    private static final String[] AUDIO_QUALITY_PRESETS = {
            "LOW1(SampleRate:44.1kHZ, Bitrate:18kbps)",
            "LOW2(SampleRate:44.1kHZ, Bitrate:24kbps)",
            "MEDIUM1(SampleRate:44.1kHZ, Bitrate:32kbps)",
            "MEDIUM2(SampleRate:44.1kHZ, Bitrate:48kbps)",
            "HIGH1(SampleRate:44.1kHZ, Bitrate:96kbps)",
            "HIGH2(SampleRate:44.1kHZ, Bitrate:128kbps)"
    };

    private static final int[] AUDIO_QUALITY_PRESETS_MAPPING = {
            AUDIO_QUALITY_LOW1,
            AUDIO_QUALITY_LOW2,
            AUDIO_QUALITY_MEDIUM1,
            AUDIO_QUALITY_MEDIUM2,
            AUDIO_QUALITY_HIGH1,
            AUDIO_QUALITY_HIGH2
    };

    private static final String[] WATERMARK_SIZE_PRESETS = {
            "SMALL",
            "MEDIUM",
            "LARGE",
    };

    private static final WatermarkSetting.WATERMARK_SIZE[] WATERMARK_SIZE_PRESETS_MAPPING = {
            WatermarkSetting.WATERMARK_SIZE.SMALL,
            WatermarkSetting.WATERMARK_SIZE.MEDIUM,
            WatermarkSetting.WATERMARK_SIZE.LARGE
    };

    private static final String[] WATERMARK_LOCATION_PRESETS = {
            "NORTH-WEST",
            "NORTH-EAST",
            "SOUTH-EAST",
            "SOUTH-WEST",
    };

    private static final WatermarkSetting.WATERMARK_LOCATION[] WATERMARK_LOCATION_PRESETS_MAPPING = {
            WatermarkSetting.WATERMARK_LOCATION.NORTH_WEST,
            WatermarkSetting.WATERMARK_LOCATION.NORTH_EAST,
            WatermarkSetting.WATERMARK_LOCATION.SOUTH_EAST,
            WatermarkSetting.WATERMARK_LOCATION.SOUTH_WEST
    };

    private static final String[] YUV_FILTER_MODE = {
            "NONE",
            "Linear",
            "Bilinear",
            "Box",
    };

    private String mWatermarkFilePath;
    private String mPictureFilePath;

    public void forceCustomVideoEncodingSize(boolean enable) {
        getView().findViewById(R.id.video_size_preset).setEnabled(!enable);
        if (enable) {
            ((RadioButton) getView().findViewById(R.id.video_size_custom)).setChecked(true);
        }
    }

    public void enableAudioOnly(boolean enable) {
        getView().findViewById(R.id.video_config_panel).setVisibility(enable ? View.GONE : View.VISIBLE);
    }

    public void enableWatermark(boolean enable) {
        View watermarkConfigPanel = getView().findViewById(R.id.watermark_panel);
        watermarkConfigPanel.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    public void enablePictureStreaming(boolean enable) {
        View pictureConfigPanel = getView().findViewById(R.id.picture_panel);
        pictureConfigPanel.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    private EncodingConfig buildEncodingConfig() {
        EncodingConfig encodingConfig = new EncodingConfig();

        View root = getView();
        // if audio only
        encodingConfig.mIsAudioOnly = root.findViewById(R.id.video_config_panel).getVisibility() == View.GONE;
        // set codec type
        boolean codecSW = ((RadioButton) root.findViewById(R.id.encoding_sw)).isChecked();
        boolean codecHW = ((RadioButton) root.findViewById(R.id.encoding_hw)).isChecked();
        if (encodingConfig.mIsAudioOnly) {
            encodingConfig.mCodecType = codecSW ? AVCodecType.SW_AUDIO_CODEC : AVCodecType.HW_AUDIO_CODEC;
        } else {
            encodingConfig.mCodecType = codecSW ? AVCodecType.SW_VIDEO_WITH_SW_AUDIO_CODEC :
                    codecHW? AVCodecType.HW_VIDEO_SURFACE_AS_INPUT_WITH_HW_AUDIO_CODEC :
                            AVCodecType.HW_VIDEO_YUV_AS_INPUT_WITH_HW_AUDIO_CODEC;
        }
        // set video if not audio only
        if (!encodingConfig.mIsAudioOnly) {
            // quality
            encodingConfig.mIsVideoQualityPreset = ((RadioButton) root.findViewById(R.id.video_quality_preset)).isChecked();
            if (encodingConfig.mIsVideoQualityPreset) {
                Spinner presetSpinner = (Spinner) root.findViewById(R.id.video_quality_presets);
                encodingConfig.mVideoQualityPreset = VIDEO_QUALITY_PRESETS_MAPPING[presetSpinner.getSelectedItemPosition()];
            } else {
                encodingConfig.mVideoQualityCustomFPS = Integer.parseInt(((EditText) root.findViewById(R.id.video_quality_custom_fps)).getText().toString());
                encodingConfig.mVideoQualityCustomBitrate = Integer.parseInt(((EditText) root.findViewById(R.id.video_quality_custom_bitrate)).getText().toString());
                encodingConfig.mVideoQualityCustomMaxKeyFrameInterval = Integer.parseInt(((EditText) root.findViewById(R.id.video_quality_custom_max_key_frame_interval)).getText().toString());
                Spinner profileSpinner = (Spinner) root.findViewById(R.id.video_quality_custom_profile);
                encodingConfig.mVideoQualityCustomProfile = VIDEO_QUALITY_PROFILES_MAPPING[profileSpinner.getSelectedItemPosition()];
            }

            // size
            encodingConfig.mIsVideoSizePreset = ((RadioButton) root.findViewById(R.id.video_size_preset)).isChecked();
            if (encodingConfig.mIsVideoSizePreset) {
                Spinner presetSpinner = (Spinner) root.findViewById(R.id.video_size_presets);
                encodingConfig.mVideoSizePreset = presetSpinner.getSelectedItemPosition();
            } else {
                encodingConfig.mVideoSizeCustomWidth = Integer.parseInt(((EditText) root.findViewById(R.id.video_size_custom_width)).getText().toString());
                encodingConfig.mVideoSizeCustomHeight = Integer.parseInt(((EditText) root.findViewById(R.id.video_size_custom_height)).getText().toString());
            }

            // misc
            encodingConfig.mVideoOrientationPortrait = ((RadioButton) root.findViewById(R.id.orientation_portrait)).isChecked();
            encodingConfig.mVideoRateControlQuality = ((RadioButton) root.findViewById(R.id.rate_control_quality)).isChecked();
            boolean isAdaptiveBitrate = ((RadioButton) root.findViewById(R.id.bitrate_auto)).isChecked();
            boolean isManualBitrate = ((RadioButton) root.findViewById(R.id.bitrate_manual)).isChecked();
            encodingConfig.mBitrateAdjustMode = isAdaptiveBitrate ? StreamingProfile.BitrateAdjustMode.Auto :
                            (isManualBitrate ? StreamingProfile.BitrateAdjustMode.Manual : StreamingProfile.BitrateAdjustMode.Disable);
            if (isAdaptiveBitrate) {
                encodingConfig.mAdaptiveBitrateMin = Integer.parseInt(((EditText) root.findViewById(R.id.auto_bitrate_min)).getText().toString());
                encodingConfig.mAdaptiveBitrateMax = Integer.parseInt(((EditText) root.findViewById(R.id.auto_bitrate_max)).getText().toString());
            }

            encodingConfig.mVideoFPSControl = ((CheckBox) root.findViewById(R.id.fps_control)).isChecked();

            // YUV filter mode
            Spinner yuvFilterModeSpinner = (Spinner) root.findViewById(R.id.yuv_filter_mode_set);
            encodingConfig.mYuvFilterMode  = StreamingProfile.YuvFilterMode.values()[yuvFilterModeSpinner.getSelectedItemPosition()];

            // watermark
            CheckBox cbWatermarkControl = (CheckBox) getView().findViewById(R.id.watermark_control);
            encodingConfig.mIsWatermarkEnabled = cbWatermarkControl.isChecked();
            LinearLayout layoutWatermarkPanel = (LinearLayout) getView().findViewById(R.id.watermark_panel);
            layoutWatermarkPanel.setVisibility(encodingConfig.mIsWatermarkEnabled? View.VISIBLE : View.GONE);
            if (getView().findViewById(R.id.watermark_panel).getVisibility() == View.VISIBLE) {
                encodingConfig.mWatermarkAlpha = Integer.parseInt(((EditText) root.findViewById(R.id.watermark_alpha)).getText().toString());
                Spinner sizeSpinner = (Spinner) root.findViewById(R.id.watermark_size_presets);
                encodingConfig.mWatermarkSize = WATERMARK_SIZE_PRESETS_MAPPING[sizeSpinner.getSelectedItemPosition()];
                encodingConfig.mIsWatermarkLocationPreset = ((RadioButton) root.findViewById(R.id.watermark_location_preset)).isChecked();
                if (encodingConfig.mIsWatermarkLocationPreset) {
                    Spinner presetSpinner = (Spinner) root.findViewById(R.id.watermark_location_presets);
                    encodingConfig.mWatermarkLocationPreset = WATERMARK_LOCATION_PRESETS_MAPPING[presetSpinner.getSelectedItemPosition()];
                } else {
                    encodingConfig.mWatermarkLocationCustomX = Float.parseFloat(((EditText) root.findViewById(R.id.watermark_location_custom_x)).getText().toString());
                    encodingConfig.mWatermarkLocationCustomY = Float.parseFloat(((EditText) root.findViewById(R.id.watermark_location_custom_y)).getText().toString());
                }
            }

            // picture streaming
            encodingConfig.mIsPictureStreamingEnabled = ((CheckBox) getView().findViewById(R.id.pic_streaming_control)).isChecked();
            if (mPictureFilePath != null) {
                encodingConfig.mPictureStreamingFilePath = mPictureFilePath;
            }
        }
        // set audio
        encodingConfig.mIsAudioQualityPreset = ((RadioButton) root.findViewById(R.id.audio_quality_preset)).isChecked();
        if (encodingConfig.mIsAudioQualityPreset) {
            Spinner presetSpinner = (Spinner) root.findViewById(R.id.audio_quality_presets);
            encodingConfig.mAudioQualityPreset = AUDIO_QUALITY_PRESETS_MAPPING[presetSpinner.getSelectedItemPosition()];
        } else {
            encodingConfig.mAudioQualityCustomSampleRate = Integer.parseInt(((EditText) root.findViewById(R.id.audio_quality_custom_sample_rate)).getText().toString());
            encodingConfig.mAudioQualityCustomBitrate = Integer.parseInt(((EditText) root.findViewById(R.id.audio_quality_custom_bitrate)).getText().toString());
        }

        return encodingConfig;
    }

    @Override
    public Intent getIntent() {
        Intent data = new Intent();
        data.putExtra("EncodingConfig", buildEncodingConfig());
        return data;
    }

    private void initVideoQualityPanel(final View root) {
        final Spinner presetSpinner = (Spinner) root.findViewById(R.id.video_quality_presets);
        ArrayAdapter<String> data = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, VIDEO_QUALITY_PRESETS);
        presetSpinner.setAdapter(data);
        presetSpinner.setSelection(4);

        Spinner profileSpinner = (Spinner) root.findViewById(R.id.video_quality_custom_profile);
        data = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, VIDEO_QUALITY_PROFILES);
        profileSpinner.setAdapter(data);
        profileSpinner.setSelection(0);

        RadioGroup videoRadioGroup = (RadioGroup) root.findViewById(R.id.video_quality_radio_group);
        videoRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                View customPanel = root.findViewById(R.id.video_quality_custom_panel);
                presetSpinner.setVisibility(checkedId == R.id.video_quality_preset ? View.VISIBLE : View.GONE);
                customPanel.setVisibility(checkedId == R.id.video_quality_preset ? View.GONE : View.VISIBLE);
            }
        });
    }

    private void initVideoSizePanel(final View root) {
        final Spinner presetSpinner = (Spinner) root.findViewById(R.id.video_size_presets);
        ArrayAdapter<String> data = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, VIDEO_SIZE_PRESETS);
        presetSpinner.setAdapter(data);
        presetSpinner.setSelection(1);

        RadioGroup videoRadioGroup = (RadioGroup) root.findViewById(R.id.video_size_radio_group);
        videoRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                View customPanel = root.findViewById(R.id.video_size_custom_panel);
                presetSpinner.setVisibility(checkedId == R.id.video_size_preset ? View.VISIBLE : View.GONE);
                customPanel.setVisibility(checkedId == R.id.video_size_preset ? View.GONE : View.VISIBLE);
            }
        });
    }

    private void initAudioQualityPanel(final View root) {
        final Spinner presetSpinner = (Spinner) root.findViewById(R.id.audio_quality_presets);
        ArrayAdapter<String> data = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, AUDIO_QUALITY_PRESETS);
        presetSpinner.setAdapter(data);
        presetSpinner.setSelection(3);

        RadioGroup audioGroup = (RadioGroup) root.findViewById(R.id.audio_quality_radio_group);
        audioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                View customPanel = root.findViewById(R.id.audio_quality_custom_panel);
                presetSpinner.setVisibility(checkedId == R.id.audio_quality_preset ? View.VISIBLE : View.GONE);
                customPanel.setVisibility(checkedId == R.id.audio_quality_preset ? View.GONE : View.VISIBLE);
            }
        });
    }

    private void initWatermarkPanel(final View root) {
        Spinner presetSpinner = (Spinner) root.findViewById(R.id.watermark_size_presets);
        ArrayAdapter<String> data = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, WATERMARK_SIZE_PRESETS);
        presetSpinner.setAdapter(data);
        presetSpinner.setSelection(1);

        final Spinner locationSpinner = (Spinner) root.findViewById(R.id.watermark_location_presets);
        data = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, WATERMARK_LOCATION_PRESETS);
        locationSpinner.setAdapter(data);
        locationSpinner.setSelection(2);

        RadioGroup watermarkGroup = (RadioGroup) root.findViewById(R.id.watermark_location_radio_group);
        watermarkGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                View customPanel = root.findViewById(R.id.watermark_location_custom_panel);
                locationSpinner.setVisibility(checkedId == R.id.watermark_location_preset ? View.VISIBLE : View.GONE);
                customPanel.setVisibility(checkedId == R.id.watermark_location_preset ? View.GONE : View.VISIBLE);
            }
        });
    }

    private void initPicturePanel(final View root) {
        ((ImageView) root.findViewById(R.id.picture_preview)).setImageResource(R.drawable.pause_publish);
        root.findViewById(R.id.picture_preview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogProperties properties = new DialogProperties();
                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.selection_type = DialogConfigs.FILE_SELECT;
                properties.root = new File(DialogConfigs.STORAGE_DIR);
                properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
                properties.extensions = new String[]{"jpg", "png"};

                FilePickerDialog dialog = new FilePickerDialog(getActivity(), properties);
                dialog.setTitle("Select a File");
                dialog.setDialogSelectionListener(new DialogSelectionListener() {
                    @Override
                    public void onSelectedFilePaths(String[] files) {
                        ImageView imageView = ((ImageView) getView().findViewById(R.id.picture_preview));
                        if (files != null && files.length > 0){
                            mPictureFilePath = files[0];
                            imageView.setImageBitmap(BitmapFactory.decodeFile(mPictureFilePath));
                        } else {
                            mPictureFilePath = null;
                            imageView.setImageResource(R.drawable.pause_publish);
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    private void initBitrateCtrlPanel(final View root) {
        RadioGroup bitrateRadioGroup = (RadioGroup) root.findViewById(R.id.bitrate_control_group);
        bitrateRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                LinearLayout bitrateLayout = (LinearLayout) root.findViewById(R.id.auto_bitrate_range);
                bitrateLayout.setVisibility(checkedId == R.id.bitrate_auto ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void initYuvFilterModePanel(final View root) {
        final Spinner yuvFilterModeSpinner = (Spinner) root.findViewById(R.id.yuv_filter_mode_set);
        ArrayAdapter<String> data = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, YUV_FILTER_MODE);
        yuvFilterModeSpinner.setAdapter(data);
        yuvFilterModeSpinner.setSelection(1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_encoding_config, null);
        initVideoQualityPanel(root);
        initVideoSizePanel(root);
        initAudioQualityPanel(root);
        initWatermarkPanel(root);
        initPicturePanel(root);
        initBitrateCtrlPanel(root);
        initYuvFilterModePanel(root);
        return root;
    }

}
