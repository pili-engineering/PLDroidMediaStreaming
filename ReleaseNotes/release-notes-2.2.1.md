# PLDroidMediaStreaming Release Notes for 2.2.1

本次更新:

## 版本

- 发布 pldroid-media-streaming-2.2.1.jar
- 发布 libpldroid_streaming_amix.so
- 更新 libpldroid_streaming_h264_encoder.so
- 更新 libpldroid_mmprocessing.so

## 功能

-  新增混音功能

  ```
    MediaStreamingManager 类中增加:
    /**
     * get Audio Mixer for playing MP3 etc. files while streaming.
     * @return
     */
    public AudioMixer getAudioMixer()

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public class AudioMixer {
        public boolean setFile(String path, boolean loop)

        /**
         * start or resume the mixer.
         */
        public boolean play()
    }
  ```

-  新增返听功能

  ```
    MediaStreamingManager 类中增加:
    public boolean startPlayback()
    public void stopPlayback()
  ```

-  新增图片推流过程中动态切换图片功能

  ```
    public void setPictureStreamingResourceId(int resId)
    public void setPictureStreamingFilePath(String filePath)
  ```

-  新增推流画面自定义剪裁

  ```
    /**
     * Sets the preferred cropped video encoding size.
     * top-left of the video is the origin of the coordinate system.
     * <p>
     * @param x the starting point coordinate
     * @param y the starting point coordinate
     * @param width the width in pixels of the encoding size.
     * @param height the height in pixels of the encoding size.
     *
     * @return this
     *
     * */
    public StreamingProfile setPreferredVideoEncodingSize(int x, int y, int width, int height)
  ```

## 缺陷

- 修复切换摄像头偶现的无法推流的问题
- 修复开启动态码率后在部分配置下出现的马赛克问题
- 移除 CameraSourceImproved 相关接口

## 更新注意事项

- build.gradle 中不再需要如下配置

```
compile 'com.qiniu.pili:pili-android-qos:0.8.+'
```