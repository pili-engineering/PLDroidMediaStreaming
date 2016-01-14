# PLDroidCameraStreaming Release Notes for 1.4.6

## 简介
PLDroidCameraStreaming 是为 Android 开发者提供的 RTMP 直播推流 SDK

## 记录

### 推流 SDK
  - 发布 pldroid-camera-streaming-1.4.6.jar
  - 更新 libpldroid_streaming_core.so，libpldroid_streaming_aac_encoder.so 和 libpldroid_streaming_h264_encoder.so
  - 提升软编编码帧率
  - 优化推流过程中前后置摄像头切换体验
  - 新增 happydns 支持，并提供 setDnsManager API，用户可自定义 DnsManager
  - 新增 StreamStatus 回调，实现 StreamStatusCallback 获取音视频帧率和码率
  - 新增 setRecordingHint API，可实现高帧率推流
  - 修复推流过程中，特殊操作后，推流无图像问题
  - 修复推流过程中，HOME 键退出，再次启动 app，无法切换 camera 问题
  - 修复部分机型音画不同步，包括切换前后置
  - 修复推流过程中，概率性 crash 问题

### Demo
  - 更新 demo 样例代码
