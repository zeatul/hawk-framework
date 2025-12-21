## 选择你喜欢的语言

[English](README.md) | [中文](#)

---

# hawk-project

这是一个常用企业应用系统开发所需要的工具项目集合。
1. hawk-framework-project
   1. hawk-core:
      1. 提供一些框架使用的通用工具类: 逻辑流lambda表达式，state校验，argument校验，雪花算法等。
   2. hawk-dao-base:
      1. 提供数据库访问层的一些通用类: 分页，通用异常，COLUMN功能扩展等。
   3. hawk-dynamic-sql:
      1. 使用java写SQL语句，可以直接翻译成标准SQL语句，或者jdbc参数化后的SQL语句。
      2. 根据当前环境（datasource或方言设定）自动翻译成对应数据库适配的SQL语句
      3. 支持全部通用SQL语法要素和部分常用SQL方言要素。
      4. 真正做到SQL语句设计时和方言无关，只在运行时才有关。
   4. hawk-dynamic-sql-mybatis:
      1. 将hawk-dynamic-sql写的SQL语句翻译成Mybatis参数化的SQL语句。
      2. 支持全自动参数化：参数和参数值都通过SQL语句解析得来。
      3. 支持写sql语句时指定Mybatis格式参数：参数和参数值由程序员自己指定。
      4. 提供通用的Inert，Update，Delete，Select Mapper。
2. hawk-designer-project
   1. 通过JAVA注解进行数据库建模。
      1. 支持全数据库DDL语法要素: DOMAIN，METADATA，COLUMN，INDEX，PRIMARY KEY, TABLE, SCHEMA.
      2. 支持COLUMN功能扩展: 物理主键，记录版本号，记录创建时间，记录修改时间，记录创建人，记录修改人等。
      3. 支持自定义注解和注解解释器。
   2. 通过JAVA注解进行JAVA类和接口的建模。
      1. 支持全JAVA语法要素: CLASS, ANNOTATION, ENUM, FIELD, METHOD, CONSTRUCTOR.
   3. 提供了JAVA代码生成工具可以将1和2建模的结果翻译成:
      1. 数据库访问层代码，DDL，可以根据COLUMN的扩展功能来决定是否提供自维护的注解，创建时间，修改时间，创建人，修改人，版本号，乐观锁等。自维护功能可以自定义。
      2. JAVA枚举，JAVA接口和JAVA POJO。可以根据模型的定义来自动提供增强的JAVA注解，比如数据校验，事务支持，缓存支持等。
   4. 提供了强大的扩展能力：程序员可以自定义注解，注解解释器，代码生成器来生成符合自己需求的代码。
   5. 模型可以作为jar包共享，并支持多版本，尤其适合有较强的管理规范需求，有企业级数据字典需求的场景。
   6. 提供了完整的代码示例来演示以上所有功能。
3. hawk-generator-project
   1. hawk-database-generator
      1. 用JAVA定义数据库模型，支持: SCHEMA, TABLE, COLUMN, INDEX, PRIMARY KEY。
      2. 模型可以根据数据库方言翻译成对应的DDL。
   2. hawk-java-generator
      1. 用JAVA写JAVA，完美支持JDK8的全部语法要素：包，接口，类，匿名类，内部类，枚举，注解，构造函数，方法和静态方法，成语变量和静态成语变量，泛型，代码和控制流，注释。
   3. hawk-project-tools
      1. 提供一些便利方法操纵项目目录和目录内的文件。


## 功能特性
- 快速性能
- 易于使用
- 开源项目

## 安装
```bash
npm install my-project
```

## 使用方法
```javascript
import MyProject from 'my-project';
const instance = new MyProject();
```
