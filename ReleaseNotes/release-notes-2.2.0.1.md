# PLDroidMediaStreaming Release Notes for 2.2.0.1

本次更新:

## 版本

- 发布 pldroid-media-streaming-2.2.0.1.jar
- 更新 libpldroid_streaming_h264_encoder.so
- 更新 libpldroid_mmprocessing.so


## 功能

## 缺陷

- 修复切换摄像头偶现的无法推流的问题
- 修复开启动态码率后在部分配置下出现的马赛克问题

## 更新注意事项

- build.gradle 中不再需要如下配置

```
compile 'com.qiniu.pili:pili-android-qos:0.8.+'
```