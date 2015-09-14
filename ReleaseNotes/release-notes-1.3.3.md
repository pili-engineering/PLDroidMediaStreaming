# PLDroidCameraStreaming Release Notes for 1.3.3

## 简介
PLDroidCameraStreaming 是为 Android 开发者提供的 RTMP 直播推流 SDK

## 记录

### 推流 SDK
  - 发布 pldroid-camera-streaming-1.3.3.jar
  - 删除 arm64-v8a/libpldroid_ffmpegbridge.so 以及 armeabi-v7a/libpldroid_ffmpegbridge.so
  - 新增 armeabi 支持
  - 新增 arm64-v8a/libpldroid_streaming_core.so, armeabi-v7a/libpldroid_streaming_core.so 和 armeabi/libpldroid_streaming_core.so
  - 体积裁剪数十倍，动态链接库裁剪至 69KB
  - 完全移除 FFmpeg 依赖
  - 修复推流过程中，切换前后置断流问题
  - 修复自适应码率过程中，切换 quality 断流问题
  - 修复前后置切换概率性 crash 问题
  - 新增 `STATE.DISCONNECTED` 状态
