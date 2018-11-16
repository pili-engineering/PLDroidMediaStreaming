# PLDroidMediaStreaming Release Notes for 2.4.0

本次更新:

## 版本

- 发布 pldroid-media-streaming-2.4.0.jar

## 功能

- 新增以下两个方法用于 Surface 硬编下，推流前的纹理回调
	*	`MediaStreamingManager.setSurfaceTextureCallback2(SurfaceTextureCallback2)`
	* 	`StreamingManager.setSurfaceTextureCallback2(SurfaceTextureCallback2)`
- 新增以下方法用于设置水印自定义像素大小，不仅限于固定枚举值
	*	`WatermarkSetting.setCustomSize(int width, int height)`

## 注意事项

- 从 v2.3.0 版本开始，增加 libpldroid_streaming_puic.so 库
- libpldroid_streaming_core.so 依赖于 libpldroid_streaming_puic.so，无论是否启用 QUIC 推流，都需要包含 libpldroid_streaming_puic.so 库