# PLDroidMediaStreaming Release Notes for 3.0.1

本次更新:

## 版本

- 发布 pldroid-media-streaming-3.0.1.jar
- 更新 libpldroid_streaming_core.so

## 功能

- 新增日志保存功能

## 优化

- 优化 demo 结构
- 优化 SEI 发送功能，支持自定义大小的数据发送

## 缺陷

- 修复个别机型初始化前置摄像头失败的问题
- 修复个别机型录屏推流异常的问题
- 修复 targetSdkVersion 29 以后录屏崩溃的问题

## 注意事项

- 从 3.0.1 版本开始，如果您使用了 Happy DNS 库，建议升级到 0.2.16 版本
- **从 v3.0.0 版本开始，七牛直播推流 SDK 需要先获取授权才能使用。授权分为试用版和正式版，可通过 400-808-9176 转 2 号线联系七牛商务咨询，或者 [通过工单](https://support.qiniu.com/?ref=developer.qiniu.com) 联系七牛的技术支持。**
- **v3.0.0 之前的版本不受影响，请继续放心使用。**
- **老客户升级 v3.0.0 版本之前，请先联系七牛获取相应授权，以免发生鉴权不通过的现象。**
- 基于 114 dns 解析的不确定性，使用该解析可能会导致解析的网络 ip 无法做到最大的优化策略，进而出现推流质量不佳的现象。因此建议使用非 114 dns 解析
- 从 v2.4.1 开始，VideoProfile 对 H264 格式配置的参数由 annexb 改为 avcc，之前设置为 false 的客户，需要将配置改为 true。

例如目前设有如下配置的客户：

```java
StreamingProfile.VideoProfile vProfile =
	new StreamingProfile.VideoProfile(20, 1000 * 1024, 60, false);
```
需将参数调整为：

```java
StreamingProfile.VideoProfile vProfile =
	new StreamingProfile.VideoProfile(20, 1000 * 1024, 60, true);
```
