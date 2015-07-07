# PLDroidCameraStreaming Release Notes for 1.1.0

## 简介
PLDroidCameraStreaming 是为 Android 开发者提供的 RTMP 直播推流 SDK

## 记录

### 推流 SDK
* 发布 pldroid-camera-streaming-1.1.0.jar
* 更新 libpldroid_ffmpegbridge.so
* 优化 ffmpegbridge 模块，降低 libpldroid_ffmpegbridge.so 文件大小
* 添加纯音频推流支持：添加纯音频推流 `CameraStreamingManager(Context ctx)` 构造函数
* 纯音频推流支持后台运行
* 添加 preview size 设定接口：`setCameraPrvSizeLevel` 及 `setCameraPrvSizeRatio`
* 添加 torch 操作接口： `turnLightOn` 及 `turnLightOff`
* 添加控制连续自动对焦的接口：`setContinuousFocusModeEnabled`
* 废弃 `setCameraPreviewSize` 接口
* 修复部分机型因 preivew size 不支持而导致的 crash 问题


### Demo
* 添加 `AudioStreamingActivity` 及 `StreamingBaseActivity`，用来演示纯音频推流
* 添加 torch 操作演示代码
