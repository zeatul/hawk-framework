# hawk-validator

[English](README.md) | [中文](#)

基于 Jakarta Bean Validation (JSR 380) 的扩展约束注解库，提供常用校验规则。

## 模块概览

| 包 | 说明 |
|---------|-------------|
| `glz.hawkframework.validator.constraints` | 约束注解定义 |
| `glz.hawkframework.validator.validators` | 约束校验器实现 |
| `glz.hawkframework.validator.support` | 通用校验支持（UniversalCheck 注册表） |
| `glz.hawkframework.validator.bootstrapping` | Spring EL 脚本评估器工厂 |

## 内置约束

| 注解 | 校验目标 | 说明 |
|------------|-------------------|-------------|
| `@UniversalCheck` | 任意对象 | 自定义校验方法名 + 消息模板；需通过 `UniversalCheckSupport.addChecker` 注册 |
| `@CompareFields` | 对象 | 比较对象内两个字段（LESS/GREATER/EQUAL/LESS_OR_EQUAL/GREATER_OR_EQUAL/NOT_EQUAL）；支持 SpEL 路径 |
| `@NotNullGroup` | 对象 | 多字段非空组合：AT_LEAST_ONE_IS_NOT_NULL / AT_LEAST_ONE_IS_NULL / ONLY_ONE_IS_NOT_NULL / ONLY_ONE_IS_NULL / ALL_ARE_NOT_NULL / ALL_ARE_NULL / ALL_ARE_NOT_NULL_OR_ALL_ARE_NULL |
| `@FixedLength` | CharSequence/数组/Collection/Map | 长度/大小必须等于指定值 |
| `@RegularCharSequence` | CharSequence | 预定义正则：ASCII/DIGIT/ALPHABET/UPPERCASE/LOWERCASE/DIGIT_ALPHABET... |
| `@EnumValue` / `@EnumName` | String/Integer | 校验枚举值/名称的合法性 |

## 核心特性

### UniversalCheck - 完全自定义校验
```java
// 1. 注册校验器（应用启动时一次性注册）
UniversalCheckSupport.addChecker("phone", value -> {
    String phone = (String) value;
    return phone != null && phone.matches("^1[3-9]\\d{9}$");
});
UniversalCheckSupport.addMessageParameterSupplier("phone", value -> Map.of("value", value));

// 2. 在字段上使用
public class UserDTO {
    @UniversalCheck(checkerName = "phone", message = "手机号格式错误: {value}")
    private String phone;
}
```

### CompareFields - 字段间比较
```java
@CompareFields(left = "startTime", right = "endTime", expectedResult = CompareResult.LESS_OR_EQUAL)
public class EventDTO {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
```
- 支持嵌套路径：`address.city`、`items[0].price`
- 自动类型转换：数字→BigDecimal、日期→Instant、字符串直接比较
- **null 语义**：任一值为 null 时校验通过（不报错）

### NotNullGroup - 组合非空校验
```java
@NotNullGroup(fields = {"phone", "email"}, type = NotNullType.AT_LEAST_ONE_IS_NOT_NULL)
public class ContactDTO {
    private String phone;
    private String email;
}
```

## 依赖
- hawk-core
- Jakarta Validation API 2.0+
- Hibernate Validator 6.2+（运行时）
- Spring Expression (SpEL)

## 快速开始

```gradle
implementation "io.github.zeatul:hawk-validator:1.3.0-SNAPSHOT"
```

### Spring Boot 集成
```java
@Configuration
public class ValidatorConfig {
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
}
```

### 国际化消息
在 `ValidationMessages.properties` 中定义：
```properties
glz.hawkframework.validator.constraints.CompareFields.LESS.message=开始时间必须早于或等于结束时间
glz.hawkframework.validator.constraints.NotNullGroup.AT_LEAST_ONE_IS_NOT_NULL.message=字段 {fieldNames} 至少有一个不能为空
```

## 注意事项
- `UniversalCheckSupport` 静态 Map 无自动清理，测试/热重载场景需手动调用 `clear()`（待补充）
- `CompareFields` 使用的 SpEL 表达式缓存无大小限制，动态路径可能导致 OOM（建议使用固定路径）
- 跨类型数值比较（Long vs Integer）当前会抛异常，建议统一字段类型

## 版本兼容性
- Java 8+（字节码目标 8）
- Spring 5.3.x / 6.x
- Jakarta Validation 2.0 / 3.0
