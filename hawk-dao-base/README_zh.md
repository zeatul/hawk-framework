# hawk-dao-base

[English](README.md) | [中文](#)

DAO 基础设施模块，提供字段解析、表注解、分页、分片、异常转换等核心功能。

## 模块概览

| 包 | 说明 |
|---------|-------------|
| `glz.hawkframework.dao.column` | 字段描述符、POJO 字段解析器 |
| `glz.hawkframework.dao.column.annotation` | 记录字段注解（ID、版本、创建/更新时间与用户、分片等） |
| `glz.hawkframework.dao.context` | 仓库上下文、参数、异常转换器 |
| `glz.hawkframework.dao.exception` | 数据访问异常体系 |
| `glz.hawkframework.dao.pagination` | 分页接口及默认实现 |
| `glz.hawkframework.dao.process` | 插入/更新处理器（生命周期回调） |
| `glz.hawkframework.dao.sharding` | 分片参数与服务 |
| `glz.hawkframework.dao.table` | 类描述符、表注解 |

## 核心特性

### 字段解析 (`PojoFieldParser`)
- 基于注解的字段元数据提取
- 支持 `@RecordId`、`@RecordVersion`、`@RecordCreateDateTime`、`@RecordUpdateDateTime` 等
- 解析结果按类缓存（`ConcurrentHashMap`），避免重复反射
- 提供 `FieldDescriptor`，包含 Getter/Setter/Updated 检查

### 异常转换 (`ExceptionConverter`)
- `DefaultExceptionConverter`：将 Spring `DuplicateKeyException`、SQL 完整性异常、影响行数异常转换为领域异常
- 支持自定义异常消息 Supplier

### 分页 (`PagedResult`, `Pageable`)
- `DefaultPagedResult`：总条数 + 记录列表 + 分页信息
- `DefaultPage` / `DefaultOffsetAndLimit`：两种模式 —— 页码/大小 或 偏移/限制

### 记录注解
| 注解 | 说明 |
|------------|-------------|
| `@RecordId` | 主键 ID |
| `@RecordVersion` | 乐观锁版本号 |
| `@RecordCreateDateTime` / `@RecordUpdateDateTime` | 创建/更新时间 |
| `@RecordCreateUserId` / `@RecordUpdateUserId` | 创建/更新用户 |
| `@RecordDeleteDateTime` / `@RecordDeleteUserId` | 逻辑删除时间/用户 |
| `@RecordShardingSource` / `@RecordShardingTarget` | 分片源/目标字段 |
| `@RecordAutoTruncate` | 自动截断过长字符串 |

## 依赖
- hawk-core
- Spring JDBC / MyBatis（可选，运行时）
- Jakarta Validation

## 快速开始

```gradle
implementation "io.github.zeatul:hawk-dao-base:1.3.0-SNAPSHOT"
```

### 实体定义示例
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

### 字段解析使用
```java
PojoFieldParser<BookPo> parser = new PojoFieldParser<>(BookPo.class);
Map<Class<? extends Annotation>, List<FieldDescriptor<BookPo, Object, Annotation>>> map = parser.parse();

// 设置 ID
FieldDescriptor<BookPo, Object, RecordId> idDesc = map.get(RecordId.class).get(0);
idDesc.getFieldModifier().accept(bookPo, "BOOK-001");
```

### 自定义异常消息
```java
DefaultExceptionConverter converter = new DefaultExceptionConverter(
    ctx -> "并发修改：" + ctx.getPoClass().getSimpleName(),
    ctx -> "记录不存在：" + ctx.getPoClass().getSimpleName(),
    ctx -> "找到多条记录：" + ctx.getPoClass().getSimpleName(),
    ctx -> "唯一键冲突：" + ctx.getPoClass().getSimpleName()
);
```

## 版本兼容性
- Java 8+（字节码目标 8）
- Spring 5.3.x / 6.x
- MyBatis 3.5.x
