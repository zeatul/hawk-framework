# hawk-validator

[English](#) | [中文](README_zh.md)

An extension constraint annotation library based on Jakarta Bean Validation (JSR 380), providing common validation rules.

## Module Overview

| Package | Description |
|---------|-------------|
| `glz.hawkframework.validator.constraints` | Constraint annotation definitions |
| `glz.hawkframework.validator.validators` | Constraint validator implementations |
| `glz.hawkframework.validator.support` | Common validation support (UniversalCheck registry) |
| `glz.hawkframework.validator.bootstrapping` | Spring EL script evaluator factory |

## Built-in Constraints

| Annotation | Validation Target | Description |
|------------|-------------------|-------------|
| `@UniversalCheck` | Any object | Custom checker method name + message template; requires registration via `UniversalCheckSupport.addChecker` |
| `@CompareFields` | Object | Compares two fields within an object (LESS/GREATER/EQUAL/LESS_OR_EQUAL/GREATER_OR_EQUAL/NOT_EQUAL); supports SpEL paths |
| `@NotNullGroup` | Object | Multi-field null-state combinations: AT_LEAST_ONE_IS_NOT_NULL / AT_LEAST_ONE_IS_NULL / ONLY_ONE_IS_NOT_NULL / ONLY_ONE_IS_NULL / ALL_ARE_NOT_NULL / ALL_ARE_NULL / ALL_ARE_NOT_NULL_OR_ALL_ARE_NULL |
| `@FixedLength` | CharSequence/array/Collection/Map | Length/size must equal the specified value |
| `@RegularCharSequence` | CharSequence | Predefined patterns: ASCII/DIGIT/ALPHABET/UPPERCASE/LOWERCASE/DIGIT_ALPHABET... |
| `@EnumValue` / `@EnumName` | String/Integer | Validates the legality of an enum value/name |

## Core Features

### UniversalCheck - Fully Custom Validation
```java
// 1. Register a checker (registered once at application startup)
UniversalCheckSupport.addChecker("phone", value -> {
    String phone = (String) value;
    return phone != null && phone.matches("^1[3-9]\\d{9}$");
});
UniversalCheckSupport.addMessageParameterSupplier("phone", value -> Map.of("value", value));

// 2. Use it on a field
public class UserDTO {
    @UniversalCheck(checkerName = "phone", message = "手机号格式错误: {value}")
    private String phone;
}
```

### CompareFields - Cross-field Comparison
```java
@CompareFields(left = "startTime", right = "endTime", expectedResult = CompareResult.LESS_OR_EQUAL)
public class EventDTO {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
```
- Supports nested paths: `address.city`, `items[0].price`
- Automatic type conversion: numbers→BigDecimal, dates→Instant, strings compared directly
- **null semantics**: validation passes when either value is null (no error reported)

### NotNullGroup - Combined Non-null Validation
```java
@NotNullGroup(fields = {"phone", "email"}, type = NotNullType.AT_LEAST_ONE_IS_NOT_NULL)
public class ContactDTO {
    private String phone;
    private String email;
}
```

## Dependencies
- hawk-core
- Jakarta Validation API 2.0+
- Hibernate Validator 6.2+ (runtime)
- Spring Expression (SpEL)

## Quick Start

```gradle
implementation "io.github.zeatul:hawk-validator:1.3.0-SNAPSHOT"
```

### Spring Boot Integration
```java
@Configuration
public class ValidatorConfig {
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
}
```

### Internationalized Messages
Define them in `ValidationMessages.properties`:
```properties
glz.hawkframework.validator.constraints.CompareFields.LESS.message=开始时间必须早于或等于结束时间
glz.hawkframework.validator.constraints.NotNullGroup.AT_LEAST_ONE_IS_NOT_NULL.message=字段 {fieldNames} 至少有一个不能为空
```

## Notes
- The `UniversalCheckSupport` static Map is never cleaned up automatically; tests/hot-reload scenarios must call `clear()` manually (to be added)
- The SpEL expression cache used by `CompareFields` has no size limit; dynamic paths may lead to OOM (prefer fixed paths)
- Cross-type numeric comparison (Long vs Integer) currently throws an exception; unify field types where possible

## Version Compatibility
- Java 8+ (bytecode target 8)
- Spring 5.3.x / 6.x
- Jakarta Validation 2.0 / 3.0
