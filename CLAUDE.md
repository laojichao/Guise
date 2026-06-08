# Guise - 设备参数伪装 Xposed 模块

## 项目概述
Guise 是一个 Xposed 模块，允许为 Android 设备上的应用传递自定义的设备参数，模拟不同的设备型号、品牌、系统版本等信息。

## 技术栈
- **开发语言**: Kotlin
- **UI 框架**: Jetpack Compose
- **Hook 框架**: Xposed API
- **数据存储**: MMKV, Room Database
- **网络请求**: Ktor

## 环境要求
- Android 10+ (minSdk 29)
- 已安装 Xposed 框架（LSPosed、EdXposed 等）

## 功能特性
- 设备信息伪装（型号、品牌、制造商等）
- 系统参数修改
- 应用级别的独立配置
- 基于 Jetpack Compose 的现代 UI

## 版本信息
当前版本: 1.1.2

## 构建命令
```bash
./gradlew assembleRelease
```

## 许可证
MIT License
