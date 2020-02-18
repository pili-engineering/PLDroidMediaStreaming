# PLDroidMediaStreaming Release Notes for 2.1.1.3

#### 版本
  - 发布 pldroid-media-streaming-2.1.1.3.jar
  - 发布 libpldroid_streaming_core.so

#### 功能
  - 在 meta_info 中添加了系统信息

#### 更新注意事项
  - 在请在 AndroidManifest 里面加入如下权限:

```
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

