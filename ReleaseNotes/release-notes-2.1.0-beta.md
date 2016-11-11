# PLDroidMediaStreaming Release Notes for 2.1.0 Beta

## 简介
PLDroidMediaStreaming 是为 Android 开发者提供的 RTMP 直播推流 SDK

## 记录

### 推流 SDK
#### 功能
  - 新增外部数据源支持
    - 新增 `StreamingManager`
    
      ```
       class com.qiniu.pili.droid.streaming.StreamingManager {
         public StreamingManager(android.content.Context);
         public StreamingManager(android.content.Context, AVCodecType);
         boolean prepare(StreamingProfile);
         boolean resume();
         void pause();
         void destroy();
         boolean startStreaming();
         boolean stopStreaming();
         void setNativeLoggingEnabled(boolean);
         void setStreamingStateListener(StreamingStateChangedListener);
         void setStreamingSessionListener(StreamingSessionListener);
         void setStreamStatusCallback(StreamStatusCallback);
         void setStreamingProfile(StreamingProfile);
         Surface getInputSurface(int width, int height);
         void frameAvailable(boolean endOfStream);
         void inputAudioFrame(ByteBuffer buffer, int size, long tsInNanoTime, boolean isEof);
         void inputAudioFrame(byte[] buffer, long tsInNanoTime, boolean isEof);
         void inputVideoFrame(ByteBuffer buffer, int size, int width, int height, int rotation, boolean mirror, int fmt, long tsInNanoTime);
         void inputVideoFrame(byte[] buffer, int width, int height, int rotation, boolean mirror, int fmt, long tsInNanoTime);
         void updateEncodingType(AVCodecType);
      }
      ```
  - 新增录屏支持
    - 新增 `ScreenStreamingManager`
    
      ```
      class com.qiniu.pili.droid.streaming.ScreenStreamingManager {
         public ScreenStreamingManager(android.app.Activity);
         boolean prepare(ScreenSetting screenSetting, MicrophoneStreamingSetting microphoneSetting, StreamingProfile profile);
         boolean startStreaming();
         boolean stopStreaming();
         void setStreamingStateListener(StreamingStateChangedListener);
         void mute(boolean enable);
         void destroy();
      }
      ```
    - 新增 `ScreenSetting`
    
    ```
    class com.qiniu.pili.droid.streaming.ScreenSetting {
        ScreenSetting setSize(int width, int height);
        ScreenSetting setDpi(int dpi);
        int getWidth();
        int getHeight();
        int getDpi();
    }
    ```
    
    - 注意事项：需要在 AndroidManifest.xml 增加 SDK 内置 Activity `com.qiniu.pili.droid.streaming.screen.ScreenCaptureRequestActivity` 的声明

    ```
      <activity
            android:name="com.qiniu.pili.droid.streaming.screen.ScreenCaptureRequestActivity"
            android:theme="@android:style/Theme.Translucent.NoTitleBar" >
      </activity>
    ```
  - 新增 `StreamingPreviewCallback` 接口回调参数 
  
    ```
    /**
    * Called if the {@link StreamingPreviewCallback} registered.
    *
    * @param data the contents of the preview frame in fmt format
    * @param width the width of the frame
    * @param height the height of the frame
    * @param rotation set the clockwise rotation of frame in degrees to achieve the same effect of preview display.
    * @param fmt the format of the frame. See also {@link com.qiniu.pili.droid.streaming.av.common.PLFourCC}
    * @param tsInNanoTime the timestamp of the frame
    *
    * */
    boolean StreamingPreviewCallback#onPreviewFrame(byte[] data, int width, int height, int rotation, int fmt, long tsInNanoTime);
    ```

#### 优化
  - 新增 Camera 数据源优化及配置
    - `CameraStreamingSetting#setCameraSourceImproved(boolean)`
  - 新增 Camera 预览尺寸优化
    - `CameraStreamingSetting#setPreviewSizeOptimize(boolean)`
  - 支持自适应码率
    - `StreamingProfile#setAdaptiveBitrateEnable(boolean)`
    - Min Bitrate 为 `StreamingProfile#VIDEO_QUALITY_LOW1`, Max Bitrate 为用户设置的目标码率
      
#### 缺陷
  - 修复部分水印资源显示异常问题
  - 修复特殊低端机型对焦导致的 crash 问题
  - 修复部分机型硬编导致的 crash 问题
  - 修复特殊情况下部分机型，onDrawFrame 未运行在渲染线程导致的 crash 问题
  - 修复特殊机型 INTERNET 权限已声明但不生效导致的 crash 问题
  - 修复其他概率性 crash 问题

#### 版本
  - 发布 pldroid-media-streaming-2.1.0.jar
  - 更新 libpldroid_mmprocessing.so
  - 更新 libpldroid_streaming_h264_encoder.so

### Demo
  - 新增避免 Android 6.0(+) 设备权限导致 crash 问题的 patch
  - 新增录屏功能的 Demo 展示
    - `ScreenRecorderActivity`
  - 新增外部数据源功能的 Demo 展示
    - `ExtCapStreamingActivity`
  - 更新 Demo 
