# PLDroidMediaStreaming Release Notes for 3.1.1

本次更新:

## 版本

- 发布 pldroid-media-streaming-3.1.1.jar

## 功能

- 新增直播中对动态切换蓝牙耳机的支持

## 缺陷

- 修复外部导入数据直播时间戳异常的问题
- 修复个别场景下贴纸异常的问题
- 修复个别场景下屏幕旋转导致的预览异常问题
- 修复外部导入直播修改配置失效的问题
- 修复个别场景的崩溃问题

## 注意事项

- 从 v3.1.1 版本开始，Happy DNS 库建议到 0.2.18 版本
- **从 v3.0.2 版本开始，请务必添加 `android.arch.lifecycle:extensions:x.y.z` 的依赖**
- 从 v3.0.2 版本开始，`StreamingEnv.init(Context context)` 已被弃用，请更新到 `StreamingEnv.init(Context contex, String userId)` 进行环境的初始化，其中，userId 代表用户的唯一标识符，用于区分不同的用户
- **从 v3.0.0 版本开始，七牛直播推流 SDK 需要先获取授权才能使用。授权分为试用版和正式版，可通过 400-808-9176 转 2 号线联系七牛商务咨询，或者 [通过工单](https://support.qiniu.com/?ref=developer.qiniu.com) 联系七牛的技术支持。**
- **v3.0.0 之前的版本不受影响，请继续放心使用。**
- **老客户升级 v3.0.0 版本之前，请先联系七牛获取相应授权，以免发生鉴权不通过的现象。**