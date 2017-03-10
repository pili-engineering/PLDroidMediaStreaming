# PLDroidMediaStreaming Release Notes for 2.1.3

本次更新:

## 版本

- 发布 pldroid-media-streaming-2.1.3.jar
- 更新 libpldroid_streaming_core.so

## 功能

-   `StreamingProfile` 新增动态码率上下限配置接口

	```
    /**
     * 码率范围：150 * 1024 ～ 2000 * 1024
     */
    public StreamingProfile setVideoAdaptiveBitrateRange(int minBitrate, int maxBitrate);
	```

-   `ScreenStreamingManager` 新增设置录屏推流状态回调的接口

	```
   public void setStreamingStateListener(StreamingStateChangedListener listener)
	```

-   `MediaStreamingManager` 新增是否支持 `PreviewAppearance` 模式的检测接口

	```
   public static boolean isSupportPreviewAppearance();
	```

-   `CameraStreamingSetting` 新增是否支持 `improved` 模式的接口

	```
   	public boolean isSupportCameraSourceImproved();
	```

-   `StreamingProfile` 新增自定义动态码率相关阈值接口

	```
    /**
     * Custom adaptive bitrate dependent threshold
     *
     * @param tcpSendTimeAdjustThreshold	发送数据包耗时的阈值，自适应码率调整的临界值，单位：毫秒，默认：3，范围: (1-10)
     * @param fpsAdjustThreshold	帧率阈值，自适应码率调整的临界值，单位：fps，默认：15, 范围: (1-20)
     *
     * @return this
     * */
    public StreamingProfile setAdaptiveBitrateAdjustThreshold(int tcpSendTimeAdjustThreshold, int fpsAdjustThreshold)
	```

## 缺陷

- 修复录屏推流过程中偶现的部分崩溃问题
- 修复推流音频采集过程中低概率的数组越界异常
- 修复部分 Android 机型摄像头预览画面太暗的问题
- 修复推流 AVCC 嵌套问题
- 修复自定义推流帧率配置不生效的问题

## 更新注意事项

- 请在 build.gradle 中更新如下配置

```
compile 'com.qiniu.pili:pili-android-qos:0.8.18'
```

- 请在 AndroidManifest 里面加入如下权限:

```
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```