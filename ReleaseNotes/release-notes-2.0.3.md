# PLDroidMediaStreaming Release Notes for 2.0.3 Hotfix

## 简介
PLDroidMediaStreaming 是为 Android 开发者提供的 RTMP 直播推流 SDK

## 记录

### 推流 SDK

#### 缺陷
  - 修复 home 键退出之后，再次启动应用，导致 onPreviewFrame 回调不生效问题
  - 修复硬编模式下，home 键退出导致的 crash 问题
  - 修复关闭音频权限之后，开始直播并未正常返回 AUDIO_RECORDING_FAIL 问题
  - 修复硬编模式下，部分机型 EglCore 导致的空指针异常

#### 版本
  - 发布 pldroid-media-streaming-2.0.3.jar
