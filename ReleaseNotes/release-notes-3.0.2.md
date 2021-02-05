# PLDroidMediaStreaming Release Notes for 3.0.2

本次更新:

## 版本

- 发布 pldroid-media-streaming-3.0.2.jar
- 更新 libpldroid_streaming_core.so

## 功能

- 新增摄像头抢占回调
- 新增编码器异常状态回调

## 优化

- 优化录屏场景下可能出现的帧率较低的问题

## 缺陷

- 修复音视频和纯音频模式动态切换失败的问题
- 修复个别机型推流画面变形的问题
- 修复添加贴纸预览和播放效果不一致的问题
- 修复软编场景实时刷新水印存在的闪烁问题
- 修复特殊场景下偶现的 ANR 问题
- 修复特殊场景下的崩溃问题

## 注意事项

- **从 v3.0.2 版本开始，请务必添加 `android.arch.lifecycle:extensions:x.y.z` 的依赖**
- 从 v3.0.2 版本开始，`StreamingEnv.init(Context context)` 已被弃用，请更新到 `StreamingEnv.init(Context contex, String userId)` 进行环境的初始化，其中，userId 代表用户的唯一标识符，用于区分不同的用户
- 从 v3.0.1 版本开始，Happy DNS 库请务必升级到 0.2.17 版本
- **从 v3.0.0 版本开始，七牛直播推流 SDK 需要先获取授权才能使用。授权分为试用版和正式版，可通过 400-808-9176 转 2 号线联系七牛商务咨询，或者 [通过工单](https://support.qiniu.com/?ref=developer.qiniu.com) 联系七牛的技术支持。**
- **v3.0.0 之前的版本不受影响，请继续放心使用。**
- **老客户升级 v3.0.0 版本之前，请先联系七牛获取相应授权，以免发生鉴权不通过的现象。**