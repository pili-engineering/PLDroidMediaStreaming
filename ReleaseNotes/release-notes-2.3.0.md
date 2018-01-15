# PLDroidMediaStreaming Release Notes for 2.3.0

本次更新:

## 版本

- 发布 pldroid-media-streaming-2.3.0.jar
- 新增 libpuicclient.so
- 更新 libpldroid_mmprocessing.so
- 更新 libpldroid_streaming_core.so

## 功能

- 新增录制时动态水印功能
- 新增 QUIC 推流功能

## 缺陷

- 修复金立 M7 黑屏问题
- 修复纯音频推流 pause 后无法 resume 问题
- 修复弱网下 pause 小概率 ANR 问题

## 注意事项

- 从 v2.3.0 版本开始，增加 libpuicclient.so 库
- libpldroid_streaming_core.so 依赖于 libpuicclient.so，无论是否启用 QUIC 推流，都需要包含 libpuicclient.so 库