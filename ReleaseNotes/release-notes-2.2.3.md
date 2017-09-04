# PLDroidMediaStreaming Release Notes for 2.2.3

本次更新:

## 版本

- 发布 pldroid-media-streaming-2.2.3.jar

## 缺陷

- 修复未配置码率上下限导致动态码率失控的问题

## 更新注意事项

- build.gradle 中不再需要如下配置

```
compile 'com.qiniu.pili:pili-android-qos:0.8.+'
```