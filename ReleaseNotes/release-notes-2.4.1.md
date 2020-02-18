# PLDroidMediaStreaming Release Notes for 2.4.1

本次更新:

## 版本

- 发布 pldroid-media-streaming-2.4.1.jar
- 更新 libpldroid_streaming_amix.so
- 更新 libpldroid_streaming_core.so

## 功能

- 新增发送 SEI 功能
- 新增设置 bitmap 水印功能

## 缺陷

- 修复截图场景下的空指针问题
- 修复软编推流水印颜色异常的问题
- 修复非循环混音场景下的崩溃问题

## 注意事项

- 从 v2.3.0 版本开始，增加 libpldroid_streaming_puic.so 库
- libpldroid_streaming_core.so 依赖于 libpldroid_streaming_puic.so，无论是否启用 QUIC 推流，都需要包含 libpldroid_streaming_puic.so 库