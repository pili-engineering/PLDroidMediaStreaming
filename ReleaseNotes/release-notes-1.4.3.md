# PLDroidCameraStreaming Release Notes for 1.4.3

## 简介
PLDroidCameraStreaming 是为 Android 开发者提供的 RTMP 直播推流 SDK

## 记录

### 推流 SDK
  - 发布 pldroid-camera-streaming-1.4.3.jar
  - 更新 libpldroid_streaming_core.so，libpldroid_streaming_aac_encoder.so 和 libpldroid_streaming_h264_encoder.so
  - 新增 `SharedLibraryNameHelper` 绝对路径加载方式
  - 新增 `StreamingSessionListener`，可方便安全地实现重连策略及 Audio 数据获取失败时的策略
  - 新增 `EncodingType` 支持
  - 修复硬编模式下，多次切换前后置摄像头 Crash 问题
  - 修复硬编模式下，部分机型截图 crash 问题
  - 修复 metadata 格式问题
  - 修复软编模式下，推流过程中概率性 crash 问题
  - 修复概率性无视频帧问题

### Demo
  - 更新 demo 展示代码 
