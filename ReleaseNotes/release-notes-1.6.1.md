# PLDroidCameraStreaming Release Notes for 1.6.1

## 简介
PLDroidCameraStreaming 是为 Android 开发者提供的 RTMP 直播推流 SDK

## 记录

### 推流 SDK
  - 发布 pldroid-camera-streaming-1.6.1.jar
  - 新增 libpldroid_mmprocessing.so
  - 更新 libpldroid_streaming_core.so 和 libpldroid_streaming_h264_encoder.so 
  - 增加水印支持
    - 新增 `WatermarkSetting`
    - 新增 `WatermarkSetting.WATERMARK_LOCATION`
    - 新增 `WatermarkSetting.WATERMARK_SIZE`
    - 新增 `prepare(CameraStreamingSetting, MicrophoneStreamingSetting, WatermarkSetting, StreamingProfile)`
  - 优化软编 codec，提升画质和码控能力
  - 兼容特殊的直播设备
  - 新增 TransformMatrix 到 SurfaceTextureCallback#onDrawFrame
  - 修复 `CameraStreamingManager#pause` 耗时较长
  - 修复硬编纯音频无法正常停止推流
  - 修复硬编推流过程中特殊步骤导致的概率性 crash
  
### Demo
  - 更新 Demo 代码
