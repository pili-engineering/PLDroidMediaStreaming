# PLDroidCameraStreaming Release Notes for 1.5.0

## 简介
PLDroidCameraStreaming 是为 Android 开发者提供的 RTMP 直播推流 SDK

## 记录

### 推流 SDK
  - 发布 pldroid-camera-streaming-1.5.0.jar
  - 更新 libpldroid_streaming_core.so 和 libpldroid_streaming_h264_encoder.so
  - 支持手动对焦
    - `public void CameraStreamingManager#doSingleTapUp(int x, int y)`
    - `public void CameraStreamingManager#setFocusAreaIndicator(ViewGroup indicatorLayout, View indicator)`
  - 支持 Zoom
    - `public void CameraStreamingManager#setZoomValue(int value)`
    - `public int CameraStreamingManager#getMaxZoom()`
    - `public int CameraStreamingManager#getZoom()`
    - `public boolean CameraStreamingManager#isZoomSupported()`
  - 支持 mute/unmute
    - `public void CameraStreamingManager#mute(boolean enable)`
  - 新增 `setSendTimeoutInSecond` API
    - `public StreamingProfile StreamingProfile#setSendTimeoutInSecond(int sendTimeout)`  
  - 新增 `AVProfile`, `AudioProfile`, `VideoProfile`
    - `StreamingProfile StreamingProfile#setAVProfile(AVProfile avProfile)`
    - `public StreamingProfile#VideoProfile(int reqFps, int reqBitrate, maxKeyFrameInterval)`
    - `public StreamingProfile#AudioProfile(int sampleRate, int reqBitrate)`
    
  - 对回调方法 `sortCameraPrvSize` 的行参 supportedPreviewSizeList 进行从小到大排序
  - 当 DnsManager 设置为 null 后，不进行 Dns 解析，[Issue 78](https://github.com/pili-engineering/PLDroidCameraStreaming/issues/78)
  - 优化数据源采集和显示效率，避免 UI 卡顿
  - 修复硬编模式下，重连导致概率性 crash 问题
  - 方法 onPrepare(), onResume(), onPause(), onDestroy() 分别重命名为 prepare(), resume(), pause(), destroy()

### Demo
  - 更新 demo 样例代码
