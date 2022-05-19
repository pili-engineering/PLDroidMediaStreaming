# PLDroidMediaStreaming Release Notes for 3.1.3

本次更新:

## 版本

- 发布 pldroid-media-streaming-3.1.3.jar

## 功能

- 支持 userID 变更

## 优化

- 更新 HappyDNS 版本为 1.0.0，支持配置 HTTPDNS 服务

## 注意事项

- **从 v3.1.3 版本开始，HappyDNS 库务必升级到 1.0.0 版本**
- 从 v3.1.2 版本开始，SDK 将不再强制要求获取 READ_PHONE_STATE 和 ACCESS_FINE_LOCATION 的权限
- 从 v3.1.1 版本开始，HappyDNS 库务必到 0.2.18 版本
- **从 v3.0.2 版本开始，请务必添加 `android.arch.lifecycle:extensions:x.y.z` 的依赖**
- 从 v3.0.2 版本开始，`StreamingEnv.init(Context context)` 已被弃用，请更新到 `StreamingEnv.init(Context contex, String userId)` 进行环境的初始化，其中，userId 代表用户的唯一标识符，用于区分不同的用户