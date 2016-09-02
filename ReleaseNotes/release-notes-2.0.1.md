# PLDroidMediaStreaming Release Notes for 2.0.1

## 简介
PLDroidMediaStreaming 是为 Android 开发者提供的 RTMP 直播推流 SDK

## 记录

### 推流 SDK
#### 功能
  - 硬编支持 YUV 数据回调
    - 实现 `StreamingPreviewCallback` 并注册
  
#### 缺陷
  - 兼容更多的硬编机型
  - 修复硬编特殊机型音画不同步问题
  - 修复特殊机型上的 crash 问题
  - 修复重连可能导致的 crash 问题

#### 版本
  - 发布 pldroid-media-streaming-2.0.1.jar
  - 依赖 compile 'com.qiniu.pili:pili-android-qos:0.8.+'
  - 更新 libpldroid_streaming_core.so
  - 更新 libpldroid_streaming_h264_encoder.so
  - 更新 demo 代码
