# PLDroidMediaStreaming Release Notes for 3.1.5

本次更新:

## 版本

- 发布 pldroid-media-streaming-3.1.5.jar
- 更新 libpldroid_streaming_core.so
- 更新 libpldroid_streaming_h264_encoder.so
- 更新 libpldroid_streaming_srt.so
- 更新 libpldroid_mmprocessing.so
- 移除 libssl.so
- 移除 libcrypto.so

## 功能

- 新增对 H.265 硬编编码格式的支持

## 缺陷

- 修复频繁切换摄像头后无法再次切换的问题
- 修复 SRT 推流音画不同步的问题
- 修复上架时提示 OpenSSL 版本存在风险的问题

## 注意事项

- **从 v3.1.5 版本开始，将不再支持 armeabi 架构**
- **从 v3.1.3 版本开始，HappyDNS 库务必升级到 1.0.0 版本**
- 从 v3.1.2 版本开始，SDK 将不再强制要求获取 READ_PHONE_STATE 和 ACCESS_FINE_LOCATION 的权限
- 从 v3.1.1 版本开始，HappyDNS 库务必到 0.2.18 版本