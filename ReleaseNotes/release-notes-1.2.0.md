# PLDroidCameraStreaming Release Notes for 1.2.0

## 简介
PLDroidCameraStreaming 是为 Android 开发者提供的 RTMP 直播推流 SDK

## 记录

### 推流 SDK
* 发布 pldroid-camera-streaming-1.2.0.jar
* 更新 libpldroid_ffmpegbridge.so
* 更新 Stream 设置接口：`setStream(stream)`
* 添加 Camera 切换接口：`switchCamera`
* 修复 Android L crash 问题
* 添加 Camera 切换状态：`STATE.CAMERA_SWITCHED`
* 添加 Torch 是否支持状态：`STATE.TORCH_INFO`
* 更新状态回调接口：`onStateChanged(state, extra)`
* 修复特殊操作的概率性 crash 问题
* 修复部分机型 `turnLightOn` 及 `turnLightOff` 接口无效问题
* 修复部分机型点击 Home 按键 crash 问题
* 修复部分机型因 `PREVIEW_SIZE_LEVEL` 导致 crash 问题


### Demo
* 添加 Camera 切换操作演示代码
* 更新 Torch 组件显示逻辑
