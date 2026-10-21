# hawk-batch

[English](README.md) | [中文](#)

批处理框架模块（规划中）。

## 状态
🚧 **开发中** —— 目前仅包含对 hawk-core 的依赖，核心功能待实现。

## 计划功能
- [ ] 批量任务调度与执行
- [ ] 分块读取/处理/写入（Chunk-oriented processing）
- [ ] 重试、跳过、监听器机制
- [ ] 多线程/分布式执行支持
- [ ] Spring Batch 兼容适配层

## 依赖
- hawk-core

## 快速开始
```gradle
implementation "io.github.zeatul:hawk-batch:1.3.0-SNAPSHOT"
```

> ⚠️ 当前版本不可用于生产环境，请等待后续版本发布。
