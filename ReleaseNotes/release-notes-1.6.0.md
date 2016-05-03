# PLDroidCameraStreaming Release Notes for 1.6.0

## 简介
PLDroidCameraStreaming 是为 Android 开发者提供的 RTMP 直播推流 SDK

## 记录

### 推流 SDK
  - 发布 pldroid-camera-streaming-1.6.0.jar
  - 更新 libpldroid_streaming_core.so 和 libpldroid_streaming_h264_encoder.so 
  - 新增 mirror 支持：`CameraStreamingSetting#setFrontCameraMirror`
  - 新增 `StreamingEnv`
  - 修复特殊机型硬编闪屏问题
  - 修复禁播导致的 crash 问题
  - 改善部分机型硬编 tearing 现象
  - 兼容异常输入的情况，并提供回调：`STATE.INVALID_STREAMING_URL`
  - 新增质量上报支持
  - 修复资源泄漏问题
  - 修复特殊机型 crash 问题
  
### Demo
  - 重构 Demo 代码
