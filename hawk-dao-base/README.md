# hawk-dao-base

[English](#) | [中文](README_zh.md)

DAO infrastructure module providing core features such as field parsing, table annotations, pagination, sharding, and exception conversion.

## Module Overview

| Package | Description |
|---------|-------------|
| `glz.hawkframework.dao.column` | Field descriptors, POJO field parsers |
| `glz.hawkframework.dao.column.annotation` | Record field annotations (ID, version, create/update time/user, sharding, etc.) |
| `glz.hawkframework.dao.context` | Repository context, parameters, exception converters |
| `glz.hawkframework.dao.exception` | Data access exception hierarchy |
| `glz.hawkframework.dao.pagination` | Pagination interfaces and default implementations |
| `glz.hawkframework.dao.process` | Insert/update processors (lifecycle callbacks) |
| `glz.hawkframework.dao.sharding` | Sharding parameters and services |
| `glz.hawkframework.dao.table` | Class descriptors, table annotations |

## Core Features

### Field Parsing (`PojoFieldParser`)
- Annotation-based field metadata extraction
- Supports `@RecordId`, `@RecordVersion`, `@RecordCreateDateTime`, `@RecordUpdateDateTime`, etc.
- Parse results are cached at class level (`ConcurrentHashMap`) to avoid repeated reflection
- Provides `FieldDescriptor` with Getter/Setter/Updated checks

### Exception Conversion (`ExceptionConverter`)
- `DefaultExceptionConverter`: converts Spring `DuplicateKeyException`, SQL integrity exceptions, and affected-row-count exceptions into domain exceptions
- Supports custom exception message suppliers

### Pagination (`PagedResult`, `Pageable`)
- `DefaultPagedResult`: total count + record list + pagination info
- `DefaultPage` / `DefaultOffsetAndLimit`: two modes — page number/size or offset/limit

### Record Annotations
| Annotation | Description |
|------------|-------------|
| `@RecordId` | Primary key ID |
| `@RecordVersion` | Optimistic lock version number |
| `@RecordCreateDateTime` / `@RecordUpdateDateTime` | Create/update time |
| `@RecordCreateUserId` / `@RecordUpdateUserId` | Create/update user |
| `@RecordDeleteDateTime` / `@RecordDeleteUserId` | Logical delete time/user |
| `@RecordShardingSource` / `@RecordShardingTarget` | Sharding source/target fields |
| `@RecordAutoTruncate` | Automatically truncate overly long strings |

## Dependencies
- hawk-core
- Spring JDBC / MyBatis (optional, runtime)
- Jakarta Validation

## Quick Start

```gradle
implementation "io.github.zeatul:hawk-dao-base:1.3.0-SNAPSHOT"
```

### Entity Definition Example
```java
@TableFunction("t_book")
public class BookPo {
    @RecordId
    private String id;

    @RecordVersion
    private Long version;

    @RecordCreateDateTime
    private LocalDateTime createTime;

    @RecordUpdateDateTime
    private LocalDateTime updateTime;

    @RecordCreateUserId
    private String createUser;

    @RecordUpdateUserId
    private String updateUser;

    // getters/setters...
}
```

### Field Parsing Usage
```java
PojoFieldParser<BookPo> parser = new PojoFieldParser<>(BookPo.class);
Map<Class<? extends Annotation>, List<FieldDescriptor<BookPo, Object, Annotation>>> map = parser.parse();

// Set the ID
FieldDescriptor<BookPo, Object, RecordId> idDesc = map.get(RecordId.class).get(0);
idDesc.getFieldModifier().accept(bookPo, "BOOK-001");
```

### Custom Exception Messages
```java
DefaultExceptionConverter converter = new DefaultExceptionConverter(
    ctx -> "并发修改：" + ctx.getPoClass().getSimpleName(),
    ctx -> "记录不存在：" + ctx.getPoClass().getSimpleName(),
    ctx -> "找到多条记录：" + ctx.getPoClass().getSimpleName(),
    ctx -> "唯一键冲突：" + ctx.getPoClass().getSimpleName()
);
```

## Version Compatibility
- Java 8+ (bytecode target 8)
- Spring 5.3.x / 6.x
- MyBatis 3.5.x
