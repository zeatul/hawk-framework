# hawk-excel

[English](README.md) | [中文](#)

基于 Apache POI 的 Excel 读写工具，支持注解驱动的模板导出与数据导入。

## 模块概览

| 包 | 说明 |
|---------|-------------|
| `glz.hawkframework.excel.helper` | 核心工具类 `ExcelHelper` |
| `glz.hawkframework.excel.annotations` | 注解：`@ExcelSheet`、`@ExcelColumn` |
| `glz.hawkframework.excel.model` | 领域模型：Sheet/Row/Header/Column/Cell |
| `glz.hawkframework.excel.parse` | 解析器：`SheetParse`、`ColumnParse` |

## 核心特性

### 注解驱动
```java
@ExcelSheet(sheetIndex = 0, sheetName = "图书", dataStartIndex = 1)
public class Book {
    @ExcelColumn(index = 0, name = "行号", isRowNo = true)
    private int rowNo;

    @ExcelColumn(index = 1, name = "书名")
    private String name;

    @ExcelColumn(index = 2, name = "价格", format = "#,##0.00")
    private BigDecimal price;

    @ExcelColumn(index = 3, name = "出版日期", format = "yyyy-MM-dd")
    private LocalDate publishDate;

    @ExcelColumn(index = 4, name = "是否有效")
    private Boolean valid;

    // getters/setters...
}
```

### 写入 Excel（支持模板）
```java
// 1. 无模板写入
Map<Class<?>, List<?>> data = Map.of(Book.class, books);
ExcelHelper.writeExcel(outputFile, data);

// 2. 使用模板（保留样式与首行数据行格式）
ExcelHelper.writeExcelWithTemplate("/template/book_template.xlsx", outputFile, data);
```

### 读取 Excel
```java
// 读取单个 Sheet
List<Book> books = ExcelHelper.readOneSheet("/data/books.xlsx", Book.class);

// 读取多个 Sheet
Map<Class<?>, List<?>> map = ExcelHelper.readSheets("/data/all.xlsx", Book.class, Author.class);
List<Book> books = (List<Book>) map.get(Book.class);
```

### 类型转换支持
| Java 类型 | Excel 单元格类型 | 说明 |
|-----------|-----------------|-------------|
| String | STRING/NUMERIC | 数字转字符串保留完整精度 |
| Integer/Long | NUMERIC | 自动转换 |
| BigDecimal/BigInteger | NUMERIC | 高精度 |
| Boolean | BOOLEAN/STRING | true/false 或 1/0 |
| LocalDate/LocalTime/LocalDateTime | NUMERIC(date)/STRING | 需通过 `@ExcelColumn(format)` 指定模式 |
| Date | NUMERIC(date) | 传统 Date 类型 |

- 日期解析：线程安全的 `DateTimeFormatter` 缓存
- 公式求值：`FormulaEvaluator` 自动计算公式单元格

## 依赖
- hawk-core
- Apache POI 5.5+（poi-ooxml）
- Spring Core（用于 `MergedAnnotations`）

## 快速开始

```gradle
implementation "io.github.zeatul:hawk-excel:1.3.0-SNAPSHOT"
```

### 完整示例：导出图书列表
```java
@ExcelSheet(sheetIndex = 0, sheetName = "图书列表", dataStartIndex = 1)
@Data
public class BookExportVO {
    @ExcelColumn(index = 0, name = "序号", isRowNo = true)
    private int rowNo;

    @ExcelColumn(index = 1, name = "书名")
    private String name;

    @ExcelColumn(index = 2, name = "作者")
    private String author;

    @ExcelColumn(index = 3, name = "价格", format = "¥#,##0.00")
    private BigDecimal price;

    @ExcelColumn(index = 4, name = "出版日期", format = "yyyy年MM月dd日")
    private LocalDate publishDate;
}

// 在 Service 中
public void exportBooks(HttpServletResponse response) throws IOException {
    List<BookExportVO> data = bookService.findAllForExport();
    Map<Class<?>, List<?>> excelData = Map.of(BookExportVO.class, data);

    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    response.setHeader("Content-Disposition", "attachment; filename=books.xlsx");

    ExcelHelper.writeExcel(response.getOutputStream(), excelData); // 需使用 OutputStream 重载
}
```

### 导入校验示例
```java
@ExcelSheet(sheetIndex = 0, dataStartIndex = 1)
public class BookImportDTO {
    @ExcelColumn(index = 0, name = "书名", readable = true, writable = false)
    private String name;

    @ExcelColumn(index = 1, name = "价格", format = "#,##0.00")
    private BigDecimal price;
}

// Controller
@PostMapping("/import")
public Result importBooks(@RequestParam MultipartFile file) {
    List<BookImportDTO> books = ExcelHelper.readOneSheet(file.getInputStream(), BookImportDTO.class);
    // 校验 + 保存
    return Result.ok();
}
```

## 注意事项
- **仅适用于小文件**（全量加载进内存）；大文件建议使用 `SXSSFWorkbook` 流式写入（未内置）
- `@ExcelColumn.index` 必须从 0 开始，且不得超过 16383（Excel 列数限制）
- `dataStartIndex`：0 = 无表头，1 = 第 1 行为表头（数据从第 2 行开始）
- 模板文件只能包含一个数据行（第 `dataStartIndex` 行），用于提取单元格样式
- 当内部类/匿名类作为 Sheet 类时，缓存键使用 `Class<?>`，避免 `getCanonicalName()` 返回 null

## 版本兼容性
- Java 8+（字节码目标 8）
- Apache POI 5.5.x
- Spring 5.3.x / 6.x
