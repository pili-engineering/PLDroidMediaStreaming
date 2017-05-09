# PLDroidMediaStreaming Release Notes for 2.2.0

本次更新:

## 版本

- 发布 pldroid-media-streaming-2.2.0.jar
- 更新 libpldroid_streaming_core.so
- 更新 libpldroid_streaming_h264_encoder.so
- 更新 libpldroid_streaming_aac_encoder.so
- 更新 libpldroid_mmprocessing.so


## 功能

- `VideoProfile` 提供 `annexb` 和 `avcc` 格式的配置选项

  ```
  public VideoProfile(int fps, int bitrate, int maxKeyFrameInterval, boolean annexb);
  ```

- 新增后台推流功能

  ```
    /**
     * toggle publishing the picture set in StreamingProfile
     * 
     * must call setPictureStreamingFilePath() before togglePictureStreaming()
     */
    public boolean togglePictureStreaming()

    /**
     * Set picture for streaming
     *
     * Accept only 32 bit png (ARGB)
     */
    public StreamingProfile setPictureStreamingFilePath(String filePath)
  ```

- 新增码率调节方式设置接口

  ```
    /**
     * adaptive bitrate adjust mode
     */
    public enum BitrateAdjustMode {
        Auto,     // 自动调节码率
        Manual,   // 手动调节码率
        Disable   // 禁止调节码率
    }
  ```

- 新增手动调节码率接口

  ```
    /**
     * manual adjustment bitrate for streaming
     *
     * @param targetBitrate unit: bps, range: 10 * 1024 ~ 10000 * 1024
     *
     * @return true: success, false: fail
     */
    public boolean adjustVideoBitrate(int targetBitrate)
  ```

- 新增双声道推流的支持

  ```
    /**
     * Notice !!! {@link AudioFormat#CHANNEL_IN_STEREO} is NOT guaranteed to work on all devices.
     */
  microphoneStreamingSetting = new MicrophoneStreamingSetting();
  microphoneStreamingSetting.setChannelConfig(AudioFormat.CHANNEL_IN_STEREO);
  ```

- 实现新版日志系统，SDK 输出的日志过滤 TAG 为：`PLDroidMediaStreaming`

## 缺陷

- 修复因图片的透明度导致的硬编水印异常
- 修复自定义帧率配置不生效的问题
- 修复部分场景下停止推流低概率偶现的ANR异常
- 修复极端弱网或低内存下丢关键帧导致播放花屏的问题
- 修复软编推流 tbc 过大的问题

## 更新注意事项

- 请在 build.gradle 中删除如下配置

```
compile 'com.qiniu.pili:pili-android-qos:0.8.+'
```