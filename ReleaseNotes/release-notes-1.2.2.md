# PLDroidCameraStreaming Release Notes for 1.2.2

## 简介
PLDroidCameraStreaming 是为 Android 开发者提供的 RTMP 直播推流 SDK

## 记录

### 推流 SDK
* 发布 pldroid-camera-streaming-1.2.2.jar
* 更新 libpldroid_ffmpegbridge.so
* 修复概率性的 crash 问题
* 添加 `STATE.CONNECTION_TIMEOUT` 状态：streaming 状态下，若网络断开或连接超时，该状态将会被回调
* 修复部分机型因连接错误而导致屏幕 Hang 住

### Demo
* 在 UI 层对点击事件加入保护逻辑，避免快速点击导致应用 crash
