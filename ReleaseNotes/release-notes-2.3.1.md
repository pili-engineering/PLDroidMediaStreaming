# PLDroidMediaStreaming Release Notes for 2.3.1

本次更新:

## 版本

- 发布 pldroid-media-streaming-2.3.1.jar

## 功能

- 新增推流时动态文字与贴图功能（仅支持 Surface 硬编）

## 注意事项

- 从 v2.3.0 版本开始，增加 libpldroid_streaming_puic.so 库
- libpldroid_streaming_core.so 依赖于 libpldroid_streaming_puic.so，无论是否启用 QUIC 推流，都需要包含 libpldroid_streaming_puic.so 库