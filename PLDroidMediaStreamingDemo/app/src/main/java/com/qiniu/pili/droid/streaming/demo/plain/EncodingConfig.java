package com.qiniu.pili.droid.streaming.demo.plain;

import com.qiniu.pili.droid.streaming.AVCodecType;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.WatermarkSetting;

import java.io.Serializable;

public class EncodingConfig implements Serializable {
    public AVCodecType mCodecType;
    public boolean mIsAudioOnly;

    public boolean mIsVideoQualityPreset;
    public int mVideoQualityPreset;
    public int mVideoQualityCustomFPS;
    public int mVideoQualityCustomBitrate;
    public int mVideoQualityCustomMaxKeyFrameInterval;

    public boolean mIsVideoSizePreset;
    public int mVideoSizePreset;

    public int mVideoSizeCustomWidth;
    public int mVideoSizeCustomHeight;

    public boolean mVideoOrientationPortrait;

    public boolean mVideoRateControlQuality;

    public StreamingProfile.BitrateAdjustMode mBitrateAdjustMode;

    public boolean mVideoFPSControl;

    public boolean mIsWatermarkEnabled;
    public int mWatermarkAlpha;
    public WatermarkSetting.WATERMARK_SIZE mWatermarkSize;
    public boolean mIsWatermarkLocationPreset;
    public WatermarkSetting.WATERMARK_LOCATION mWatermarkLocationPreset;
    public float mWatermarkLocationCustomX;
    public float mWatermarkLocationCustomY;

    public boolean mIsPictureStreamingEnabled;
    public String mPictureStreamingFilePath;

    public boolean mIsAudioQualityPreset;
    public int mAudioQualityPreset;
    public int mAudioQualityCustomSampleRate;
    public int mAudioQualityCustomBitrate;
}
