# PLDroidCameraStreaming Release Notes for 1.7.0

## 简介
PLDroidCameraStreaming 是为 Android 开发者提供的 RTMP 直播推流 SDK

## 记录

### 推流 SDK
#### 功能
  - 新增内置美颜功能，美颜效果可调节
    - `CameraStreamingSetting#setBuiltInFaceBeautyEnabled(boolean)`
    - `CameraStreamingSetting.FaceBeautySetting`
    - `CameraStreamingManager.updateFaceBeautySetting(CameraStreamingSetting.FaceBeautySetting)`
  - 新增采集帧率控制，避免帧率飙升不可控
    - `StreamignProfile#setFpsControllerEnable(boolean)`

#### 缺陷
  - 修复特殊步骤下 Contex 泄露问题
  - 修复硬编水印在特殊机型上（Meilan note 2）异常显示

#### 优化
  - 优化采集，避免过度 UI 操作导致推流帧率降低
  - 优化内存使用

#### 版本
  - 发布 pldroid-camera-streaming-1.7.0.jar
  - 更新 libpldroid_mmprocessing.so
  - 更新 libpldroid_streaming_core.so
  - 更新 libpldroid_streaming_h264_encoder.so 
  - 更新 Demo
