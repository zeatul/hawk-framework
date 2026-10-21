/*
 * Copyright 2025-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package glz.hawkframework.excel.helper;

import glz.hawkframework.core.helper.*;
import glz.hawkframework.excel.annotations.ExcelColumn;
import glz.hawkframework.excel.annotations.ExcelSheet;
import glz.hawkframework.excel.parse.ColumnParse;
import glz.hawkframework.excel.parse.SheetParse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.annotation.Nonnull;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static glz.hawkframework.core.helper.DateTimeHelper.*;
import static glz.hawkframework.core.support.ArgumentSupport.*;
import static glz.hawkframework.core.support.LogicSupport.consumeIfNotNull;

/**
 * This class is responsible for
 * <p></p>
 *
 * @author Zhang Peng
 */
public abstract class ExcelHelper {

    // Use Class<?> as the key to avoid NPE caused by getCanonicalName() returning null (inner classes, anonymous classes, local classes)
    private final static Map<Class<?>, SheetParse> sheetParseMap = new ConcurrentHashMap<>();

    /**
     * Write data to an Excel file
     * <p>The parameter {@code excelData} is a map whose key is a Class defining the format of a sheet, and whose value is the data to be written to that sheet</p>
     * <p>Intended for small files only</p>
     * <p>The sheet format in the template file is copied to the output file. In addition to providing the header, the template can also provide the display format of the data in the first data row; if absent, the default format is used. The template file can have only one data row</p>
     * <p>templateResourcePath must start with {@code /} and be separated by {@code /}</p>
     *
     * @param templateResourcePath the classpath path of the excel template file, used to provide the sheet format, must not be null
     * @param outputFile           the output file, must not be null
     * @param excelData            the data to populate
     */
    public static void writeExcelWithTemplate(@Nonnull String templateResourcePath, @Nonnull File outputFile, @Nonnull Map<Class<?>, List<?>> excelData) {
        argNotBlank(templateResourcePath, "templateResourcePath");
        writeExcelWithTemplate(() -> FileHelper.streamOf(templateResourcePath), outputFile, excelData);
    }

    /**
     * Write data to an Excel file
     * <p>The parameter {@code excelData} is a map whose key is a Class defining the format of a sheet, and whose value is the data to be written to that sheet</p>
     * <p>Intended for small files only</p>
     * <p>The sheet format in the template file is copied to the output file. In addition to providing the header, the template can also provide the display format of the data in the first data row; if absent, the default format is used. The template file can have only one data row</p>
     *
     * @param templateFile the excel template file, used to provide the sheet format, must not be null
     * @param outputFile   the output file, must not be null
     * @param excelData    the data to populate
     */
    public static void writeExcelWithTemplate(@Nonnull File templateFile, @Nonnull File outputFile, @Nonnull Map<Class<?>, List<?>> excelData) {
        argument(argNotNull(templateFile, "templateFile"), File::exists, f -> "The file doesn't exist, filePath = ." + f.getAbsolutePath());
        writeExcelWithTemplate(() -> {
            try {
                return new FileInputStream(templateFile);
            } catch (FileNotFoundException e) {
                throw new UncheckedIOException(e);
            }
        }, outputFile, excelData);
    }

    /**
     * Write data to an Excel file
     * <p>The parameter {@code excelData} is a map whose key is a Class defining the format of a sheet, and whose value is the data to be written to that sheet</p>
     * <p>Intended for small files only</p>
     * <p>The sheet format in the template file is copied to the output file. In addition to providing the header, the template can also provide the display format of the data in the first data row; if absent, the default format is used. The template file can have only one data row</p>
     *
     * @param templateInputStreamSupplier the input stream of the excel template file, used to provide the sheet format, must not be null
     * @param outputFile                  the output file, must not be null
     * @param excelData                   the data to populate
     */
    public static void writeExcelWithTemplate(Supplier<InputStream> templateInputStreamSupplier, @Nonnull File outputFile, @Nonnull Map<Class<?>, List<?>> excelData) {
        argNotNull(templateInputStreamSupplier, "templateInputStreamSupplier");
        argument(argNotNull(outputFile, "outputFile"), f -> !f.exists(), f -> "The file already exists, filePath = ." + f.getAbsolutePath());
        argNotEmpty(excelData, "excelData");
        try (InputStream is = templateInputStreamSupplier.get(); XSSFWorkbook workbook = new XSSFWorkbook(is); FileOutputStream fos = new FileOutputStream(outputFile)) {
            // workbook-level style cache, keyed by format string; styles provided by the template row are stored separately
            Map<String, CellStyle> dateStyleCache = new HashMap<>();
            for (Class<?> aClass : excelData.keySet()) {
                SheetParse sheetParse = getSheetParse(aClass);
                XSSFSheet sheet = workbook.getSheetAt(sheetParse.getSheetIndex());
                List<?> sheetData = excelData.get(aClass);
                // Cache the cell styles of the template file
                Map<ColumnParse, CellStyle> cellStyleMap = new HashMap<>();
                Row templateRow = sheet.getRow(sheetParse.getDataStartIndex());
                if (templateRow != null) {
                    for (ColumnParse columnParse : sheetParse.getColumns()) {
                        if (columnParse == null) continue;// skip columns that have no column
                        if (columnParse.isRowNo()) continue;
                        Cell templateCell = templateRow.getCell(columnParse.getColumnIndex());
                        if (templateCell != null) {
                            cellStyleMap.put(columnParse, templateCell.getCellStyle());
                        }
                    }
                    // Remove the template row
                    sheet.removeRow(templateRow);
                }

                // Populate data
                for (int i = 0; i < sheetData.size(); i++) {
                    XSSFRow row = sheet.createRow(i + sheetParse.getDataStartIndex()); // skip the header
                    for (int j = 0; j < sheetParse.getColumns().length; j++) {
                        XSSFCell cell = null;
                        try {
                            ColumnParse columnParse = sheetParse.getColumns()[j];
                            if (columnParse == null) continue; // skip columns that have no column
                            if (columnParse.isRowNo()) continue;
                            cell = row.createCell(columnParse.getColumnIndex());
                            Object cellValue = columnParse.getGetter().apply(sheetData.get(i));
                            setCellValue(workbook, cell, columnParse, cellStyleMap, dateStyleCache, cellValue);
                        } catch (RuntimeException ex) {
                            if (cell != null) {
                                throw new IllegalStateException(String.format("Met Error while write Cell[%d, %d] of Sheet['%s']", cell.getRowIndex(), cell.getColumnIndex(), sheet.getSheetName()), ex);
                            } else {
                                throw ex;
                            }

                        }
                    }
                }
            }
            workbook.write(fos);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    /**
     * Write data to an Excel file
     * <p>The parameter {@code excelData} is a map whose key is a Class defining the format of a sheet, and whose value is the data to be written to that sheet</p>
     * <p>Intended for small files only</p>
     *
     * @param file      the excel file path, must not be null
     * @param excelData the data to be written to excel, must not be null
     */
    public static void writeExcel(@Nonnull File file, @Nonnull Map<Class<?>, List<?>> excelData) {
        argument(argNotNull(file, "file"), f -> !f.exists(), f -> "The file already exists, filePath = ." + f.getAbsolutePath());
        argNotEmpty(excelData, "excelData");

        try (XSSFWorkbook workbook = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(file)) {
            Map<SheetParse, Sheet> sheetMap = new HashMap<>();
            // workbook-level style cache, keyed by format string
            Map<String, CellStyle> dateStyleCache = new HashMap<>();
            for (Class<?> aClass : excelData.keySet()) {
                SheetParse sheetParse = getSheetParse(aClass);
                List<?> sheetData = excelData.get(aClass);
                XSSFSheet sheet = workbook.createSheet(StringHelper.isBlank(sheetParse.getSheetName()) ? "Sheet" + sheetParse.getSheetIndex() : sheetParse.getSheetName());
                sheetMap.put(sheetParse, sheet);

                // Create the header
                int headerRowCount = createHeader(workbook, sheet, sheetParse);

                // Populate data
                Map<ColumnParse, CellStyle> cellStyleMap = new HashMap<>();
                for (int i = 0; i < sheetData.size(); i++) {
                    XSSFRow row = sheet.createRow(i + headerRowCount); // skip the header
                    for (int j = 0; j < sheetParse.getColumns().length; j++) {
                        ColumnParse columnParse = sheetParse.getColumns()[j];
                        if (columnParse == null) continue; // skip columns that have no column
                        if (columnParse.isRowNo()) continue;
                        XSSFCell cell = row.createCell(columnParse.getColumnIndex());
                        Object cellValue = columnParse.getGetter().apply(sheetData.get(i));
                        setCellValue(workbook, cell, columnParse, cellStyleMap, dateStyleCache, cellValue);
                    }
                }
            }

            sheetMap.forEach((sheetParse, sheet) -> {
                workbook.setSheetOrder(sheet.getSheetName(), sheetParse.getSheetIndex());
            });

            workbook.write(fos);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void setCellValue(XSSFWorkbook workbook, XSSFCell cell, ColumnParse columnParse,
                                     Map<ColumnParse, CellStyle> cellStyleMap,
                                     Map<String, CellStyle> dateStyleCache, Object cellValue) {
        // Set the cell style that may come from the template
        consumeIfNotNull(cellStyleMap.get(columnParse), cell::setCellStyle);
        if (!columnParse.isWritable()) return;

        if (cellValue == null) return;

        if (cellValue instanceof String) {
            cell.setCellValue((String) cellValue);
            return;
        }
        if (cellValue instanceof Boolean) {
            cell.setCellValue((Boolean) cellValue);
            return;
        }
        if (cellValue instanceof Double) {
            cell.setCellValue((Double) cellValue);
            return;
        }
        if (cellValue instanceof Date) {
            cell.setCellStyle(getOrCreateDateStyle(workbook, columnParse, dateStyleCache, (short) 20));
            cell.setCellValue((Date) cellValue);
            return;
        }
        if (cellValue instanceof LocalDateTime) {
            cell.setCellStyle(getOrCreateDateStyle(workbook, columnParse, dateStyleCache, (short) 20));
            cell.setCellValue((LocalDateTime) cellValue);
            return;
        }
        if (cellValue instanceof LocalDate) {
            cell.setCellStyle(getOrCreateDateStyle(workbook, columnParse, dateStyleCache, (short) 14));
            cell.setCellValue((LocalDate) cellValue);
            return;
        }
        if (cellValue instanceof LocalTime) {
            cell.setCellStyle(getOrCreateDateStyle(workbook, columnParse, dateStyleCache, (short) 21));
            // excel does not support LocalTime, so LocalTime can only be converted to Date, and then the format is set to display only the time
            Date date = Date.from(((LocalTime) cellValue).atDate(LocalDate.of(1970, 1, 1)).atZone(ZoneId.systemDefault()).toInstant());
            cell.setCellValue(date);
            return;
        }
        if (cellValue instanceof BigDecimal) {
            cell.setCellValue(((BigDecimal) cellValue).doubleValue());
            return;
        }
        if (cellValue instanceof BigInteger) {
            cell.setCellValue(((BigInteger) cellValue).doubleValue());
            return;
        }
        if (cellValue instanceof Integer) {
            cell.setCellValue((Integer) cellValue);
            return;
        }
        if (cellValue instanceof Long) {
            cell.setCellValue((Long) cellValue);
            return;
        }
        if (cellValue instanceof Float) {
            cell.setCellValue((Float) cellValue);
            return;
        }
    }

    /**
     * Get or create the cell style for the date type
     * <p>The key of the style cache consists of the "format string + default format code", so that a style with the same format is created only once within the same workbook,
     * avoiding repeated calls to {@link Workbook#createCellStyle()} that would hit Excel's style count limit (about 64000).</p>
     *
     * @param workbook      the workbook
     * @param columnParse   the column definition, used to get the custom format
     * @param dateStyleCache the workbook-level style cache
     * @param defaultFormat the default format code used when no format is specified
     * @return the cell style
     */
    private static CellStyle getOrCreateDateStyle(XSSFWorkbook workbook, ColumnParse columnParse,
                                                  Map<String, CellStyle> dateStyleCache, short defaultFormat) {
        final String format = columnParse.getFormat();
        final String cacheKey = StringHelper.isNotBlank(format) ? format : "default:" + defaultFormat;
        return dateStyleCache.computeIfAbsent(cacheKey, k -> {
            CellStyle style = workbook.createCellStyle();
            if (StringHelper.isNotBlank(format)) {
                style.setDataFormat(workbook.createDataFormat().getFormat(format));
            } else {
                style.setDataFormat(defaultFormat);
            }
            return style;
        });
    }

    /**
     * Create the header
     *
     * @return the number of header rows
     */
    private static int createHeader(XSSFWorkbook workbook, XSSFSheet sheet, SheetParse sheetParse) {
        if (sheetParse.getDataStartIndex() == 0) {
            return 0;
        } else if (sheetParse.getDataStartIndex() == 1) {
            XSSFRow row = sheet.createRow(0);
            for (int i = 0; i < sheetParse.getColumns().length; i++) {
                ColumnParse columnParse = sheetParse.getColumns()[i];
                if (columnParse == null) continue; // skip columns that have no column
                if (columnParse.isRowNo()) continue;
                XSSFCell cell = row.createCell(columnParse.getColumnIndex()); // Changed from cell = row.createCell(i);
                cell.setCellValue(StringHelper.isNotBlank(columnParse.getColumnName()) ? columnParse.getColumnName() : "column" + i);
            }
            return 1;
        } else {
            throw new IllegalStateException("Only Support DataStartIndex which is equal to 0 or 1");
        }
    }

    /**
     * Read all the data of some sheets of an Excel file into a map
     * <p>The returned result is a map whose key is a Class (aClass) and whose value is a List of that aClass, containing the data of the corresponding sheet of the Excel file</p>
     * <p>Intended for small files only</p>
     *
     * @param file     the excel file
     * @param aClasses one aClass corresponds to one sheet
     * @return a map containing all the corresponding sheets
     */
    public static Map<Class<?>, List<?>> readSheets(@Nonnull File file, Class<?>... aClasses) {
        argument(argNotNull(file, "file"), f -> f.exists() && f.isFile(), f -> "The file does not exist, filePath = ." + file.getAbsolutePath());
        return readSheets(() -> {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }, aClasses);
    }

    /**
     * Read all the data of some sheets of an Excel file into a map
     * <p>The returned result is a map whose key is a Class (aClass) and whose value is a List of that aClass, containing the data of the corresponding sheet of the Excel file</p>
     * <p>Intended for small files only</p>
     *
     * @param resourcePath the classpath path of the excel file
     * @param aClasses     one aClass corresponds to one sheet
     * @return a map containing all the corresponding sheets
     */
    public static Map<Class<?>, List<?>> readSheets(@Nonnull String resourcePath, Class<?>... aClasses) {
        return readSheets(() -> FileHelper.streamOf(resourcePath), aClasses);
    }

    private static Map<Class<?>, List<?>> readSheets(Supplier<InputStream> inputStreamSupplier, Class<?>... aClasses) {
        argNotEmpty(aClasses, "aClasses");
        Map<Integer, SheetParse> sheetParseMap = new HashMap<>();
        for (Class<?> aClass : aClasses) {
            SheetParse sheetParse = getSheetParse(aClass);
            if (sheetParseMap.put(sheetParse.getSheetIndex(), sheetParse) != null) throw new IllegalStateException("Found Duplicated SheetIndex:" + sheetParse.getSheetIndex());
        }
        Map<Class<?>, List<?>> resultMap = new HashMap<>();
        try (InputStream is = inputStreamSupplier.get(); XSSFWorkbook workbook = new XSSFWorkbook(is)) {
            sheetParseMap.values().stream().forEach(sheetParse -> {
                XSSFSheet sheet = workbook.getSheetAt(sheetParse.getSheetIndex());
                List<?> list = readOneSheet(workbook, sheet, sheetParse);
                if (resultMap.put(sheetParse.getType(), list) != null) throw new IllegalStateException("Found Duplicated aClass: " + sheetParse.getType().getCanonicalName());
            });
            return resultMap;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Read all the data of a sheet of an Excel file into a collection.
     * <p>Intended for small files only</p>
     * <p>The mapping between the Excel sheet and the receiving entity class is configured by that entity class's annotations</p>
     *
     * @param file   the file name
     * @param aClass the class that receives a single record
     * @param <T>    the type of the class that receives a single record
     * @return the returned data collection, must not be null
     * @see ExcelSheet
     * @see ExcelColumn
     */
    @Nonnull
    public static <T> List<T> readOneSheet(@Nonnull File file, @Nonnull Class<T> aClass) {
        argument(argNotNull(file, "file"), f -> f.exists() && f.isFile(), f -> "The file does not exist, filePath = ." + file.getAbsolutePath());
        return readOneSheet(() -> {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new UncheckedIOException(e);
            }
        }, aClass);
    }

    /**
     * Read all the data of a sheet of an Excel file under the classpath into a collection.
     * <p>Intended for small files only</p>
     * <p>The mapping between the Excel sheet and the receiving entity class is configured by that entity class's annotations</p>
     * <p>The resourcePath must start with '/'.</p>
     * <p>Package names are separated by "/".</p>
     *
     * @param resourcePath The resource path of the Excel file.
     * @param aClass       the class that receives a single record
     * @param <T>          the type of the class that receives a single record
     * @return the returned data collection, must not be null
     * @see ExcelSheet
     * @see ExcelColumn
     */
    public static <T> List<T> readOneSheet(@Nonnull String resourcePath, @Nonnull Class<T> aClass) {
        return readOneSheet(() -> FileHelper.streamOf(resourcePath), aClass);
    }

    private static <T> List<T> readOneSheet(Supplier<InputStream> inputStreamSupplier, Class<T> aClass) {
        SheetParse sheetParse = getSheetParse(argNotNull(aClass, "aClass"));
        try (InputStream is = inputStreamSupplier.get(); XSSFWorkbook workbook = new XSSFWorkbook(is)) {
            XSSFSheet sheet = workbook.getSheetAt(sheetParse.getSheetIndex());
            return readOneSheet(workbook, sheet, sheetParse);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> readOneSheet(XSSFWorkbook workbook, XSSFSheet sheet, SheetParse sheetParse) {
        List<T> list = new ArrayList<>();
        for (Row row : sheet) {
            // Ignore the possible title row
            if (row.getRowNum() < sheetParse.getDataStartIndex()) continue;

            T object = (T) ReflectHelper.instance(sheetParse.getType());
            list.add(object);
            sheetParse.getRowNoColumns().forEach(c -> c.getSetter().accept(object, row.getRowNum()));

            for (Cell cell : row) {
                try {
                    ColumnParse[] columns = sheetParse.getColumns();
                    if (columns == null) continue;
                    if (cell.getColumnIndex() >= columns.length) {
                        throw new IllegalStateException(String.format("The column index %d of Sheet['%s'] exceeds the max configured column index %d.", cell.getColumnIndex(), sheet.getSheetName(), columns.length - 1));
                    }
                    Object cellValue = getCellValue(workbook, cell);
                    ColumnParse columnParse = columns[cell.getColumnIndex()];
                    if (columnParse == null || !columnParse.isReadable()) continue;
                    Object columnValue = getColumnValue(cell, cellValue, columnParse.getValueType(), columnParse.getFieldName(), columnParse.getFormat());
                    if (columnValue != null) columns[cell.getColumnIndex()].getSetter().accept(object, columnValue);
                } catch (Exception ex) {
                    throw new IllegalStateException(String.format("Met Error while read Cell[%d, %d] of Sheet['%s']", cell.getRowIndex(), cell.getColumnIndex(), sheet.getSheetName()), ex);
                }
            }
        }
        return list;
    }

    private static SheetParse getSheetParse(Class<?> aClass) {
        argNotNull(aClass, "aClass");

        SheetParse sheetParse = sheetParseMap.get(aClass);
        if (sheetParse != null) return sheetParse;

        sheetParse = new SheetParse();
        sheetParse.setType(aClass);
        ExcelSheet excelSheet = aClass.getAnnotation(ExcelSheet.class);
        if (excelSheet == null) throw new IllegalArgumentException("The class must be annotated with @ExcelSheet: " + aClass.getName());

        sheetParse.setDataStartIndex(excelSheet.dataStartIndex());
        if (sheetParse.getDataStartIndex() <= 0) throw new IllegalArgumentException("The dataStartIndex of ExcelSheet must be greater than 0.");

        if (StringHelper.isNotBlank(excelSheet.sheetName())) sheetParse.setSheetName(excelSheet.sheetName());

        sheetParse.setSheetIndex(excelSheet.sheetIndex());
        if (sheetParse.getSheetIndex() < 0) throw new IllegalArgumentException("The sheetIndex of ExcelSheet must be greater than 0.");

        List<ColumnParse> columnParseList = new ArrayList<>();
        List<ColumnParse> rowNoColumnList = new ArrayList<>();
        Arrays.stream(aClass.getDeclaredFields()).forEach(field -> {
            ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
            if (excelColumn == null) return;
            ColumnParse columnParse = new ColumnParse();
            columnParse.setColumnIndex(excelColumn.index());
            columnParse.setFieldName(field.getName());
            columnParse.setValueType(field.getType());
            columnParse.setGetter(LambdaHelper.createGetter(ReflectHelper.getGetter(field)));
            columnParse.setSetter(LambdaHelper.createSetter(ReflectHelper.getSetter(field)));
            columnParse.setColumnName(StringHelper.isBlank(excelColumn.name()) ? null : excelColumn.name());
            columnParse.setRowNo(excelColumn.isRowNo());
            columnParse.setFormat(excelColumn.format());
            columnParse.setReadable(excelColumn.readable());
            columnParse.setWritable(excelColumn.writable());
            if (!columnParse.isRowNo() && columnParse.getColumnIndex() < 0) {
                throw new IllegalStateException(String.format("The index of ExcelColumn must be greater than or equal to 0. field = %s", columnParse.getFieldName()));
            }
            if (columnParse.getColumnIndex() >= 16384) {
                throw new IllegalStateException(String.format("The index of ExcelColumn must be less than 16384. field = %s", columnParse.getFieldName()));
            }

            if (columnParse.isRowNo()) {
                rowNoColumnList.add(columnParse);
            } else {
                columnParseList.add(columnParse);
            }
        });

        sheetParse.setRowNoColumns(rowNoColumnList);

        if (columnParseList.isEmpty() && rowNoColumnList.isEmpty()) throw new IllegalStateException("There is no ExcelColumn annotation in the class.");

        if (!columnParseList.isEmpty()) {
            OptionalInt maxColumnIndex = columnParseList.stream().map(ColumnParse::getColumnIndex).mapToInt(Integer::intValue).max();
            int max = maxColumnIndex.orElse(0);
            ColumnParse[] columns = new ColumnParse[max + 1];
            columnParseList.forEach(columnParse -> {
                columns[columnParse.getColumnIndex()] = columnParse;
            });
            sheetParse.setColumns(columns);
        }

        sheetParseMap.put(aClass, sheetParse);

        return sheetParse;
    }

    private static Object getColumnValue(Cell cell, Object cellValue, Class<?> columnValueType, String fieldName, String format) {
        if (cellValue == null) return null;

        // The receiving type is String
        if (columnValueType.isAssignableFrom(String.class)) {
            if (cellValue instanceof Double) {
                // Date
                // Handle numbers and determine whether they are of date type
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = DateUtil.getJavaDate(cell.getNumericCellValue());
                    if (StringHelper.isBlank(format)) throw new IllegalStateException(String.format("The format of field[%s] is blank.", fieldName));
                    return getDateString(date, format);
                } else {
                    return BigDecimal.valueOf((Double) cellValue).toPlainString();
                }
            } else {
                return String.valueOf(cellValue);
            }
        }
        // The receiving type is Integer
        else if (columnValueType.isAssignableFrom(Integer.class)) {
            if (cellValue instanceof Double) return ((Double) cellValue).intValue();
            if (cellValue instanceof String) return Integer.valueOf((String) cellValue);
            throw new IllegalStateException(String.format("The value of cell[%d, %d] is illegal, can't be convert to Integer.", cell.getRowIndex(), cell.getColumnIndex()));
        }// The receiving type is Long
        else if (columnValueType.isAssignableFrom(Long.class)) {
            if (cellValue instanceof Double) return ((Double) cellValue).longValue();
            if (cellValue instanceof String) return Long.valueOf((String) cellValue);
            throw new IllegalStateException(String.format("The value of cell[%d, %d] is illegal, can't be convert to Long.", cell.getRowIndex(), cell.getColumnIndex()));
        }// The receiving type is BigInteger
        else if (columnValueType.isAssignableFrom(BigInteger.class)) {
            if (cellValue instanceof Double) return BigInteger.valueOf((long) cellValue);
            if (cellValue instanceof String) return new BigInteger((String) cellValue);
            throw new IllegalStateException(String.format("The value of cell[%d, %d] is illegal, can't be convert to Integer.", cell.getRowIndex(), cell.getColumnIndex()));
        }

        // The receiving type is BigDecimal
        else if (columnValueType.isAssignableFrom(BigDecimal.class)) {
            if (cellValue instanceof Double) return BigDecimal.valueOf((Double) cellValue);
            if (cellValue instanceof String) return new BigDecimal((String) cellValue);
            throw new IllegalStateException(String.format("The value of cell[%d, %d] is illegal, can't be convert to Long.", cell.getRowIndex(), cell.getColumnIndex()));
        }
        // The receiving type is boolean
        else if (columnValueType.isAssignableFrom(Boolean.class)) {
            if (cellValue instanceof Boolean) return cellValue;
            if (cellValue instanceof String) return Boolean.parseBoolean((String) cellValue);
            throw new IllegalStateException(String.format("The value of cell[%d, %d] is illegal, can't be convert to boolean.", cell.getRowIndex(), cell.getColumnIndex()));
        }
        // The receiving type is LocalDate
        else if (columnValueType.isAssignableFrom(LocalDate.class)) {
            if (cellValue instanceof Double) {
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = DateUtil.getJavaDate(cell.getNumericCellValue());
                    return getLocaleDate(date);
                } else {
                    throw new IllegalStateException(String.format("The value of cell[%d, %d] is illegal, can't be convert to LocalDate.", cell.getRowIndex(), cell.getColumnIndex()));
                }
            } else if (cellValue instanceof String) {
                if (StringHelper.isBlank(format)) throw new IllegalStateException(String.format("The format of field[%s] is blank.", fieldName));
                return toLocaleDate((String) cellValue, format);
            } else {
                throw new IllegalStateException(String.format("The value of cell[%d, %d] is illegal, can't be convert to LocalDate.", cell.getRowIndex(), cell.getColumnIndex()));
            }
        }// The receiving type is LocalTime
        else if (columnValueType.isAssignableFrom(LocalTime.class)) {
            if (cellValue instanceof Double) {
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = DateUtil.getJavaDate(cell.getNumericCellValue());
                    return getLocaleTime(date);
                } else {
                    throw new IllegalStateException(String.format("The value of cell[%d, %d] is illegal, can't be convert to LocalTime.", cell.getRowIndex(), cell.getColumnIndex()));
                }
            } else if (cellValue instanceof String) {
                if (StringHelper.isBlank(format)) throw new IllegalStateException(String.format("The format of field[%s] is blank.", fieldName));
                return toLocaleTime((String) cellValue, format);
            } else {
                throw new IllegalStateException(String.format("The value of cell[%d, %d] is illegal, can't be convert to LocalTime.", cell.getRowIndex(), cell.getColumnIndex()));
            }
        }// The receiving type is LocalDateTime
        else if (columnValueType.isAssignableFrom(LocalDateTime.class)) {
            if (cellValue instanceof Double) {
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = DateUtil.getJavaDate(cell.getNumericCellValue());
                    return getLocaleDateTime(date);
                } else {
                    throw new IllegalStateException(String.format("The value of cell[%d, %d] is illegal, can't be convert to LocalDateTime.", cell.getRowIndex(), cell.getColumnIndex()));
                }
            } else if (cellValue instanceof String) {
                if (StringHelper.isBlank(format)) throw new IllegalStateException(String.format("The format of field[%s] is blank.", fieldName));
                return toLocaleDateTime((String) cellValue, format);
            } else {
                throw new IllegalStateException(String.format("The value of cell[%d, %d] is illegal, can't be convert to LocalDateTime.", cell.getRowIndex(), cell.getColumnIndex()));
            }
        } else {
            throw new IllegalStateException(String.format("The field Type: %s is unsupported", columnValueType.getCanonicalName()));
        }
    }


    private static LocalDateTime getLocaleDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private static LocalDate getLocaleDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static LocalTime getLocaleTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    }

    private static String getDateString(Date date, String format) {
        // Use DateTimeFormatter instead of SimpleDateFormat to avoid thread-safety issues
        return DateTimeFormatter.ofPattern(format).withZone(ZoneId.systemDefault()).format(date.toInstant());
    }

    public static Object getCellValue(XSSFWorkbook workbook, Cell cell) {
        int rowIndex = cell.getRowIndex();
        int columnIndex = cell.getColumnIndex();
        switch (cell.getCellType()) {
            case BLANK:
                return null;
            case STRING:
                return cell.getStringCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case ERROR:
                throw new IllegalStateException(String.format("The cell[%d, %d] value is illegal. ", rowIndex, columnIndex));
            case NUMERIC:
                return cell.getNumericCellValue();
            case FORMULA:
                return getFormulaValue(workbook, cell);
            default:
                throw new IllegalStateException(String.format("Cell[%d, %d] Has Unsupported CellType: %s", rowIndex, columnIndex, cell.getCellType().name()));
        }
    }

    public static Object getFormulaValue(XSSFWorkbook workbook, Cell cell) {
        int rowIndex = cell.getRowIndex();
        int columnIndex = cell.getColumnIndex();
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        CellValue result = evaluator.evaluate(cell);
        switch (result.getCellType()) {
            case NUMERIC:
                return result.getNumberValue();
            case BLANK:
                return null;
            case BOOLEAN:
                return result.getBooleanValue();
            case STRING:
                return result.getStringValue();
            case ERROR:
                throw new IllegalStateException(String.format("The cell[%d, %d] value is illegal. ", rowIndex, columnIndex));
            default:
                throw new IllegalStateException(String.format("Cell[%d, %d] Has Unsupported CellType: %s", rowIndex, columnIndex, result.getCellType().name()));
        }
    }

}
