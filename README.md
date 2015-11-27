# PLDroidCameraStreaming

PLDroidCameraStreaming 是一个适用于 Android 的 RTMP 直播推流 SDK，可高度定制化和二次开发。特色是同时支持 H.264 软编／硬编和 AAC 软编／硬编。支持 Android Camera 画面捕获，并进行 H.264 编码，以及支持 Android 麦克风音频采样并进行 AAC 编码；还实现了一套可供开发者选择的编码参数集合，以便灵活调节相应的分辨率和码率；同时，SDK 提供数据源回调接口，用户可进行 Filter 处理。借助 PLDroidCameraStreaming ，开发者可以快速构建一款类似 [Meerkat](https://meerkatapp.co/) 或 [Periscope](https://www.periscope.tv/) 的 Android 直播应用。

## 功能特性
  - [x] 支持 H.264 和 AAC 软编（推荐）
  - [x] 支持 H.264 和 AAC 硬编
  - [x] 软编支持 Android Min API 15（Android 4.0.3）及其以上版本
  - [x] 硬编支持 Android Min API 18（Android 4.3）及其以上版本
  - [x] 支持构造带安全授权凭证的 RTMP 推流地址
  - [x] 支持 RTMP 封包及推流
  - [x] 支持 RTMP 推流自适应网络质量动态切换码率或自定义策略
  - [x] 支持数据源回调接口，可自定义 Filter (滤镜) 特效处理
  - [x] 支持前后置摄像头，以及动态切换
  - [x] 支持自动对焦
  - [x] 支持闪光灯操作
  - [x] 支持纯音频推流，以及后台运行
  - [x] 支持截帧功能
  - [x] 支持 ARM, ARMv7a, ARM64v8a, X86 主流芯片体系架构
  
## 测试通过的机型清单
以下是目前已经在真机上验证通过的机型列表，您也可以在 Issue 中添加您测试通过的机型信息，感谢！

| 品牌 | 机型 | 版本 |
|---|---|---|
| 三星 | GALAXY S5 | 5.0 |
| 三星 | GALAXY S4 i9500 | 5.0.0 |
| 三星 | GALAXY Trend Duos S7562C | 4.1.2 |
| 三星 | GALAXY Note II N7108 | 4.3 |
| 三星 | GALAXY Premier I9260 | 4.1.2 |
| 三星 | GALAXY SIII I9300 | 4.3 |
| 三星 | GALAXY Note2 | 4.3 |
| 三星 | SM-A5000 | 4.4.4 |
| 三星 | SM-N9008V | 5.0 |
| 三星 | SM-G7108V | 4.3 |
| 三星 | SM-G7106 | 4.3 |
| 三星 | GT-I9508 | 4.4.2 |
| 三星 | SM-G9006V | 4.4.2  |
| 三星 | SM-N7508V | 4.3 |
| 三星 | SM-G9008V  | 4.4.2 |
| 三星 | SM-N9005 | 5.0 |
| 三星 | SM-G5308W | 4.4.4 |
| 三星 | SM-G9008W | 4.4.2 |
| 三星 | SM-N9108V | 5.0.1 |
| 三星 | SM-N9008 | 4.4.2 |
| 华为 | Y523 | 4.4.2 |
| 华为 | 荣耀畅玩4X CHE-TL00 | 4.4.2 |
| 华为 | H30-T00 | 4.4.2 |
| 华为 | P7-L07 | 4.4.2 |
| 华为 | p8 | 5.0 |
| 华为 | u9500 | 4.0.3 |
| 华为 | Honor 6 | 4.4.2 |
| 华为 | 畅玩4 | 4.4.4 |
| 华为 | C199 | v4.4.2 |
| 小米 | Xiaomi 2A | 4.1.1 |
| 小米 | Xiaomi 3 | 4.4.4 |
| 小米 | Xiaomi 2S | 5.0.2 |
| 小米 | 4L TE-CMCC | 4.4.4 |
| 红米 | NOTE 1 TD | 4.4.2 |
| 红米 | HM 1S | 4.4.2 |
| 红米 | NOTE | 4.4.4 |
| 魅族 | Mx 4 Pro | 4.4.2 |
| 魅族 | Mx 5 | 5.0.1 |
| vivo | X5M | 5.0.2 |
| vivo | Y17W | 4.2.2 |
| vivo | Y17T | 4.2.2 |
| vivo | S7T | 4.2.2 |
| vivo | X3V |  4.4.2 |
| oppo | R7 | 5.1.1 |
| oppo | R2017  | 4.3 |
| oppo | R831S | 4.3 |
| oppo | R8107 | 4.4.4 |
| oppo | R8007 | 4.3 |
| oppo | X9007 | 4.3 |
| 酷派 | Coolpad 大神1 8297 | 4.2.2 |
| 酷派 | Coolpad 8730L | 4.3 |
| 酷派 | Coolpad 8675 | 4.4.2 |
| 酷派 | 8729 | 4.3 |
| 酷派 | 7620L | 4.3 |
| 索尼 | Z3 | 5.0.2 |
| 金立 | GN9000L | 4.3 |
| Alcatel One Touch | 6040D | 4.4.2 |
| 美图 | M4 | 4.4.4 |
| 锤子 | SM701 | 4.4.2 |

## 内容摘要
- [使用方法](#使用方法)
  - [配置工程](#项目配置)
  - [权限](#权限)
  - [示例代码](#示例代码)
- [版本历史](#版本历史)

## 使用方法
### 项目配置
从 `releases/` 目录获取：

```
pldroid-camera-streaming-xxx.jar

arm64-v8a/libpldroid_streaming_aac_encoder.so
arm64-v8a/libpldroid_streaming_core.so
arm64-v8a/libpldroid_streaming_h264_encoder.so

armeabi/libpldroid_streaming_aac_encoder.so
armeabi/libpldroid_streaming_core.so
armeabi/libpldroid_streaming_h264_encoder.so

armeabi-v7a/libpldroid_streaming_aac_encoder.so
armeabi-v7a/libpldroid_streaming_core.so
armeabi-v7a/libpldroid_streaming_h264_encoder.so

x86/libpldroid_streaming_aac_encoder.so
x86/libpldroid_streaming_core.so
x86/libpldroid_streaming_h264_encoder.so
```

并在项目中加入对应的 jar / so 文件的依赖关系，可参考 [PLDroidCameraStreamingDemo][1] 中的做法。

### 权限
在项目开始前，您需要在 `AndroidManifest.xml` 中添加如下权限：

```XML
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.WAKE_LOCK" />

<uses-feature android:name="android.hardware.camera.autofocus" />
<uses-feature android:glEsVersion="0x00020000" android:required="true" />
```
可参考 [PLDroidCameraStreamingDemo][2] 中的做法。

### 示例代码
1) 编写布局文件
```XML
<com.pili.pldroid.streaming.widget.AspectFrameLayout
    android:id="@+id/cameraPreview_afl"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_centerInParent="true" >

    <android.opengl.GLSurfaceView
        android:id="@+id/cameraPreview_surfaceView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_gravity="center" />
</com.pili.pldroid.streaming.widget.AspectFrameLayout>
```

2) 初始化 `Layout` 和 `View`
```JAVA
AspectFrameLayout afl = (AspectFrameLayout) findViewById(R.id.cameraPreview_afl);
GLSurfaceView glSurfaceView = (GLSurfaceView) findViewById(R.id.cameraPreview_surfaceView);
```

3) 实例化并初始化 `StreamingProfile`、`Stream`、`CameraStreamingSetting`

`streamJsonStrFromServer` 是由服务端返回的一段 JSON String，该 JSON String 描述了 `Stream` 的结构。通常，您可以使用 Pili 服务端 SDK 的 `getStream(streamId)` 方法来获取一个 `Stream` 对象，在服务端并将该对象以 JSON String 格式输出，该输出即是 `streamJsonStrFromServer` 变量的内容。从业务服务器获取对应的 `Stream` 可参考 [MainActivity.java](/PLDroidCameraStreamingDemo/app/src/main/java/com/pili/pldroid/streaming/camera/demo/MainActivity.java) 中的 `requestByHttpPost` 方法。

然后根据 `streamJsonStrFromServer` 构造 `JSONObject` 类型的对象 `streamJson`。

```JAVA
String streamJsonStrFromServer = "stream json string from your server";

JSONObject streamJson = null;
try {
    streamJson = new JSONObject(streamJsonStrFromServer);
} catch (JSONException e) {
    e.printStackTrace();
}
        
Stream stream = new Stream(streamJson);

StreamingProfile profile = new StreamingProfile();
profile.setVideoQuality(StreamingProfile.VIDEO_QUALITY_MEDIUM1)
       .setAudioQuality(StreamingProfile.AUDIO_QUALITY_HIGH2)
       .setStream(stream);

CameraStreamingSetting setting = new CameraStreamingSetting();
setting.setCameraId(Camera.CameraInfo.CAMERA_FACING_BACK)
       .setContinuousFocusModeEnabled(true)
       .setCameraPrvSizeLevel(CameraStreamingSetting.PREVIEW_SIZE_LEVEL.MEDIUM)
       .setCameraPrvSizeRatio(CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_4_3);
```

- SDK 预定义的 Video Quality 列表：
```JAVA
public static final int VIDEO_QUALITY_LOW1;
public static final int VIDEO_QUALITY_LOW2;
public static final int VIDEO_QUALITY_LOW3;

public static final int VIDEO_QUALITY_MEDIUM1;
public static final int VIDEO_QUALITY_MEDIUM2;
public static final int VIDEO_QUALITY_MEDIUM3;

public static final int VIDEO_QUALITY_HIGH1;
public static final int VIDEO_QUALITY_HIGH2;
public static final int VIDEO_QUALITY_HIGH3;
```

- SDK 预定义的 Audio Quality 列表：
```JAVA
public static final int AUDIO_QUALITY_LOW1;
public static final int AUDIO_QUALITY_LOW2;

public static final int AUDIO_QUALITY_MEDIUM1;
public static final int AUDIO_QUALITY_MEDIUM2;

public static final int AUDIO_QUALITY_HIGH1;
public static final int AUDIO_QUALITY_HIGH2;
```

- SDK 预定义的 Video Encoding Size 列表：
```JAVA
public static final int VIDEO_ENCODING_SIZE_QVGA;
public static final int VIDEO_ENCODING_SIZE_VGA;
public static final int VIDEO_ENCODING_SIZE_HD;
public static final int VIDEO_ENCODING_SIZE_FHD;
```

- Video Quality 配置表

| Level | Fps | Video Bitrate(Kbps) |
|---|---|---|
|VIDEO_QUALITY_LOW1|12|150|
|VIDEO_QUALITY_LOW2|15|264|
|VIDEO_QUALITY_LOW3|15|350|
|VIDEO_QUALITY_MEDIUM1|30|512|
|VIDEO_QUALITY_MEDIUM2|30|800|
|VIDEO_QUALITY_MEDIUM3|30|1000|
|VIDEO_QUALITY_HIGH1|30|1200|
|VIDEO_QUALITY_HIGH2|30|1500|
|VIDEO_QUALITY_HIGH3|30|2000|

- Audio Quality 配置表

| Level | Audio Bitrate(Kbps) | Audio Sample Rate(Hz)|
|---|---|---|
|AUDIO_QUALITY_LOW1|18|44100|
|AUDIO_QUALITY_LOW2|24|44100|
|AUDIO_QUALITY_MEDIUM1|32|44100|
|AUDIO_QUALITY_MEDIUM2|48|44100|
|AUDIO_QUALITY_HIGH1|96|44100|
|AUDIO_QUALITY_HIGH2|128|44100|

- Video Encoding Size 配置表

| Level | Resolution(16:9) | Resolution(4:3)|
|---|---|---|
|VIDEO_ENCODING_SIZE_QVGA|480 x 272|320 x 240|
|VIDEO_ENCODING_SIZE_VGA|848 x 480|640 x 480|
|VIDEO_ENCODING_SIZE_HD|1280 x 720|960 x 720|
|VIDEO_ENCODING_SIZE_FHD|1920 x 1088|1440 x 1088|

>若设置一个未被 SDK 支持的 quality，将会得到 `IllegalArgumentException("Cannot support the quality:" + quality)` 异常。

- SDK 预定义的 preivew size level 列表：
```
CameraStreamingSetting.PREVIEW_SIZE_LEVEL.SMALL
CameraStreamingSetting.PREVIEW_SIZE_LEVEL.MEDIUM
CameraStreamingSetting.PREVIEW_SIZE_LEVEL.LARGE
```

- SDK 预定义的 preview size ratio 列表：
```
CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_4_3
CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_16_9
```

SDK 会根据您设置的 ratio 、 level 从系统支持的 preview size 列表中找到最佳的 size

>若缺少上述步骤，SDK 默认使用如下设置：
```
Camera Id      : Camera.CameraInfo.CAMERA_FACING_BACK
Publish Url    : Environment.getExternalStorageDirectory().getAbsolutePath() + "/pldroid-recording.mp4"
Audio Quality  : StreamingProfile.AUDIO_QUALITY_LOW1
Video Quality  : StreamingProfile.VIDEO_QUALITY_LOW1
Prv Size Level : MEDIUM
Prv Size Ratio : RATIO_16_9
```

4) 实例化并初始化核心类 `CameraStreamingManager`
- Camera Streaming

硬编初始化 
```JAVA
mCameraStreamingManager = new CameraStreamingManager(this, afl, glSurfaceView, EncodingType.HW_VIDEO_WITH_HW_AUDIO_CODEC);
mCameraStreamingManager.onPrepare(setting, profile);
mCameraStreamingManager.setStreamingStateListener(this);
```
软编初始化 

```JAVA
mCameraStreamingManager = new CameraStreamingManager(this, afl, glSurfaceView, EncodingType.SW_VIDEO_WITH_SW_AUDIO_CODEC);
mCameraStreamingManager.onPrepare(setting, profile);
mCameraStreamingManager.setStreamingStateListener(this);
```

- 纯音频推流

软编初始化 
```JAVA
mCameraStreamingManager = new CameraStreamingManager(this, EncodingType.SW_AUDIO_CODEC);
mCameraStreamingManager.onPrepare(profile);
mCameraStreamingManager.setStreamingStateListener(this);
```

硬编初始化
```JAVA
mCameraStreamingManager = new CameraStreamingManager(this, EncodingType.HW_AUDIO_CODEC);
mCameraStreamingManager.onPrepare(setting, profile);
mCameraStreamingManager.setStreamingStateListener(this);
```

目前支持的 `EncodingType` 有：
```
HW_VIDEO_WITH_HW_AUDIO_CODEC,
SW_VIDEO_WITH_HW_AUDIO_CODEC,
SW_VIDEO_WITH_SW_AUDIO_CODEC,
SW_AUDIO_CODEC,
HW_AUDIO_CODEC,
SW_VIDEO_CODEC,
HW_VIDEO_CODEC
```

您需要实现 `StreamingStateListener`，
以便通过回调函数 `onStateChanged` 接收如下消息：
- STATE.PREPARING
- STATE.READY
- STATE.CONNECTING
- STATE.STREAMING
- STATE.SHUTDOWN
- STATE.IOERROR
- STATE.NETBLOCKING
- STATE.CAMERA_SWITCHED
- STATE.TORCH_INFO
- STATE.CONNECTION_TIMEOUT
- STATE.SENDING_BUFFER_EMPTY
- STATE.SENDING_BUFFER_FULL
- STATE.DISCONNECTED
- STATE.NO_SUPPORTED_PREVIEW_SIZE

通过 `onStateHandled` 接收如下消息：
- STATE.SENDING_BUFFER_HAS_FEW_ITEMS
- STATE.SENDING_BUFFER_HAS_MANY_ITEMS

>您需要注意的是，`onStateChanged` 回调函数可能被非 UI 线程调用，可参考 [CameraStreamingActivity][3] 

5) 开始推流
```JAVA
// should be invoked after getting STATE.READY message
mCameraStreamingManager.startStreaming();
```

6) 停止推流
```JAVA
mCameraStreamingManager.stopStreaming();
```

7) `CameraStreamingManager` 另外几个重要的状态周期函数 `onResume()`、`onPause()`、`onDestory()`

如果 `CameraStreamingManager` 存在于某一个 Activity 中，建议在 Activity 的 `onResume()`、`onPause()`、`onDestory()` 中分别进行调用 `CameraStreamingManager` 的周期函数，即：

```JAVA
@Override
protected void onResume() {
  super.onResume();
  mCameraStreamingManager.onResume();
}

@Override
protected void onPause() {
  super.onPause();
  mCameraStreamingManager.onPause();
  getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
}
    
@Override
protected void onDestroy() {
  super.onDestroy();
  mCameraStreamingManager.onDestroy();
}
```

>纯音频推流支持后台运行，只需要控制好 `onPause()` 及 `onDestory()` 周期函数即可。

8) 自适应码率

下面是 `SendingBufferProfile` 的相关配置：
```
public static final float LOW_THRESHOLD_MIN = 0;
public static final float LOW_THRESHOLD_MAX = 1.0f;
public static final float LOW_THRESHOLD_DEFAULT = 0.2f;

public static final float HIGH_THRESHOLD_MIN = 0;
public static final float HIGH_THRESHOLD_MAX = 1.0f;
public static final float HIGH_THRESHOLD_DEFAULT = 0.8f;

public static final float DURATION_LIMIT_MIN = HIGH_THRESHOLD_MAX + 0.1f;
public static final float DURATION_LIMIT_MAX = 5.0f;
public static final float DURATION_LIMIT_DEFAULT = 3.0f;

public static final long LOW_THRESHOLD_TIMEOUT_MIN     = 10 * 1000; // ms
public static final long DEFAULT_LOW_THRESHOLD_TIMEOUT = 60 * 1000; // ms

// [LOW_THRESHOLD_MIN, LOW_THRESHOLD_MAX] && [LOW_THRESHOLD_MIN, high - 0.1)
private float mLowThreshold;

// [HIGH_THRESHOLD_MIN, HIGH_THRESHOLD_MAX] && (low + 0.1, HIGH_THRESHOLD_MAX]
private float mHighThreshold;

// [DURATION_LIMIT_MIN, DURATION_LIMIT_MAX]
private float mDurationLimit;

// [LOW_THRESHOLD_TIMEOUT_MIN, Long.MAX_VALUE)
// To measure the low buffering case
private long mLowThresholdTimeout;
```

您需要首先构造 `SendingBufferProfile` ，并传入 `LowThreshold`(s), `HighThreshold`(s), `DurationLimit`(s) 和 `LowThresholdTimeout`(ms)

- LowThreshold 是 `SENDING_BUFFER_HAS_FEW_ITEMS` 消息的阀值。在收到 `SENDING_BUFFER_HAS_FEW_ITEMS` 后，表明 SendingBuffer 中有 LowThreshold 的 buffer，您可以在此回调中添加提升 quality 的相关逻辑。

- HighThreshold 是 `SENDING_BUFFER_HAS_MANY_ITEMS` 消息的阀值。在收到 `SENDING_BUFFER_HAS_MANY_ITEMS` 后，表明 SendingBuffer 中有 HighThreshold 的 buffer，您可以在此回调中添加降低 quality 的相关逻辑。

- DurationLimit 是 `SENDING_BUFFER_FULL` 消息的阀值。在收到 FULL 之后，SDK 将会开始进行丢帧处理。

- SDK 检测到 SendingBuffer 达到 LowThreshold 后，会通过 LowThresholdTimeout 来决定何时回调 `SENDING_BUFFER_HAS_FEW_ITEMS`。当前 LowThresholdTimeout 最低为 `LOW_THRESHOLD_TIMEOUT_MIN`，默认为 `DEFAULT_LOW_THRESHOLD_TIMEOUT`。

下面是 Demo 中的处理逻辑，在接收到 `SENDING_BUFFER_HAS_FEW_ITEMS` 后，会提升 quality，设定之后，需要调用 `notifyProfileChanged` ；在接收到 `SENDING_BUFFER_HAS_MANY_ITEMS` 后，会降低 quality，同理，需要调用 `notifyProfileChanged`。

> 在 `onStateHandled` 中，您可以直接返回 false，表明不执行自适应码率的策略

```
case CameraStreamingManager.STATE.SENDING_BUFFER_HAS_FEW_ITEMS:
    mProfile.improveVideoQuality(1);
    mCameraStreamingManager.notifyProfileChanged(mProfile);
    return true;
case CameraStreamingManager.STATE.SENDING_BUFFER_HAS_MANY_ITEMS:
    mProfile.reduceVideoQuality(1);
    mCameraStreamingManager.notifyProfileChanged(mProfile);
    return true;
```

9) 截帧

在调用 `captureFrame` 的时候，您需要传入 width 和 height，以及 `FrameCapturedCallback`，如果传入的 width 或者 height 小于等于 0，SDK 返回的 `Bitmap` 将会是预览的尺寸 。SDK 完成截帧之后，会回调 `onFrameCaptured`，并将结果以参数的形式返回给调用者。

> 注意：调用者有义务对 Bitmap 进行回收释放

```
mCameraStreamingManager.captureFrame(w, h, new FrameCapturedCallback() {
    @Override
    public void onFrameCaptured(Bitmap bmp) {
    
    }
}
```

10) Filter 实现
您可以通过如下 Callback 获取数据源或 Texture id，并定制化滤镜效果。

- `onPreviewFrame` 会回调 NV21 格式的 YUV 数据，您 Filter 之后，会进行后续的编码和封包等操作。
- 该过程是一个同步过程。
- `onPreviewFrame` 仅在 Streaming 状态下被回调。

```JAVA
public interface StreamingPreviewCallback {
    void onPreviewFrame(byte[] datas, Camera camera);
}
```

`onDrawFrame` 会回调 Texture id，您处理完之后，返回新的 Texture id 进行显示。该过程是一个同步过程。
- 如下四个回调均执行在 GL rendering thread
- 如果 `onDrawFrame` 直接返回 `texId`，代表放弃 Filter 处理；否则 `onDrawFrame` 应该返回一个您处理过的纹理 ID
- 自定义的 Texture id 应该是 `GLES20.GL_TEXTURE_2D` 类型

```JAVA
public interface SurfaceTextureCallback {
    void onSurfaceCreated();
    void onSurfaceChanged(int width, int height);
    void onSurfaceDestroyed();
    int onDrawFrame(int texId, int width, int height);
}
```

11) FULL & REAL mode

在获得 `AspectFrameLayout` 对象之后，您可以调用 `setShowMode` 方法来选择您需要的显示方式 。

- SHOW_MODE.FULL，可以全屏显示（没有黑边），但是预览的图像和播放的效果有出入
- SHOW_MODE.REAL，所见即所得

```
AspectFrameLayout afl = (AspectFrameLayout) findViewById(R.id.cameraPreview_afl);
afl.setShowMode(AspectFrameLayout.SHOW_MODE.FULL);
```

12) 自定义 SDK 涉及的 Shared Library Name

如果您将 so 更名如下（前者为更新前的 so，后者为您更新后的 so）：

- libpldroid_streaming_aac_encoder.so  -> libpldroid_streaming_aac_encoder_xxx.so
- libpldroid_streaming_core.so         -> libpldroid_streaming_core_xxx.so
- libpldroid_streaming_h264_encoder.so -> libpldroid_streaming_h264_encoder_xxx.so

那么您需要在 `CameraStreamingManager` 构造前调用如下代码：

```
SharedLibraryNameHelper.getInstance().renameSharedLibrary(
        SharedLibraryNameHelper.PLSharedLibraryType.PL_SO_TYPE_AAC, "pldroid_streaming_aac_encoder_xxx");

SharedLibraryNameHelper.getInstance().renameSharedLibrary(
        SharedLibraryNameHelper.PLSharedLibraryType.PL_SO_TYPE_CORE, "pldroid_streaming_core_xxx");

SharedLibraryNameHelper.getInstance().renameSharedLibrary(
        SharedLibraryNameHelper.PLSharedLibraryType.PL_SO_TYPE_H264, "pldroid_streaming_h264_encoder_xxx");
```

或带路径的方式
```
SharedLibraryNameHelper.getInstance().renameSharedLibrary(
        SharedLibraryNameHelper.PLSharedLibraryType.PL_SO_TYPE_AAC, getApplicationInfo().nativeLibraryDir + "/libpldroid_streaming_aac_encoder_xxx.so");

SharedLibraryNameHelper.getInstance().renameSharedLibrary(
        SharedLibraryNameHelper.PLSharedLibraryType.PL_SO_TYPE_CORE, getApplicationInfo().nativeLibraryDir + "/libpldroid_streaming_core_xxx.so");

SharedLibraryNameHelper.getInstance().renameSharedLibrary(
        SharedLibraryNameHelper.PLSharedLibraryType.PL_SO_TYPE_H264, getApplicationInfo().nativeLibraryDir + "/libpldroid_streaming_h264_encoder_xxx.so");
```

13）软编的 `EncoderRCModes`
目前支持的类型：
- EncoderRCModes.QUALITY_PRIORITY: 质量优先，实际的码率可能高于设置的码率
- EncoderRCModes.BITRATE_PRIORITY: 码率优先，更精确地码率控制

可通过 `StreamingProfile` 的 `setEncoderRCMode` 方法进行设置，如下：
```JAVA
StreamingProfile profile;
profile.setEncoderRCMode(StreamingProfile.EncoderRCModes.QUALITY_PRIORITY);
```

14）`StreamingSessionListener`
该 Listener 的原型如下：
```
public interface StreamingSessionListener {
    boolean onRecordAudioFailedHandled(int code);
    boolean onRestartStreamingHandled(int code);
}
```
您可以实现 `StreamingSessionListener`，比如：
```
@Override
public boolean onRecordAudioFailedHandled(int err) {
    mCameraStreamingManager.updateEncodingType(CameraStreamingManager.EncodingType.SW_VIDEO_CODEC);
    mCameraStreamingManager.startStreaming();
    return true;
}

@Override
public boolean onRestartStreamingHandled(int err) {
    return mCameraStreamingManager.startStreaming();
}
```

在消费了 `onRecordAudioFailedHandled` 或 `onRestartStreamingHandled` 之后，您应该返回 true 通知 SDK；若不做任何处理，返回 false。
- `onRecordAudioFailedHandled`：在 Audio 数据读取失败后，会回调该方法，如前面的代码，您可以继续继续纯视频推流
- `onRestartStreamingHandled`：在网络链接失败之后，SDK 会回调 `STATE.DISCONNECTED` 消息，并在合适的时刻回调 `onRestartStreamingHandled` 方法，您可以在此方法中安全地实现重连策略。若网络不可达，会回调 `STATE.IOERROR`。

15) `setNativeLoggingEnabled(enabled)`

当 enabled 设置为 true ，SDK Native 层的 log 将会被打开；当设置为 false，SDK Native 层的 log 将会被关闭。默认处于打开状态。

```JAVA
mCameraStreamingManager.setNativeLoggingEnabled(false);
```

### 版本历史

* 1.4.3 ([Release Notes][23])
  - 发布 pldroid-camera-streaming-1.4.3.jar
  - 更新 libpldroid_streaming_core.so
  - 新增 `SharedLibraryNameHelper` 绝对路径加载方式
  - 新增 `StreamingSessionListener`，可方便安全地实现重连策略及 Audio 数据获取失败时的策略
  - 新增 `EncodingType` 支持
  - 修复硬编模式下，多次切换前后置摄像头 crash 问题
  - 修复硬编模式下，部分机型截图 crash 问题
  - 修复 metadata 格式问题
  - 修复软编模式下，推流过程中概率性 crash 问题
  - 修复概率性无视频帧问题
  - 更新 demo 展示代码
  - 增加支持的机型信息 

* 1.4.1 ([Release Notes][22])
  - 发布 pldroid-camera-streaming-1.4.1.jar
  - 更新 libpldroid_streaming_core.so
  - 新增 libpldroid_streaming_aac_encoder.so 和 libpldroid_streaming_h264_encoder.so
  - 新增 H.264 和 AAC 软编支持
  - 新增软编数据源回调接口，可定制化 Filter (滤镜) 特效处理
  - 修复硬编部分机型 crash 问题
  - 修复硬编切换前后置时长异常问题
  - 更新 demo 样例代码

* 1.3.9 ([Release Notes][21])
  - 发布 pldroid-camera-streaming-1.3.9.jar
  - 更新 libpldroid_streaming_core.so
  - 增加 x86 支持
  - 新增 x86/libpldroid_streaming_core.so
  - 优化内存，减少内存抖动，增强稳定性
  - 修复 onResume 之后快速 onPause 导致的 crash 问题
  - 修复部分机型截图 crash 问题
  - 修复部分机型切换前后置摄像头之后，导致切片异常问题
  - 修复网络异常导致的 crash 问题(issue 54)

* 1.3.8 ([Release Notes][20])
  - 发布 pldroid-camera-streaming-1.3.8.jar
  - 更新 libpldroid_streaming_core.so
  - 优化切换前后置摄像头数据重发时间，增强推流过程中切换前后置摄像头的稳定性
  - 优化内存使用，避免 OOM
  - 修复部分机型概率性 crash 问题
  - 兼容 supportedPreviewSizeList 为空的机型

* 1.3.7 ([Release Notes][19])
  - 发布 pldroid-camera-streaming-1.3.7.jar
  - 更新 libpldroid_streaming_core.so
  - 修复部分机型概率性 crash 问题
  - 修复部分机型前后置 camera 切换的 crash 问题
  - 兼容无前置 camera 的机型

* 1.3.6 ([Release Notes][18])
  - 发布 pldroid-camera-streaming-1.3.6.jar
  - 更新 libpldroid_streaming_core.so
  - 优化 video stream 流畅度
  - 修复概率性断流问题
  - 修复部分机型推流过程中，概率性 crash 问题
  - 修复部分机型切换前后置摄像头过程中，概率性 crash 问题

* 1.3.5 ([Release Notes][17])
  - 发布 pldroid-camera-streaming-1.3.5.jar
  - 更新 libpldroid_streaming_core.so
  - 修复部分机型音视频不同步问题
  - 分离 preview size 与 encoding size
  - 新增 `setEncodingSizeLevel` API，并提供 encoding size 参数列表
  - 修复部分机型花屏问题
  - 修复前后置摄像头切换概率性断流问题
  - 修复概率性 crash 问题

* 1.3.4 ([Release Notes][16])
  - 发布 pldroid-camera-streaming-1.3.4.jar
  - 更新 libpldroid_streaming_core.so
  - 修复采用 ART 运行时的 Android 机型的 crash 问题
  - 修复封包不兼容的问题

* 1.3.3 ([Release Notes][15])
  - 发布 pldroid-camera-streaming-1.3.3.jar
  - 删除 arm64-v8a/libpldroid_ffmpegbridge.so 以及 armeabi-v7a/libpldroid_ffmpegbridge.so
  - 新增 armeabi 支持
  - 新增 arm64-v8a/libpldroid_streaming_core.so, armeabi-v7a/libpldroid_streaming_core.so 和 armeabi/libpldroid_streaming_core.so
  - 体积裁剪数十倍，动态链接库裁剪至 69KB
  - 完全移除 FFmpeg 依赖
  - 修复推流过程中，切换前后置断流问题
  - 修复自适应码率过程中，切换 quality 断流问题
  - 修复前后置切换概率性 crash 问题

* 1.3.2 ([Release Notes][14])
  - 发布 pldroid-camera-streaming-1.3.2.jar
  - 修复输入法弹起导致预览画面调整的问题

* 1.3.1 ([Release Notes][13])
  - 发布 pldroid-camera-streaming-1.3.1.jar
  - 增加 arm64-v8a 支持，新增 arm64-v8a/libpldroid_ffmpegbridge.so
  - 更新 armeabi-v7a/libpldroid_ffmpegbridge.so
  - 新增切换 `Stream` 接口：setStreamingProfile
  - 新增 `setLocalFileAbsolutePath` 接口
  - 修复横屏下，经过特殊操作，Camera 预览显示异常的问题

* 1.3.0 ([Release Notes][12])
  - 发布 pldroid-camera-streaming-1.3.0.jar
  - 新增自适应码率功能
  - 新增截帧接口
  - 新增 Preview Layout `REAL/FULL` mode，解决显示黑边问题
  - 修复 IOS 和 Android 使用同一个 stream 时，导致 IOS 无法正常推流的问题
  - 修复部分机型切换前后置 crash 问题
  - 新增自适应码率演示代码
  - 新增截帧演示代码
  - 新增 REAL/FULL mode 演示代码

* 1.2.3 ([Release Notes][11])
  - 发布 pldroid-camera-streaming-1.2.3.jar
  - 新增 Audio quality 和 Video quality 配置项，可自由组合音视频码率参数
  - 新增 Video quality 设置接口 `setVideoQuality`
  - 新增 Audio quality 设置接口 `setAudioQuality`
  - 优化 jar 包，减少约 30% 体积

* 1.2.2 ([Release Notes][10])
  - 发布 pldroid-camera-streaming-1.2.2.jar
  - 更新 libpldroid_ffmpegbridge.so
  - 修复概率性的 crash 问题
  - 添加 `STATE.CONNECTION_TIMEOUT` 状态
  - 修复部分机型因连接错误而导致屏幕 Hang 住
  - 在 UI 层对点击事件加入保护逻辑，避免快速点击导致应用 crash

* 1.2.1 ([Release Notes][9])
  - 发布 pldroid-camera-streaming-1.2.1.jar
  - 更新 libpldroid_ffmpegbridge.so
  - 优化内存问题，修复 OOM 异常
  - 优化 Quality 配置
  - 添加 `setNativeLoggingEnabled()` 接口

* 1.2.0 ([Release Notes][8])
  - 发布 pldroid-camera-streaming-1.2.0.jar
  - 更新 libpldroid_ffmpegbridge.so
  - 更新 Stream 设置接口：`setStream(stream)`
  - 添加 Camera 切换接口：`switchCamera`
  - 修复 Android L crash 问题
  - 添加 Camera 切换状态：`STATE.CAMERA_SWITCHED`
  - 添加 Torch 是否支持状态：`STATE.TORCH_INFO`
  - 更新状态回调接口：`onStateChanged(state, extra)`
  - 修复特殊操作的概率性 crash 问题
  - 修复部分机型 `turnLightOn` 及 `turnLightOff` 接口无效问题
  - 修复部分机型点击 Home 按键 crash 问题
  - 修复部分机型因 `PREVIEW_SIZE_LEVEL` 导致 crash 问题
  - 添加 Camera 切换操作演示代码
  - 更新 Torch 组件显示逻辑

* 1.1.0 ([Release Notes][7])
  - 发布 pldroid-camera-streaming-1.1.0.jar
  - 更新 libpldroid_ffmpegbridge.so
  - 优化 ffmpegbridge 模块，降低 libpldroid_ffmpegbridge.so 文件大小
  - 添加纯音频推流支持：添加纯音频推流 `CameraStreamingManager(Context ctx)` 构造函数
  - 纯音频推流支持后台运行
  - 添加 preview size 设定接口：`setCameraPrvSizeLevel` 及 `setCameraPrvSizeRatio`
  - 添加 torch 操作接口： `turnLightOn` 及 `turnLightOff`
  - 添加控制连续自动对焦的接口：`setContinuousFocusModeEnabled`
  - 废弃 `setCameraPreviewSize` 接口
  - 修复部分机型因 preivew size 不支持而导致的 crash 问题
  - 添加 `AudioStreamingActivity` 及 `StreamingBaseActivity`，用来演示纯音频推流
  - 添加 torch 操作演示代码

* 1.0.2 ([Release Notes][6])
  - 发布 pldroid-camera-streaming-1.0.2.jar
  - 修复无 `StreamingStateListener` 情况下的 Crash 问题
  - 修复正常启动后无 READY 消息返回问题
  - 更新 `Stream` 定义，并与服务端保持一致
  - 增加相机正常启动后即开始推流功能
  
* 1.0.1 ([Release Notes][5])
  - 发布 pldroid-camera-streaming-1.0.1.jar
  - 更新 `Stream` 类结构
  - 更新 `Stream` 的构造方式

* 1.0.0 ([Release Notes][4])
  - 发布 PLDroidCameraStreaming v1.0.0

[1]: /PLDroidCameraStreamingDemo
[2]: /PLDroidCameraStreamingDemo/app/src/main/AndroidManifest.xml
[3]: /PLDroidCameraStreamingDemo/app/src/main/java/com/pili/pldroid/streaming/camera/demo/CameraStreamingActivity.java
[4]: /ReleaseNotes/release-notes-1.0.0.md
[5]: /ReleaseNotes/release-notes-1.0.1.md
[6]: /ReleaseNotes/release-notes-1.0.2.md
[7]: /ReleaseNotes/release-notes-1.1.0.md
[8]: /ReleaseNotes/release-notes-1.2.0.md
[9]: /ReleaseNotes/release-notes-1.2.1.md
[10]: /ReleaseNotes/release-notes-1.2.2.md
[11]: /ReleaseNotes/release-notes-1.2.3.md
[12]: /ReleaseNotes/release-notes-1.3.0.md
[13]: /ReleaseNotes/release-notes-1.3.1.md
[14]: /ReleaseNotes/release-notes-1.3.2.md
[15]: /ReleaseNotes/release-notes-1.3.3.md
[16]: /ReleaseNotes/release-notes-1.3.4.md
[17]: /ReleaseNotes/release-notes-1.3.5.md
[18]: /ReleaseNotes/release-notes-1.3.6.md
[19]: /ReleaseNotes/release-notes-1.3.7.md
[20]: /ReleaseNotes/release-notes-1.3.8.md
[21]: /ReleaseNotes/release-notes-1.3.9.md
[22]: /ReleaseNotes/release-notes-1.4.1.md
[23]: /ReleaseNotes/release-notes-1.4.3.md
