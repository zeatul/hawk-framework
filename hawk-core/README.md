# hawk-core

[English](#) | [中文](README_zh.md)

Core utility library providing infrastructure components.

## Module Overview

| Package | Description |
|---------|-------------|
| `glz.hawkframework.core.cache` | Cache abstraction with Caffeine/Guava implementations |
| `glz.hawkframework.core.id` | ID generators (snowflake Long/BigInteger) |
| `glz.hawkframework.core.asyc` | Async executors (based on CompletableFuture) |
| `glz.hawkframework.core.helper` | General-purpose helper classes (string, reflection, date, enum, etc.) |
| `glz.hawkframework.core.i18n` | Internationalization display/message support |
| `glz.hawkframework.core.pojo` | POJO parsers |
| `glz.hawkframework.core.support` | Argument validation, logic helpers, state machine support |
| `glz.hawkframework.core.tuple` | Tuple2~Tuple8 tuple classes |
| `glz.hawkframework.core.function` | Additional functional interfaces |

## Core Features

### Cache (`Cache`)
- The `Cache<K,V>` interface defines the standard operations: `get/put/computeIfAbsent/evict/clear/exists`
- `CaffeineCache`: high performance, supports random expiration jitter
- `GuavaCache`: Guava Cache compatibility
- `DefaultCacheExecutor`: cache stampede protection, with a built-in private sentinel to avoid value collisions

### Snowflake ID (`IdGenerator`)
- `SnowflakeIdGeneratorForLong`: 64-bit Long IDs, default 41-bit time + 2-bit DC + 9-bit Machine + 11-bit Sequence
- `SnowflakeIdGeneratorForBigInteger`: supports larger bit widths
- Lock-free CAS implementation (`AtomicLong`) with excellent high-concurrency performance
- Builder-style configuration with pre-build validation

### Async Execution (`AsyncExecutor`)
- `DefaultAsyncExecutor`: wraps a thread pool, supports `submit`/`submitDelayed`/`scheduleAtFixedRate`
- Returns `AsyncResult`, which wraps `CompletableFuture`

### Utility Classes
- `StringHelper`: string emptiness checks, case conversion, replacement, repetition
- `ReflectHelper`/`LambdaHelper`: reflective construction, Getter/Setter-to-Lambda conversion (LambdaMetafactory + reflection fallback)
- `DateTimeHelper`: thread-safe date formatting (`DateTimeFormatter` cache)
- `EnumHelper`: internationalized enum display names; requires `EnumHelper.initialize(i18nMessageSource)` first
- `ArgumentSupport`: fluent argument validation API (`argNotNull`/`argNotBlank`/`argTrue`...)

## Dependencies
- Java 8+
- Caffeine / Guava (optional, pick one)
- SLF4J + Log4j2
- Spring Core (some helper classes rely on `MergedAnnotations`)

## Quick Start

```gradle
implementation "io.github.zeatul:hawk-core:1.3.0-SNAPSHOT"
```

### Cache Example
```java
Cache<String, User> cache = CaffeineCache.builder()
    .setMaximumSize(10_000)
    .setExpireAfterWrite(Duration.ofHours(2))
    .build();

DefaultCacheExecutor<String, User> executor = new DefaultCacheExecutor<>(cache);
User user = executor.execute("user:1", () -> userRepository.findById(1));
```

### Snowflake ID Example
```java
SnowflakeIdConfig config = SnowflakeIdConfig.builder()
    .setDataCenterId(1)
    .setMachineId(1)
    .setInitialDateOfSnowflakeId(LocalDate.of(2024, 1, 1))
    .build();

IdGenerator<Long> generator = new SnowflakeIdGeneratorForLong(config);
Long id = generator.next();
```

### Enum Internationalization Example
```java
@Configuration
class Config {
    @Bean
    void initEnumHelper(I18NMessageSource messageSource) {
        EnumHelper.initialize(messageSource);
    }
}

// Usage
String displayName = EnumHelper.getDisplayName(Status.ACTIVE, Locale.CHINESE).orElse("未知");
```

## Version Compatibility
- Java 8+ (bytecode target 8)
- Spring 5.3.x / 6.x compatible
- Jakarta Validation 2.0 / 3.0 compatible
