# PLDroidCameraStreaming Release Notes for 1.5.1

## 简介
PLDroidCameraStreaming 是为 Android 开发者提供的 RTMP 直播推流 SDK

## 记录

### 推流 SDK
  - 发布 pldroid-camera-streaming-1.5.1.jar
  - 更新 libpldroid_streaming_core.so 和 libpldroid_streaming_h264_encoder.so
  - 新增蓝牙麦克风支持
    - 新增 `MicrophoneStreamingSetting` 类
    - 新增 `CameraStreamingManager#prepare(CameraStreamingSetting, MicrophoneStreamingSetting, StreamingProfile)` 方法
  - 优化启用／关闭输入法弹框导致的屏闪现象
  - 修复部分机型手动对焦引起的 crash 问题
  - 修复部分机型推流过程中概率性 crash 问题
  - 修复部分机型频繁切换输入法导致黑屏问题
  - 修复特殊机型硬编音画不同步问题

### Demo
  - 更新 demo 样例代码
    - 新增 `ACCESS_NETWORK_STATE` 和 `MODIFY_AUDIO_SETTINGS` permission
