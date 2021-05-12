# PLDroidMediaStreaming Release Notes for 3.1.0

本次更新:

## 版本

- 发布 pldroid-media-streaming-3.1.0.jar
- 发布 libpldroid_streaming_srt.so
- 发布 libssl.so
- 发布 libcrypto.so
- 更新 libpldroid_streaming_core.so

## 功能

- 新增 SRT 传输协议支持
- 新增日志上传功能

## 缺陷

- 修复 Runtime 相关安全漏洞问题
- 修复图片推流场景下的内存泄漏问题
- 修复个别场景下的空指针问题
- 修复自定义水印导致的崩溃问题
- 修复个别场景下的编码器异常问题
- 修复频繁添加贴纸场景下的 ANR 问题

## 注意事项

- **从 v3.0.2 版本开始，请务必添加 `android.arch.lifecycle:extensions:x.y.z` 的依赖**
- 从 v3.0.2 版本开始，`StreamingEnv.init(Context context)` 已被弃用，请更新到 `StreamingEnv.init(Context contex, String userId)` 进行环境的初始化，其中，userId 代表用户的唯一标识符，用于区分不同的用户
- 从 v3.0.1 版本开始，Happy DNS 库请务必升级到 0.2.17 版本
- **从 v3.0.0 版本开始，七牛直播推流 SDK 需要先获取授权才能使用。授权分为试用版和正式版，可通过 400-808-9176 转 2 号线联系七牛商务咨询，或者 [通过工单](https://support.qiniu.com/?ref=developer.qiniu.com) 联系七牛的技术支持。**
- **v3.0.0 之前的版本不受影响，请继续放心使用。**
- **老客户升级 v3.0.0 版本之前，请先联系七牛获取相应授权，以免发生鉴权不通过的现象。**