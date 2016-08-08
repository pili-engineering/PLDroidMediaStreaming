# PLDroidMediaStreaming

PLDroidMediaStreaming 是一个适用于 Android 的 RTMP 直播推流 SDK，可高度定制化和二次开发。特色是同时支持 H.264 软编／硬编和 AAC 软编／硬编。支持 Android Camera 画面捕获，并进行 H.264 编码，以及支持 Android 麦克风音频采样并进行 AAC 编码；还实现了一套可供开发者选择的编码参数集合，以便灵活调节相应的分辨率和码率；同时，SDK 提供数据源回调接口，用户可进行 Filter 处理。借助 PLDroidMediaStreaming ，开发者可以快速构建一款类似 [Meerkat](https://meerkatapp.co/) 或 [Periscope](https://www.periscope.tv/) 的 Android 直播应用。

## 功能特性
  - [x] 支持 H.264 和 AAC 软编（推荐）
  - [x] 支持 H.264 和 AAC 硬编
  - [x] 软编支持 Android Min API 15（Android 4.0.3）及其以上版本
  - [x] 硬编支持 Android Min API 18（Android 4.3）及其以上版本
  - [x] 支持构造带安全授权凭证的 RTMP 推流地址
  - [x] 支持 RTMP 封包及推流
  - [x] 支持 RTMP 推流自适应网络质量动态切换码率或自定义策略
  - [x] 支持内置美颜，以及可动态调节美颜效果
  - [x] 支持数据源回调接口，可自定义 Filter (滤镜) 特效处理
  - [x] 支持前后置摄像头，以及动态切换
  - [x] 支持自动对焦
  - [x] 支持手动对焦
  - [x] 支持 Encoding Mirror 设置
  - [x] 支持 Zoom 操作
  - [x] 支持 Mute/Unmute
  - [x] 支持闪光灯操作
  - [x] 支持纯音频推流，以及后台运行
  - [x] 支持截帧功能
  - [x] 支持动态更改 Encoding Orientation
  - [x] 支持动态切换横竖屏
  - [x] 支持蓝牙麦克风
  - [x] 支持 ARM, ARMv7a, ARM64v8a, X86 主流芯片体系架构
  
## PLDroidMediaStreaming 文档

请参考 wiki 文档：[PLDroidMediaStreaming 开发指南](https://github.com/pili-engineering/PLDroidMediaStreaming/wiki)

## 设备以及系统要求

- 设备要求：搭载 Android 系统的设备
- 系统要求：Android 4.0.3(API 15) 及其以上

## 版本升级须知
升级 SDK 之前，请在[这里](https://github.com/pili-engineering/PLDroidMediaStreaming/wiki#3.4)查看升级须知。

### 反馈及意见
当你遇到任何问题时，可以通过在 GitHub 的 repo 提交 issues 来反馈问题，请尽可能的描述清楚遇到的问题，如果有错误信息也一同附带，并且在 Labels 中指明类型为 bug 或者其他。

[通过这里查看已有的 issues 和提交 Bug。](https://github.com/pili-engineering/PLDroidMediaStreaming/issues)

