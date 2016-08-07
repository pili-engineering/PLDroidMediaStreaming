# PLDroidMediaStreaming Release Notes for 2.0.0 Beta

## 简介
PLDroidMediaStreaming 是为 Android 开发者提供的 RTMP 直播推流 SDK

## 记录

### 推流 SDK
#### 功能
  - 更名为 PLDroidMediaStreaming
  - 新增可直接设置推流地址的 API
    - StreamingProfile#setPublishUrl(String url)
  - 新增推流节点调度机制
  - 新增 MediaStreamingManager 类，并废弃 CameraStreamingManager
  
   ```
   // MediaStreamingManager 对应的 API
   public <init>(android.content.Context);
   public <init>(android.content.Context, ***);
   public <init>(android.content.Context, com.qiniu.pili.droid.streaming.widget.AspectFrameLayout, android.opengl.GLSurfaceView);
   public <init>(android.content.Context, com.qiniu.pili.droid.streaming.widget.AspectFrameLayout, android.opengl.GLSurfaceView, ***);
   boolean prepare(***);
   boolean prepare(***, ***);
   boolean prepare(***, ***, ***);
   boolean prepare(***, ***, ***, ***);
   boolean resume();
   void pause();
   void destroy();
   boolean turnLightOn();
   boolean turnLightOff();
   boolean switchCamera();
   boolean switchCamera(***);
   boolean startStreaming();
   boolean stopStreaming();
   void setNativeLoggingEnabled(boolean);
   void setStreamingStateListener(***);
   void setStreamingSessionListener(***);
   void setStreamingPreviewCallback(***);
   void setSurfaceTextureCallback(***);
   void setStreamStatusCallback(***);
   void setAudioSourceCallback(***);
   void setVideoFilterType(***);
   void captureFrame(int,int,***);
   void setStreamingProfile(***);
   void updateEncodingType(***);
   void notifyActivityOrientationChanged();
   void doSingleTapUp(***, ***);
   void setFocusAreaIndicator(***, ***);
   void setZoomValue(***);
   *** getMaxZoom();
   *** getZoom();
   *** isZoomSupported();
   void mute(***);
   void updateFaceBeautySetting(***);
   ```
   
  - 新增一些辅助类并废弃相关的类
    - 新增 StreamingStateChangedListener，并废弃 CameraStreamingManager#StreamingStateListener
    - 新增 StreamingState，并废弃 CameraStreamingManager#STATE
    - 新增 StreamingSessionListener，并废弃 CameraStreamingManager#StreamingSessionListener
    - 新增 AVCodecType，并废弃 CameraStreamingManager#EncodingType
  - 新增 Audio PCM 数据回调接口
    - AudioSourceCallback#onAudioSourceAvailable(ByteBuffer srcBuffer, int size, boolean isEof)
  - 新增检测是否支持指定的 Camera
    - CameraStreamingSetting#hasCameraFacing(CAMERA_FACING_ID id)
  
#### 缺陷
  - 修复软编模式下， Android 6.0 target 设置为 23 无法正常推流的问题
  - 修复硬编模式下，快速 home 键导致异常退出的问题
  - 修复特殊机型初始化时导致的 crash
  
#### 优化
  - 优化水印清晰度

#### 版本
  - SDK 更名为 PLDroidMediaStreaming
  - SDK 包名更改为 com.qiniu.pili.droid.streaming.*;
  - SDK Demo 重命名为 PLDroidMediaStreamingDemo
  - 发布 pldroid-media-streaming-2.0.0.jar
  - 更新 libpldroid_mmprocessing.so
  - 更新 libpldroid_streaming_core.so
  - 更新 libpldroid_streaming_h264_encoder.so 
