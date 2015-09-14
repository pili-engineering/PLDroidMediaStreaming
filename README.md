# PLDroidCameraStreaming

PLDroidCameraStreaming 是一个适用于 Android 的 RTMP 直播推流 SDK，可高度定制化和二次开发。特色是支持 Android Camera 画面捕获并进行 H.264 硬编码， 以及支持 Android 麦克风音频采样并进行 AAC 硬编码；同时，还实现了一套可供开发者选择的编码参数集合，以便灵活调节相应的分辨率和码率。借助 PLDroidCameraStreaming ，开发者可以快速构建一款类似 [Meerkat](https://meerkatapp.co/) 或 [Periscope](https://www.periscope.tv/) 的 Android 直播应用。

## 功能特性
  - [x] 支持 MediaCodec 硬编码 
  - [x] 支持 AAC 音频编码 
  - [x] 支持 H264 视频编码 
  - [x] 内置生成安全的 RTMP 推流地址
  - [x] 支持 RTMP 协议推流
  - [x] 支持自适应码率
  - [x] 支持截帧功能
  - [x] 支持 ARM, ARMv7a, ARM64v8a
  - [x] Android Min API 18 
  - [x] 支持前后置摄像头，以及动态切换 
  - [x] 支持自动对焦
  - [x] 支持闪光灯操作
  - [x] 支持纯音频推流，以及后台运行

## 内容摘要
- [使用方法](#使用方法)
  - [配置工程](#项目配置)
  - [权限](#权限)
  - [示例代码](#示例代码)
- [版本历史](#版本历史)

## 使用方法
### 项目配置
从 `releases/` 目录获取：

- pldroid-camera-streaming-xxx.jar
- armeabi/libpldroid_streaming_core.so
- armeabi-v7a/libpldroid_streaming_core.so
- arm64-v8a/libpldroid_streaming_core.so

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

`streamJsonStrFromServer` 是由服务端返回的一段 JSON String，该 JSON String 描述了 `Stream` 的结构。通常，您可以使用 Pili 服务端 SDK 的 `getStream(streamId)` 方法来获取一个 `Stream` 对象，在服务端并将该对象以 JSON String 格式输出，该输出即是 `streamJsonStrFromServer` 变量的内容。例如，一段 JSON 格式的 `Stream` 内容如下：

```
{
    "id": "z1.live.55920c19fb16df0cbf00af8e",
    "hub": "live",
    "title": "55910c13fb16df0cbf00af8e",
    "publishKey": "b06c7427b454762e",
    "publishSecurity": "dynamic",
    "hosts" : {
        "publish" : {
            "rtmp"   : "xxx.pub.z1.pili.qiniup.com"
        },
        "play"    : {
            "hls"    : "xxx.hls1.z1.pili.qiniucdn.com",
            "rtmp"   : "xxx.live1.z1.pili.qiniucdn.com"
        }
    }
    // ...
}
```
然后根据 `streamJsonStrFromServer` 构造 `JSONObject` 类型的对象 `streamJson`。

```JAVA
/*
*
* You should get the streamJson from your server, maybe like this:
*
* Step 1: Get streamJsonStrFromServer from server
* URL url = new URL(yourURL);
* URLConnection conn = url.openConnection();
*
* HttpURLConnection httpConn = (HttpURLConnection) conn;
* httpConn.setAllowUserInteraction(false);
* httpConn.setInstanceFollowRedirects(true);
* httpConn.setRequestMethod("GET");
* httpConn.connect();
*
* InputStream is = httpConn.getInputStream();
* streamJsonStrFromServer = convertInputStreamToString(is);
*
* Step 2: Instantiate streamJson object
* JSONObject streamJson = new JSONObject(streamJsonStrFromServer);
*
*
* Then you can use streamJson to instantiate stream object
* Stream stream = new Stream(streamJson);
*
* */
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
```JAVA
mCameraStreamingManager = new CameraStreamingManager(this, afl, glSurfaceView);
mCameraStreamingManager.onPrepare(setting, profile);
mCameraStreamingManager.setStreamingStateListener(this);
```

- Pure Audio Streaming
```JAVA
mCameraStreamingManager = new CameraStreamingManager(this);
mCameraStreamingManager.onPrepare(setting, profile);
mCameraStreamingManager.setStreamingStateListener(this);
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

>纯音频推流支持后台运行，你只需要控制好 `onPause()` 及 `onDestory()` 周期函数即可。

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

在调用 `captureFrame` 的时候，您需要传入 width 和 height，以及 `FrameCapturedCallback`。SDK 完成截帧之后，会回调 `onFrameCaptured` ，并将结果以参数的形式返回给调用者。

> 调用者有义务对 Bitmap 进行释放

```
mCameraStreamingManager.captureFrame(w, h, new FrameCapturedCallback() {
    @Override
    public void onFrameCaptured(Bitmap bmp) {
    
    }
}
```

10) FULL & REAL mode

在获得 `AspectFrameLayout` 对象之后，您可以调用 `setShowMode` 方法来选择您需要的显示方式 。

- SHOW_MODE.FULL，可以全屏显示（没有黑边），但是预览的图像和播放的效果有出入
- SHOW_MODE.REAL，所见即所得

```
AspectFrameLayout afl = (AspectFrameLayout) findViewById(R.id.cameraPreview_afl);
afl.setShowMode(AspectFrameLayout.SHOW_MODE.FULL);
```

11) `setNativeLoggingEnabled(enabled)`

当 enabled 设置为 true ，SDK Native 层的 log 将会被打开；当设置为 false，SDK Native 层的 log 将会被关闭。默认处于打开状态。

```JAVA
mCameraStreamingManager.setNativeLoggingEnabled(false);
```

### 版本历史

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
