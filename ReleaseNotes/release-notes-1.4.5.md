# PLDroidCameraStreaming Release Notes for 1.4.5

## 简介
PLDroidCameraStreaming 是为 Android 开发者提供的 RTMP 直播推流 SDK

## 记录

### 推流 SDK
  - 发布 pldroid-camera-streaming-1.4.5.jar
  - 更新 libpldroid_streaming_core.so，libpldroid_streaming_aac_encoder.so 和 libpldroid_streaming_h264_encoder.so
  - 新增动态更改 Encoding Orientation 支持
  - 新增动态切换横竖屏支持
  - 新增 `onPreviewSizeSelected` 支持
  - 新增 `setPreferredVideoEncodingSize` 支持
  - 新增 `VIDEO_ENCODING_HEIGHT_544` 支持
  - 优化网络传输
  - 提升画质
  - 优化前后置切换
  - 标记 `VIDEO_ENCODING_SIZE_QVGA` 等 Deprecated
  - 标记 `onPreviewFrame(byte[] datas, Camera camera)` Deprecated
  - 修复部分机型概率性 ANR

### Demo
  - 更新 demo 样例代码
