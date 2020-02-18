# PLDroidMediaStreaming Release Notes for 2.2.4.2

本次更新:

## 版本

- 发布 pldroid-media-streaming-2.2.4.2.jar
- 更新 libpldroid_mmprocessing.so

## 功能

- `StreamingProfile` 增加 `setYuvFilterMode` 接口

```java
    /**
     * filter mode for libyuv
     */
    public enum YuvFilterMode {
        None,        // Point sample; Fastest.
        Linear,      // Filter horizontally only.
        Bilinear,    // Faster than box, but lower quality scaling down.
        Box          // Highest quality.
    }

    /**
     * Sets the YUV filter mode
     * @param mode the {@link YuvFilterMode}, default: {@link YuvFilterMode#None}
     * @return this
     */
    public StreamingProfile setYuvFilterMode(YuvFilterMode mode)
```

