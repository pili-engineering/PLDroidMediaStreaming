package com.qiniu.pili.droid.streaming.demo.plain;

import com.qiniu.pili.droid.streaming.CameraStreamingSetting;

import java.io.Serializable;

/**
 * 保存所选择的相机配置信息，仅在 demo 上用来保存配置信息
 * 此类为非必须的，您可以根据您的产品定义自行决定配置信息的保存方式
 */
public class CameraConfig implements Serializable {
    public boolean mFrontFacing;
    public CameraStreamingSetting.PREVIEW_SIZE_LEVEL mSizeLevel;
    public CameraStreamingSetting.PREVIEW_SIZE_RATIO mSizeRatio;
    public String mFocusMode;
    public boolean mIsFaceBeautyEnabled;
    public boolean mIsCustomFaceBeauty = false;
    public boolean mContinuousAutoFocus;
    public boolean mPreviewMirror;
    public boolean mEncodingMirror;
}
