# 功能要求 1 · 环境搭建与 HelloWorld 验证记录

> 启普斯 · 迭代 1 · Week 1｜执行人：王家进（总工程师）｜记录日期：2026-11-06

## 一、环境清单（本组实测）

| 项 | 版本 / 配置 |
| --- | --- |
| 操作系统 | Windows（工程路径 `D:\移动应用开发`，含中文） |
| JDK | OpenJDK 17.0.19（Microsoft Build） |
| Android Studio / AGP | AGP 9.2.1，Java + XML Views 技术栈 |
| Gradle | 9.4.1（Gradle Wrapper 随工程提供） |
| Android SDK | Platform android-36（compileSdk 36 / minorApiLevel 1）、Build Tools、Platform Tools |
| minSdk / targetSdk | 26 / 36 |
| AVD | Pixel 6 Pro，system-image `android-37.0/google_apis_ps16k/x86_64` |
| 验证分辨率 | 手机 1080×1920 @420dpi；平板 1600×2560 @320dpi（sw800dp） |

## 二、搭建步骤

1. 安装 Android Studio，SDK Manager 勾选 Android SDK Platform 36、SDK Build-Tools、Platform-Tools、Emulator；记录 `sdk.dir=D:\AndroidSdk` 到 `local.properties`。
2. Device Manager 新建 **Pixel 6 Pro** AVD，选择 API 37 x86_64 镜像，冷启动确认可进桌面。
3. 在本迭代工程内保留一个纯代码布局的 `HelloWorldActivity`（不写 XML），首次 Run 时以它显示 `Hello World!`，确认「编译 → 打包 → 安装 → 启动」链路通畅；它同时用来对照讲解 `java/` 与 `res/` 的分工，也是本次环境验证留证与复现的载体。
4. 课堂可用 `adb shell am start -n cn.scyun/.ui.HelloWorldActivity` 随时复验，截图见 `效果图/05_HelloWorld_环境验证.png`。

## 三、可复现验证

```bash
# 构建
./gradlew :app:assembleDebug                # 产物 app/build/outputs/apk/debug/app-debug.apk
# 安装并启动注册页
adb install -r app-debug.apk
adb shell am start -n cn.scyun/.ui.RegisterActivity
# 拉起 HelloWorld 验证页
adb shell am start -n cn.scyun/.ui.HelloWorldActivity
```

**验证结果**：HelloWorld 页正常显示「Hello World! / 川产云 · 迭代 1 环境搭建验证通过 / Android Studio + SDK + AVD 已跑通」，见 `效果图/05_HelloWorld_环境验证.png`；注册页在手机与平板分辨率下均正常，见 `效果图/01`、`效果图/06`。

## 四、过程中遇到并解决的问题（回顾会输入）

| # | 现象 | 原因 | 解决 |
| --- | --- | --- | --- |
| 1 | `Your project path contains non-ASCII characters`，AGP 拒绝构建 | 课程目录 `D:\移动应用开发` 含中文，AGP 9.x 默认拦截 | 在 `gradle.properties` 加 `android.overridePathCheck=true`；换纯英文目录时可去掉 |
| 2 | 离线构建报 `drawerlayout-1.0.0.aar / customview` 无缓存 | 新机器的 Gradle 缓存尚未包含 AppCompat 的全部传递依赖 | 首次联网执行一次 `./gradlew :app:assembleDebug` 下载依赖，之后 `./gradlew --offline :app:assembleDebug` 复验通过 |
| 3 | 模拟器冷启动段错误退出（exit 139） | 显式指定 `-gpu swiftshader_indirect -no-window` 时，本机 SwiftShader/Vulkan 组合创建设备失败（`VK_ERROR_FEATURE_NOT_PRESENT`） | **不显式指定 `-gpu`**，由模拟器自动选择 gfxstream/Vulkan 后端（本机识别到 NVIDIA RTX 4050）即可稳定启动；日志显示 `angle_indirect` 同样会被判为无效选项并自动回退 `auto`，故无需手工挑后端 |
| 4 | `adb shell am start` 拉起 HelloWorld 报 `Permission Denial: not exported` | Activity 声明了 `exported="false"` | 该页不承载数据/权限，改为 `exported="true"` 并加注释说明用途 |
| 5 | 切换分辨率后截到黑屏 / 启动过渡帧 | 截图早于 Activity 完成重绘 | 用 `uiautomator dump` 轮询页面控件就绪后再截图 |

## 五、对应验收清单

- ☑ Android Studio 安装、SDK / AVD 配置完成，HelloWorld 在模拟器跑通（本记录 + `效果图/05`）
- ☑ 手机（1080×1920）与平板模拟器下布局不错位、不截断（`效果图/01`、`06`）
- ☑ Layout Inspector / View 树核对 ConstraintLayout 版嵌套 ≤2 层（`布局层级核对.txt`）
- ☑ 能口述 Manifest / java / res 各目录作用（`README.md` 第三节、测试记录 TC-13）
