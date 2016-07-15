# PLDroidCameraStreaming Release Notes for 1.7.1

## 简介
PLDroidCameraStreaming 是为 Android 开发者提供的 RTMP 直播推流 SDK

## 记录

### 推流 SDK
#### 功能
  - 新增自定义水印位置支持
    - 标记 WatermarkSetting(Context ctx, int resId, WATERMARK_LOCATION location, WATERMARK_SIZE size, int alpha) 为 Deprecated
    - 标记 WatermarkSetting(Context ctx, String absoluteResPath, WATERMARK_LOCATION location, WATERMARK_SIZE size, int alpha) 为 Deprecated
    - 标记 WatermarkSetting(Context ctx, int resId, WATERMARK_LOCATION location, int alpha) 为 Deprecated
    - 新增 WatermarkSetting(Context ctx)
    - 新增 WatermarkSetting setSize(WATERMARK_SIZE size)
    - 新增 WatermarkSetting setResourcePath(String absoluteResPath)
    - 新增 WatermarkSetting setResourceId(int resId)
    - 新增 WatermarkSetting setCustomPosition(float x, float y)
    - 新增 WatermarkSetting setLocation(WATERMARK_LOCATION location)
    - 新增 WatermarkSetting setAlpha(int alpha)
  - 新增前置闪光灯支持（需硬件支持，如美图 M4）
    - 若设备支持前置闪光灯，会回调对应的信息
  - 新增第三个 Camera 的支持（比如 LG G5）
    - 新增 CameraStreamingSetting#getNumberOfCameras()
    - 新增 CameraStreamingSetting#CAMERA_FACING_ID
    - 新增 CameraStreamingManager#switchCamera(CAMERA_FACING_ID camFacingId)
  - 新增 Camera 本地预览镜像反转支持
    - 新增 CameraStreamingSetting#setFrontCameraPreviewMirror

#### 缺陷
  - 修复软编模式下 iOS 播放器硬解失败的问题
  - 修复透明水印有黑色背景问题
  - 修复特殊手机（奇酷）音画不同步
  - 修复内置美颜模式下，部分机型黑屏现象
  - 修复软编模式下，水印在播放端可能被压缩的问题
  - 修复特殊机型（美图 M4）静音模式下出现杂音的问题
  - 修复特殊机型特殊场景下，音频采集初始化失败导致的 crash 问题
  - 修复 demo issue

#### 版本
  - 发布 pldroid-camera-streaming-1.7.1.jar
  - 更新 libpldroid_mmprocessing.so
  - 更新 libpldroid_streaming_core.so
  - 更新 libpldroid_streaming_aac_encoder.so
  - 更新 libpldroid_streaming_h264_encoder.so 
  - 更新 Demo
