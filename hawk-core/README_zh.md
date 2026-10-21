# hawk-core

[English](README.md) | [中文](#)

核心工具库，提供基础设施组件。

## 模块概览

| 包 | 说明 |
|---------|-------------|
| `glz.hawkframework.core.cache` | 缓存抽象及 Caffeine/Guava 实现 |
| `glz.hawkframework.core.id` | ID 生成器（雪花算法 Long/BigInteger） |
| `glz.hawkframework.core.asyc` | 异步执行器（基于 CompletableFuture） |
| `glz.hawkframework.core.helper` | 通用工具类（字符串、反射、日期、枚举等） |
| `glz.hawkframework.core.i18n` | 国际化显示/消息支持 |
| `glz.hawkframework.core.pojo` | POJO 解析器 |
| `glz.hawkframework.core.support` | 参数校验、逻辑工具、状态机支持 |
| `glz.hawkframework.core.tuple` | Tuple2~Tuple8 元组类 |
| `glz.hawkframework.core.function` | 额外函数式接口 |

## 核心特性

### 缓存 (`Cache`)
- 接口 `Cache<K,V>` 定义标准操作：`get/put/computeIfAbsent/evict/clear/exists`
- `CaffeineCache`：高性能，支持随机过期抖动
- `GuavaCache`：兼容 Guava Cache
- `DefaultCacheExecutor`：缓存击穿防护，内置私有哨兵避免值冲突

### 雪花 ID (`IdGenerator`)
- `SnowflakeIdGeneratorForLong`：64 位 Long ID，默认 41bit 时间 + 2bit DC + 9bit Machine + 11bit Sequence
- `SnowflakeIdGeneratorForBigInteger`：支持更大位数
- 无锁 CAS 实现（`AtomicLong`），高并发性能优异
- Builder 模式配置，构建时前置校验

### 异步执行 (`AsyncExecutor`)
- `DefaultAsyncExecutor`：包装线程池，支持 `submit`/`submitDelayed`/`scheduleAtFixedRate`
- 返回 `AsyncResult`，内部包装 `CompletableFuture`

### 工具类
- `StringHelper`：字符串判空、大小写转换、替换、重复
- `ReflectHelper`/`LambdaHelper`：反射构造、Getter/Setter 转 Lambda（LambdaMetafactory + 反射回退）
- `DateTimeHelper`：线程安全日期格式化（`DateTimeFormatter` 缓存）
- `EnumHelper`：枚举显示名称国际化，需先调用 `EnumHelper.initialize(i18nMessageSource)`
- `ArgumentSupport`：流式参数校验 API（`argNotNull`/`argNotBlank`/`argTrue`...）

## 依赖
- Java 8+
- Caffeine / Guava（可选，二选一）
- SLF4J + Log4j2
- Spring Core（部分工具类依赖 `MergedAnnotations`）

## 快速开始

```gradle
implementation "io.github.zeatul:hawk-core:1.3.0-SNAPSHOT"
```

### 缓存示例
```java
Cache<String, User> cache = CaffeineCache.builder()
    .setMaximumSize(10_000)
    .setExpireAfterWrite(Duration.ofHours(2))
    .build();

DefaultCacheExecutor<String, User> executor = new DefaultCacheExecutor<>(cache);
User user = executor.execute("user:1", () -> userRepository.findById(1));
```

### 雪花 ID 示例
```java
SnowflakeIdConfig config = SnowflakeIdConfig.builder()
    .setDataCenterId(1)
    .setMachineId(1)
    .setInitialDateOfSnowflakeId(LocalDate.of(2024, 1, 1))
    .build();

IdGenerator<Long> generator = new SnowflakeIdGeneratorForLong(config);
Long id = generator.next();
```

### 枚举国际化示例
```java
@Configuration
class Config {
    @Bean
    void initEnumHelper(I18NMessageSource messageSource) {
        EnumHelper.initialize(messageSource);
    }
}

// 使用
String displayName = EnumHelper.getDisplayName(Status.ACTIVE, Locale.CHINESE).orElse("未知");
```

## 版本兼容性
- Java 8+（字节码目标 8）
- Spring 5.3.x / 6.x 兼容
- Jakarta Validation 2.0 / 3.0 兼容
