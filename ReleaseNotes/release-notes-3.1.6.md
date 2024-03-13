# PLDroidMediaStreaming Release Notes for 3.1.6

本次更新:

## 版本

- 发布 pldroid-media-streaming-3.1.6.jar
- 更新 libpldroid_streaming_core.so

## 功能

- 新增流状态异常情况下的 DISCONNECTED 状态回调
- 新增对 YV12 格式外部数据导入的支持

## 缺陷

- 修复频繁添加贴纸闪烁的问题
- 修复 H.265 场景下 hls 播放地址无法播放的问题

## 注意事项

- **从 v3.1.5 版本开始，将不再支持 armeabi 架构**
- **从 v3.1.3 版本开始，HappyDNS 库务必升级到 1.0.0 版本**
- 从 v3.1.2 版本开始，SDK 将不再强制要求获取 READ_PHONE_STATE 和 ACCESS_FINE_LOCATION 的权限
- 从 v3.1.1 版本开始，HappyDNS 库务必到 0.2.18 版本