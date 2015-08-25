# PLDroidCameraStreaming Release Notes for 1.3.0

## 简介
PLDroidCameraStreaming 是为 Android 开发者提供的 RTMP 直播推流 SDK

## 记录

### 推流 SDK
  - 发布 pldroid-camera-streaming-1.3.0.jar
  - 新增自适应码率功能
    - `SendingBufferProfile`
    - `CameraStreamingManager.STATE.SENDING_BUFFER_EMPTY`
    - `CameraStreamingManager.STATE.SENDING_BUFFER_FULL`
    - `CameraStreamingManager.STATE.SENDING_BUFFER_HAS_FEW_ITEMS`
    - `CameraStreamingManager.STATE.SENDING_BUFFER_HAS_MANY_ITEMS`
    - `notifyProfileChanged(StreamingProfile)`
    - `reduceVideoQuality(int)`
    - `improveVideoQuality(int)`
    - `CameraStreamingManager$StreamingStateListener.onStateHandled(int, Object)`
  - 新增截帧接口
    - `captureFrame(int, int, FrameCapturedCallback)`
    - `onFrameCaptured(Bitmap bmp)`
  - 新增 Preview Layout `REAL/FULL` mode，解决显示黑边问题
    - `setShowMode(AspectFrameLayout.SHOW_MODE)`
    - `AspectFrameLayout.SHOW_MODE.FULL`
    - `AspectFrameLayout.SHOW_MODE.REAL`
  - 修复 IOS 和 Android 使用同一个 stream 时，导致 IOS 无法正常推流的问题
  - 修复部分机型切换前后置 crash 问题

### 推流 Demo
  - 新增自适应码率演示代码
  - 新增截帧演示代码
  - 新增 REAL/FULL mode 演示代码
