package com.qiniu.pili.droid.streaming.demo.plain;

import com.qiniu.pili.droid.streaming.CameraStreamingSetting;

import java.io.Serializable;

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
