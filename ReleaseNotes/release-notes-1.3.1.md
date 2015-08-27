# PLDroidCameraStreaming Release Notes for 1.3.1

## 简介
PLDroidCameraStreaming 是为 Android 开发者提供的 RTMP 直播推流 SDK

## 记录

### 推流 SDK
  - 发布 pldroid-camera-streaming-1.3.1.jar
  - 增加 arm64-v8a 支持，新增 arm64-v8a/libpldroid_ffmpegbridge.so
  - 更新 armeabi-v7a/libpldroid_ffmpegbridge.so
  - 新增切换 `Stream` 接口：setStreamingProfile
  - 新增 `setLocalFileAbsolutePath` 接口
  - 修复横屏下，经过特殊操作，Camera 预览显示异常的问题
