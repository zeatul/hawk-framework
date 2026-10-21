# hawk-excel

[English](#) | [中文](README_zh.md)

An Apache POI based Excel read/write utility supporting annotation-driven template export and data import.

## Module Overview

| Package | Description |
|---------|-------------|
| `glz.hawkframework.excel.helper` | Core utility class `ExcelHelper` |
| `glz.hawkframework.excel.annotations` | Annotations: `@ExcelSheet`, `@ExcelColumn` |
| `glz.hawkframework.excel.model` | Domain model: Sheet/Row/Header/Column/Cell |
| `glz.hawkframework.excel.parse` | Parsers: `SheetParse`, `ColumnParse` |

## Core Features

### Annotation-driven
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

### Writing Excel (template support)
```java
// 1. Write without a template
Map<Class<?>, List<?>> data = Map.of(Book.class, books);
ExcelHelper.writeExcel(outputFile, data);

// 2. Use a template (preserves styles and the first data row's format)
ExcelHelper.writeExcelWithTemplate("/template/book_template.xlsx", outputFile, data);
```

### Reading Excel
```java
// Read a single sheet
List<Book> books = ExcelHelper.readOneSheet("/data/books.xlsx", Book.class);

// Read multiple sheets
Map<Class<?>, List<?>> map = ExcelHelper.readSheets("/data/all.xlsx", Book.class, Author.class);
List<Book> books = (List<Book>) map.get(Book.class);
```

### Type Conversion Support
| Java Type | Excel Cell Type | Description |
|-----------|-----------------|-------------|
| String | STRING/NUMERIC | Numeric-to-string conversion preserves full precision |
| Integer/Long | NUMERIC | Automatic conversion |
| BigDecimal/BigInteger | NUMERIC | High precision |
| Boolean | BOOLEAN/STRING | true/false or 1/0 |
| LocalDate/LocalTime/LocalDateTime | NUMERIC(date)/STRING | Requires a pattern specified via `@ExcelColumn(format)` |
| Date | NUMERIC(date) | Traditional Date type |

- Date parsing: thread-safe `DateTimeFormatter` cache
- Formula evaluation: `FormulaEvaluator` automatically evaluates formula cells

## Dependencies
- hawk-core
- Apache POI 5.5+ (poi-ooxml)
- Spring Core (for `MergedAnnotations`)

## Quick Start

```gradle
implementation "io.github.zeatul:hawk-excel:1.3.0-SNAPSHOT"
```

### Full Example: Exporting a Book List
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

// In a service
public void exportBooks(HttpServletResponse response) throws IOException {
    List<BookExportVO> data = bookService.findAllForExport();
    Map<Class<?>, List<?>> excelData = Map.of(BookExportVO.class, data);

    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    response.setHeader("Content-Disposition", "attachment; filename=books.xlsx");

    ExcelHelper.writeExcel(response.getOutputStream(), excelData); // Requires the OutputStream overload
}
```

### Import and Validation Example
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
    // Validate + save
    return Result.ok();
}
```

## Notes
- **Suitable for small files only** (everything is loaded into memory); for large files, prefer streaming writes with `SXSSFWorkbook` (not built in)
- `@ExcelColumn.index` must start at 0 and must not exceed 16383 (the Excel column limit)
- `dataStartIndex`: 0 = no header, 1 = the first row is the header (data starts from row 2)
- A template file may contain only one data row (the `dataStartIndex` row), used to extract cell styles
- When an inner/anonymous class is used as the sheet class, the cache key uses `Class<?>` to avoid `getCanonicalName()` returning null

## Version Compatibility
- Java 8+ (bytecode target 8)
- Apache POI 5.5.x
- Spring 5.3.x / 6.x
