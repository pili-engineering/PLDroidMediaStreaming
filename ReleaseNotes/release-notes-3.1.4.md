# PLDroidMediaStreaming Release Notes for 3.1.4

本次更新:

## 版本

- 发布 pldroid-media-streaming-3.1.4.jar
- 更新 libpldroid_streaming_core.so

## 缺陷

- 修复个别场景下的内存泄漏问题
- 修复软编场景切换摄像头后无法推流的问题
- 修复个别接口调用偶现耗时较长的问题

## 优化

- 更新默认 DNS 的配置，优化解析体验

## 注意事项

- **从 v3.1.3 版本开始，HappyDNS 库务必升级到 1.0.0 版本**
- 从 v3.1.2 版本开始，SDK 将不再强制要求获取 READ_PHONE_STATE 和 ACCESS_FINE_LOCATION 的权限
- 从 v3.1.1 版本开始，HappyDNS 库务必到 0.2.18 版本
- **从 v3.0.2 版本开始，请务必添加 `android.arch.lifecycle:extensions:x.y.z` 的依赖**
- 从 v3.0.2 版本开始，`StreamingEnv.init(Context context)` 已被弃用，请更新到 `StreamingEnv.init(Context contex, String userId)` 进行环境的初始化，其中，userId 代表用户的唯一标识符，用于区分不同的用户