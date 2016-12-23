# PLDroidMediaStreaming Release Notes for 2.1.2

本次更新:

## 版本

- 发布 pldroid-media-streaming-2.1.2.jar
- 更新 libpldroid_streaming_core.so
- 更新 libpldroid_mmprocessing.so
- 更新 libpldroid_streaming_aac_encoder.so
- 更新 libpldroid_streaming_h264_encoder.so

## 功能

- 新增动态 mirror 功能
    - 动态改变本地预览镜像
    ```
    /**
     * change mirror of local preview
     * @param mirror
     * @return
     */
    public boolean setPreviewMirror(boolean mirror)
    ```
    - 动态改变推流镜像
    ```
    /**
     * change mirror of streaming
     * @param mirror
     * @return
     */
    public boolean setEncodingMirror(boolean mirror)
    ```
- 在推流的 metadata 中添加了系统信息
- 增加录屏请求被拒绝后的回调
    -  `在 onStateChanged 新增 StreamingState REQUEST_SCREEN_CAPTURING_FAIL`

## 缺陷

- 修复在部分机型上预览画面太暗的问题
- 修复在部分机型上切换摄像头画面异常和闪屏问题
- 修复在部分机型上推流视频帧率为 0 的问题
- 修复部分外部美颜 OpenGL 报错的问题
- 修复硬编带来的各种稳定性问题
- 修复弱网优化效果不明显的问题
- 修复部分场景下停止推流产生的 crash 问题
- 修复 onPreviewSizeSelected 不回调问题
- 修复部分机型获取 Camera 预览帧率可能崩溃的问题

## 更新注意事项

- 请在 build.gradle 中更新如下配置

```
compile 'com.qiniu.pili:pili-android-qos:0.8.16'
```

- 请在 AndroidManifest 里面加入如下权限:

```
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```