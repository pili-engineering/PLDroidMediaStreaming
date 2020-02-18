# PLDroidMediaStreaming Release Notes for 2.1.2.3

本次更新:

## 版本

- 发布 pldroid-media-streaming-2.1.2.3.jar

## 功能

-   `MediaStreamingManager` 新增动态码率上下限配置接口

    ```
    public StreamingProfile setVideoAdaptiveBitrateRange(int minBitrate, int maxBitrate);
    ```

-   `ScreenStreamingManager` 新增设置推流状态回调接口

          ​```
          public void setStreamingStateListener(StreamingStateChangedListener listener)
          ​```

-   `MediaStreamingManager` 新增是否支持 `PreviewAppearance` 模式的检测接口

          ​```
          public static boolean isSupportPreviewAppearance();
          ​```

-   `CameraStreamingSetting` 新增是否支持 `improved` 模式的接口

          ​```
          public boolean isSupportCameraSourceImproved();
          ​```

## 缺陷

- 修复录屏推流过程中偶现的部分崩溃问题
- 修复推流音频采集过程中低概率的数组越界异常
- 修复自定义码率配置模式下动态码率不生效的问题
- 修复部分 Android 机型摄像头预览画面太暗的问题

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