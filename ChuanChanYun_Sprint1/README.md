# 《移动应用开发》迭代 1 作业 · 川产云，从一张注册页开始

> **启普斯**（软件工程 24-3 班 · 4 组）｜产业方向：**电子信息（成都高新区 / 天府软件园 · 集成电路与新型显示产业链）**
> 迭代 1 · Week 1｜Sprint 周期：2026-11-02 ~ 2026-11-08｜教材第 1–2 章：环境搭建 · 布局管理器 · 常用控件 · 表单校验
> 提交 zip：`软件工程24-3班_启普斯_迭代1_作业卡.zip`

---

## 一、交付物清单

| 文件 / 目录 | 内容 |
| --- | --- |
| `川产云_迭代1_注册页.apk` | 可直接安装的 debug 包（`cn.scyun`，versionName `0.1.0-sprint1`） |
| `ChuanChanYun_Sprint1/` | **本次作业的完整可运行源码**（Gradle 工程，不含 build 产物） |
| `效果图/` | 7 张模拟器实拍截图：注册页、校验失败、成功摘要、LinearLayout 对比版、HelloWorld、平板、填写完成 |
| `软件工程24-3班_启普斯_迭代1_作业卡.xlsx` | 按课程模板 7 个工作表填写的痛点驱动 BDD 作业卡 |
| `迭代1_测试验收记录表.xlsx` | 13 条测试用例（4 条 BDD 全覆盖 + 边界 / 布局 / 环境 / 目录口述） |
| `站会与复盘纪要.md` | 3 次站会（开工 / 中期 / 收尾）+ 1 次回顾会纪要 |
| `环境搭建与HelloWorld验证记录.md` | 功能要求 1 的环境、AVD、HelloWorld 跑通证据 |
| `布局层级核对.txt` | 真机 View 树导出，两版布局嵌套层级实测对比 |
| `README.md` | 本说明 |

---

## 二、为什么重新写一份源码（对现有 `ChuanChanYun` 的处理说明）

现有 `ChuanChanYun/` 是**8 个 Sprint 全量成品**（登录、首页、资讯、收藏、Provider、Service、PHP 后端齐备）。对照《移动应用开发实践作业设计》P6/P7 与作业卡模板逐条核查后，发现它的注册页不能原样当作迭代 1 交付：

| 迭代 1 要求（P7 / 模板） | 现有工程的实现 | 处理 |
| --- | --- | --- |
| RadioGroup：**性别**（男 / 女） | RadioGroup：个人用户 / 企业用户 | 重写 |
| CheckBox：**产业兴趣多选**，至少勾 1 个（BDD-02 前置条件） | 单个「同意用户协议」CheckBox | 重写 |
| Spinner：**所在城市** | Spinner：主营产业方向 | 重写 |
| 摘要须回显城市 / 性别 / 产业方向 | 摘要只有身份与单个产业 | 重写（BDD-03 通过） |
| 首次迭代：无历史代码、无持久化 | 依赖 `SampleData`、`SessionManager`（Sprint 3 的 SharedPreferences） | 剥离 |
| 注册页可直接启动演示 | Launcher 是登录页 `MainActivity` | 调整为注册页即 Launcher |
| 用 LinearLayout 实现后再用 ConstraintLayout 重写、对比层级 | 两版布局内容不一致，差异不在层级 | 重写为同 id / 同功能的双版本 |

因此选择**重新写一份只含迭代一要求的干净源码**，而不是把全量工程删剪成半成品：

- 保留 `cn.scyun` 包名与「川产云」产品名，后续 7 次迭代可直接在这个工程上叠加，符合课程「8 次作业是一个产品」的设计；
- **剥离**全部后续迭代内容：登录页、首页 / Fragment / Adapter、SQLite / ContentProvider、Service / 广播、HTTP / PHP 后端、夜间模式、离线补件仓库；
- 依赖只剩 `AppCompat` + `ConstraintLayout` 两个库，Manifest **不申请任何权限**，只声明 2 个 Activity；
- 配色按 P7 UI Mock 改为「川产云深绿 + 米白」，产业素材替换为本组**电子信息**方向的真实调研内容（成都、绵阳、宜宾、遂宁等川内电子信息产业承载地），不再使用模板里的示例角色与「织云小组 / 食品轻纺」示例数据。

---

## 三、工程结构与目录口述（P7 验收清单第 4 条）

```
ChuanChanYun_Sprint1/
├─ settings.gradle.kts / build.gradle.kts      工程与仓库配置、包含 :app 模块
├─ gradle/libs.versions.toml                   依赖版本目录（AGP / AppCompat / ConstraintLayout）
├─ gradle/wrapper/ + gradlew(.bat)             Gradle Wrapper，保证各机器构建版本一致
├─ local.properties                            本机 SDK 路径（sdk.dir），换机器需修改
├─ gradle.properties                           含 android.overridePathCheck=true（中文路径开关）
└─ app/
   ├─ build.gradle.kts                        applicationId / minSdk 26 / targetSdk 36 / 依赖
   └─ src/main/
      ├─ AndroidManifest.xml                  清单：2 个 Activity、Launcher 指向注册页、无权限
      ├─ java/cn/scyun/
      │  ├─ ui/RegisterActivity.java         注册页：控件绑定、Spinner、校验、摘要弹窗、双布局切换、状态保存
      │  ├─ ui/HelloWorldActivity.java       环境验证页（纯代码布局，便于对照讲解 java / res）
      │  ├─ data/IndustryCatalog.java        本组产业素材：所选方向 + 城市下拉数据源
      │  └─ util/RegisterValidator.java      不依赖 Android SDK 的纯 Java 表单校验规则
      └─ res/
         ├─ layout/activity_register.xml            ConstraintLayout 版（默认，嵌套 2 层）
         ├─ layout/activity_register_linear.xml     LinearLayout 教学对比版（嵌套 3 层）
         ├─ values/                                colors / strings / themes / dimens
         ├─ values-sw600dp/dimens.xml              平板留白与字号（同一份布局自动收窄）
         ├─ drawable/                              输入框 / 按钮 / Spinner 背景、图标、下拉三角
         └─ mipmap-anydpi-v26/                     自适应应用图标
```

**口述要点**：`AndroidManifest.xml` 是 App 的「户口本」，声明有哪些页面、谁是入口、要什么权限；`java/` 放交互逻辑；`res/layout/` 放界面结构，`res/values/` 放颜色 / 文案 / 尺寸 / 主题（文案统一走 `strings.xml`，不硬编码）；`res/drawable/` 放矢量图标与状态选择器；带 `-sw600dp` 后缀的资源目录在平板自动生效；Gradle 脚本负责把源码与资源编译打包成 APK。

---

## 四、运行与构建

| 项 | 本组实测环境 |
| --- | --- |
| Android Studio / AGP | AGP 9.2.1（Java + XML Views，非 Compose） |
| JDK | OpenJDK 17.0.19 |
| Android SDK | compileSdk 36（minorApiLevel 1），minSdk 26，targetSdk 36 |
| Gradle | 9.4.1（Wrapper 已随工程提供） |
| 模拟器 | Pixel 6 Pro AVD，Android API 37；手机 1080×1920 @420dpi、平板 1600×2560 @320dpi |

```bash
# 1) 用 Android Studio 打开 ChuanChanYun_Sprint1 目录，等首次同步完成
# 2) 命令行构建（首次需联网拉取 androidx 依赖，之后可加 --offline）
./gradlew :app:assembleDebug
# 3) 产物：app/build/outputs/apk/debug/app-debug.apk（已复制到作业一根目录）

# 功能要求 1 的验证：拉起 HelloWorld 页
adb shell am start -n cn.scyun/.ui.HelloWorldActivity

# 平板布局验证（把 AVD 切到平板尺寸，命中 values-sw600dp）
adb shell wm size 1600x2560 && adb shell wm density 320
adb shell am start -n cn.scyun/.ui.RegisterActivity
# 还原：adb shell wm size 1080x1920 && adb shell wm density 420
```

> **中文路径提示**：课程目录 `D:\移动应用开发` 含中文，AGP 9.x 默认拒绝构建，已在 `gradle.properties` 中加 `android.overridePathCheck=true`。若把工程移到纯英文目录，可删除该行。

---

## 五、功能与知识点对照（P7 逐条）

| 作业要求 | 实现位置 | 证据 |
| --- | --- | --- |
| 安装 Android Studio、配置 SDK/AVD、跑通 HelloWorld | `HelloWorldActivity` + 环境记录文档 | `效果图/05_HelloWorld_环境验证.png` |
| EditText 手机号 / 密码 | `activity_register.xml`：`inputType=phone`、`maxLength=11`、`textPassword` | `效果图/01` `07` |
| RadioGroup 性别 | `rg_gender` → `rb_male` / `rb_female` | `效果图/07` |
| CheckBox 产业兴趣多选 | 4 个 CheckBox + 至少选 1 个校验 | `效果图/07` |
| Spinner 城市 + ArrayAdapter | `IndustryCatalog.CITIES`（本组产业素材） | `效果图/07`（绵阳） |
| Button 点击 + 表单校验 | `RegisterActivity.attemptRegister()` + `RegisterValidator` | `效果图/02` |
| 手机号 11 位 / 密码 ≥6 位，失败 `setError()` | `RegisterValidator` 规则 + 3 类错误提示 | `效果图/02` |
| LinearLayout 实现后再用 ConstraintLayout 重写、对比层级 | 两套同 id 布局 + 长按标题切换 | `效果图/04`、`布局层级核对.txt` |
| 手机 1080×1920 与平板不错位、不截断 | `ScrollView` + `values-sw600dp/dimens.xml` | `效果图/01` `06` |
| ConstraintLayout 版嵌套 ≤2 层 | 4 个 CheckBox 用水平链直接约束在根布局 | `布局层级核对.txt`（实测 2 层 vs 3 层） |
| 能口述各目录作用 | 见本文件第三节 | 测试记录 TC-13 |

**成功路径**：Toast「注册成功」+「注册完成」摘要弹窗，摘要逐项回显手机号、性别、关注的产业、所在城市（`效果图/03`）。本次不做登录跳转、不落盘、不联网——分别属于迭代 2 / 3 / 5 的知识点。

---

## 六、5 分钟课堂演示脚本

1. **痛点（40s）**：小林，成都高新区封测厂新人，通勤 40 分钟被十几个公众号淹没 → 注册时一次选好产业与城市（作业卡 sheet 2）。
2. **BDD-01（60s）**：手机号输入 `1380013800`（10 位）→ 点「立即注册」→ 输入框 `setError`「请输入 11 位手机号」（`效果图/02`）。
3. **BDD-02/03（90s）**：填 `13800138000` / `abc123456`，勾「电子信息 + 医药健康」，选女、城市绵阳 → 点注册 → Toast「注册成功」+ 摘要逐项一致（`效果图/03`、`07`）。
4. **布局对比（60s）**：长按标题切到 LinearLayout 版（`效果图/04`），打开 `布局层级核对.txt` 说明 ConstraintLayout 2 层 vs LinearLayout 3 层；再把 AVD 切到平板尺寸看居中窄栏（`效果图/06`）。
5. **环境与目录（50s）**：`adb shell am start -n cn.scyun/.ui.HelloWorldActivity`（`效果图/05`），按第三节口述目录职责；收尾讲回顾会的 3 条改进任务。

---

## 七、与迭代 2 的衔接

注册收集的「产业偏好 + 所在城市」将在迭代 2 经显式 Intent / `putExtra` 传到首页；本工程保留 `cn.scyun` 包名、现有控件 id 与 `IndustryCatalog` 数据源，后续迭代直接在本工程叠加登录页与首页，不另起炉灶。校验规则 `RegisterValidator` 也可被登录页原样复用。

---

## 八、⚠️ 提交前核对清单（请务必逐项确认）

1. **成员姓名与学号**：作业卡「6 团队与分工」中的 5 人名单及 zip 文件名中的姓名/学号，是按模板格式与作业要求中的班级组号填写的**占位信息**，请按本组真实名单、真实学号替换（作业卡文件名也需同步改名）。
2. **班级 / 组号 / 提交人**：来自作业要求的 zip 命名示例，如与本组不符请一并修改。
3. **产业方向与素材**：本组设定为「电子信息（成都）」，城市与用户画像据此编写；若本组实际选择其他产业方向，请替换 `IndustryCatalog.java`、作业卡 sheet 1/2 与 README 抬头。
4. **Sprint 日期**：作业卡 sheet 1 为 2026-11-02 ~ 11-08，请按课程实际周次调整；站会日期同步在纪要中。
5. **`local.properties`**：换电脑后把 `sdk.dir` 改成该机 Android SDK 路径。
6. **提交方式**：雨课堂网页版（yuketang.cn/web）→ 本次作业试卷 → 主观题上传 zip（手机端不能传压缩包，单附件 ≤100M），截止下次课前。
