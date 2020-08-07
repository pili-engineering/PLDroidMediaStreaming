package com.qiniu.pili.droid.streaming.demo.fragment;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.qiniu.pili.droid.streaming.CameraStreamingSetting;
import com.qiniu.pili.droid.streaming.demo.R;
import com.qiniu.pili.droid.streaming.demo.plain.CameraConfig;
import com.qiniu.pili.droid.streaming.demo.utils.Config;

/**
 * 相机采集配置项 Fragment，仅用作 demo 中获取配置信息，后续在推流初始化时传递给 SDK 内部使用
 * 此 Fragment 为非必须的，您可以根据您的产品定义自行决定配置信息的配置方式
 */
public class CameraConfigFragment extends ConfigFragment {
    private static final String[] PREVIEW_SIZE_LEVEL_PRESETS = {
            "SMALL",
            "MEDIUM",
            "LARGE"
    };

    private static final CameraStreamingSetting.PREVIEW_SIZE_LEVEL[] PREVIEW_SIZE_LEVEL_PRESETS_MAPPING = {
            CameraStreamingSetting.PREVIEW_SIZE_LEVEL.SMALL,
            CameraStreamingSetting.PREVIEW_SIZE_LEVEL.MEDIUM,
            CameraStreamingSetting.PREVIEW_SIZE_LEVEL.LARGE
    };

    private static final String[] PREVIEW_SIZE_RATIO_PRESETS = {
            "4:3",
            "16:9"
    };

    private static final CameraStreamingSetting.PREVIEW_SIZE_RATIO[] PREVIEW_SIZE_RATIO_PRESETS_MAPPING = {
            CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_4_3,
            CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_16_9
    };

    private static final String[] FOCUS_MODE_PRESETS = {
            "AUTO",
            "CONTINUOUS PICTURE",
            "CONTINUOUS VIDEO"
    };

    private static final String[] FOCUS_MODE_PRESETS_MAPPING = {
            Camera.Parameters.FOCUS_MODE_AUTO,
            Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE,
            Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
    };

    @Override
    public Intent getIntent() {
        Intent data = new Intent();
        data.putExtra(Config.NAME_CAMERA_CONFIG, buildCameraConfig());
        return data;
    }

    private CameraConfig buildCameraConfig() {
        CameraConfig cameraConfig = new CameraConfig();
        View root = getView();

        cameraConfig.mFrontFacing = ((RadioButton) root.findViewById(R.id.facing_front)).isChecked();
        Spinner sizeLevelSpinner = (Spinner) root.findViewById(R.id.preview_size_level_spinner);
        cameraConfig.mSizeLevel = PREVIEW_SIZE_LEVEL_PRESETS_MAPPING[sizeLevelSpinner.getSelectedItemPosition()];
        Spinner sizeRatioSpinner = (Spinner) root.findViewById(R.id.preview_size_ratio_spinner);
        cameraConfig.mSizeRatio = PREVIEW_SIZE_RATIO_PRESETS_MAPPING[sizeRatioSpinner.getSelectedItemPosition()];
        Spinner focusModeSpinner = (Spinner) root.findViewById(R.id.focus_mode_spinner);
        cameraConfig.mFocusMode = FOCUS_MODE_PRESETS_MAPPING[focusModeSpinner.getSelectedItemPosition()];
        cameraConfig.mIsFaceBeautyEnabled = ((CheckBox) root.findViewById(R.id.face_beauty)).isChecked();
        cameraConfig.mIsCustomFaceBeauty = ((CheckBox) root.findViewById(R.id.external_face_beauty)).isChecked();
        cameraConfig.mContinuousAutoFocus = ((CheckBox) root.findViewById(R.id.continuous_auto_focus)).isChecked();
        cameraConfig.mPreviewMirror = ((CheckBox) root.findViewById(R.id.preview_mirror)).isChecked();
        cameraConfig.mEncodingMirror = ((CheckBox) root.findViewById(R.id.encoding_mirror)).isChecked();

        return cameraConfig;
    }

    private void initPreviewSizeLevelSpinner(View root) {
        Spinner presetSpinner = (Spinner) root.findViewById(R.id.preview_size_level_spinner);
        ArrayAdapter<String> data = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, PREVIEW_SIZE_LEVEL_PRESETS);
        presetSpinner.setAdapter(data);
        presetSpinner.setSelection(1);
    }

    private void initPreviewSizeRatioSpinner(View root) {
        Spinner presetSpinner = (Spinner) root.findViewById(R.id.preview_size_ratio_spinner);
        ArrayAdapter<String> data = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, PREVIEW_SIZE_RATIO_PRESETS);
        presetSpinner.setAdapter(data);
        presetSpinner.setSelection(1);
    }

    private void initFocusModeSpinner(View root) {
        Spinner presetSpinner = (Spinner) root.findViewById(R.id.focus_mode_spinner);
        ArrayAdapter<String> data = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, FOCUS_MODE_PRESETS);
        presetSpinner.setAdapter(data);
        presetSpinner.setSelection(1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_camera_config, null);
        initPreviewSizeLevelSpinner(root);
        initPreviewSizeRatioSpinner(root);
        initFocusModeSpinner(root);
        return root;
    }
}
