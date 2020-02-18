# PLDroidMediaStreaming Release Notes for 2.1.3.1

本次更新:

## 版本

- 发布 pldroid-media-streaming-2.1.3.1.jar
- 更新了 libpldroid_streaming_core.so


## 功能

- `VideoProfile` 提供 `annexb` 和 `avcc` 格式的配置选项


## 缺陷

- 修复因图片的透明度导致的硬编水印异常
- 修复自定义帧率配置不生效的问题
- 修复部分场景下停止推流低概率偶现的ANR异常

## 更新注意事项

- 请在 build.gradle 中更新如下配置

```
compile 'com.qiniu.pili:pili-android-qos:0.8.20'
```

- 请在 AndroidManifest 里面加入如下权限:

```
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```