# PLDroidMediaStreaming Release Notes for 2.2.2

本次更新:

## 版本

- 发布 pldroid-media-streaming-2.2.2.jar
- 更新 libpldroid\_streaming\_amix.so
- 更新 libpldroid\_streaming\_core.so
- 更新 libpldroid\_mmprocessing.so

## 功能

- 录屏推流中增加推图片接口

  ```java
    ScreenStreamingManager 类中增加:
    /**
     * toggle publishing the picture set in StreamingProfile
     */
    public boolean togglePictureStreaming()
  ```

## 缺陷

- 修复超过 4.5 小时连续推流断开连接问题
- 修复硬编 yuv 模式在某些机型颜色不正问题
- 修复特定音频文件导致混音失败问题
- 修复后台录屏推流在某些机型声音异常问题
- 修复软编 1080p 推流在某些机型崩溃问题

## 更新注意事项

- build.gradle 中不再需要如下配置

```
compile 'com.qiniu.pili:pili-android-qos:0.8.+'
```
