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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
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

    private final static Map<String, SheetParse> sheetParseMap = new ConcurrentHashMap<>();

    /**
     * 将数据写入Excel文件
     * <p>参数{@code excelData}是一个map，key为Class，定义了一个sheet的格式，value为要写入这个sheet的数据</p>
     * <p>只适用于小文件</p>
     * <p>模板文件中的sheet格式会复制到输出文件中，模板除了提供表头外，还可以在第一个数据行，提供数据的显示格式，没有则使用默认格式。模板文件只能有一个数据行</p>
     * <p>templateResourcePath必须用{@code /}开始，用{@code /}分割</p>
     *
     * @param templateResourcePath excel模板文件的classpath路径，用来提供sheet的格式，不可为空
     * @param outputFile           输出文件，不可为空
     * @param excelData            要填充的数据
     */
    public static void writeExcelWithTemplate(@Nonnull String templateResourcePath, @Nonnull File outputFile, @Nonnull Map<Class<?>, List<?>> excelData) {
        argNotBlank(templateResourcePath, "templateResourcePath");
        writeExcelWithTemplate(() -> FileHelper.streamOf(templateResourcePath), outputFile, excelData);
    }

    /**
     * 将数据写入Excel文件
     * <p>参数{@code excelData}是一个map，key为Class，定义了一个sheet的格式，value为要写入这个sheet的数据</p>
     * <p>只适用于小文件</p>
     * <p>模板文件中的sheet格式会复制到输出文件中，模板除了提供表头外，还可以在第一个数据行，提供数据的显示格式，没有则使用默认格式。模板文件只能有一个数据行</p>
     *
     * @param templateFile excel模板文件，用来提供sheet的格式，不可为空
     * @param outputFile   输出文件，不可为空
     * @param excelData    要填充的数据
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
     * 将数据写入Excel文件
     * <p>参数{@code excelData}是一个map，key为Class，定义了一个sheet的格式，value为要写入这个sheet的数据</p>
     * <p>只适用于小文件</p>
     * <p>模板文件中的sheet格式会复制到输出文件中，模板除了提供表头外，还可以在第一个数据行，提供数据的显示格式，没有则使用默认格式。模板文件只能有一个数据行</p>
     *
     * @param templateInputStreamSupplier excel模板文件的输入流，用来提供sheet的格式，不可为空
     * @param outputFile                  输出文件，不可为空
     * @param excelData                   要填充的数据
     */
    public static void writeExcelWithTemplate(Supplier<InputStream> templateInputStreamSupplier, @Nonnull File outputFile, @Nonnull Map<Class<?>, List<?>> excelData) {
        argNotNull(templateInputStreamSupplier, "templateInputStreamSupplier");
        argument(argNotNull(outputFile, "outputFile"), f -> !f.exists(), f -> "The file already exists, filePath = ." + f.getAbsolutePath());
        argNotEmpty(excelData, "excelData");
        try (InputStream is = templateInputStreamSupplier.get(); XSSFWorkbook workbook = new XSSFWorkbook(is); FileOutputStream fos = new FileOutputStream(outputFile)) {
            for (Class<?> aClass : excelData.keySet()) {
                SheetParse sheetParse = getSheetParse(aClass);
                XSSFSheet sheet = workbook.getSheetAt(sheetParse.getSheetIndex());
                List<?> sheetData = excelData.get(aClass);
                // 缓存模板文件的单元格样式
                Map<ColumnParse, CellStyle> cellStyleMap = new HashMap<>();
                Row templateRow = sheet.getRow(sheetParse.getDataStartIndex());
                if (templateRow != null) {
                    for (ColumnParse columnParse : sheetParse.getColumns()) {
                        if (columnParse == null) continue;// 跳过没有列的列
                        if (columnParse.isRowNo()) continue;
                        Cell templateCell = templateRow.getCell(columnParse.getColumnIndex());
                        if (templateCell != null) {
                            cellStyleMap.put(columnParse, templateCell.getCellStyle());
                        }
                    }
                    // 删除模板行
                    sheet.removeRow(templateRow);
                }

                // 填充数据
                for (int i = 0; i < sheetData.size(); i++) {
                    XSSFRow row = sheet.createRow(i + sheetParse.getDataStartIndex()); // 跳过表头
                    for (int j = 0; j < sheetParse.getColumns().length; j++) {
                        XSSFCell cell = null;
                        try {
                            ColumnParse columnParse = sheetParse.getColumns()[j];
                            if (columnParse == null) continue; // 跳过没有列的列
                            if (columnParse.isRowNo()) continue;
                            cell = row.createCell(columnParse.getColumnIndex());
                            Object cellValue = columnParse.getGetter().apply(sheetData.get(i));
                            setCellValue(workbook, cell, columnParse, cellStyleMap, cellValue);
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
     * 将数据写入Excel文件
     * <p>参数{@code excelData}是一个map，key为Class，定义了一个sheet的格式，value为要写入这个sheet的数据</p>
     * <p>只适用于小文件</p>
     *
     * @param file      excel文件地址，不可为空
     * @param excelData 要写入excel的数据，不可为空
     */
    public static void writeExcel(@Nonnull File file, @Nonnull Map<Class<?>, List<?>> excelData) {
        argument(argNotNull(file, "file"), f -> !f.exists(), f -> "The file already exists, filePath = ." + f.getAbsolutePath());
        argNotEmpty(excelData, "excelData");

        try (XSSFWorkbook workbook = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(file)) {
            Map<SheetParse, Sheet> sheetMap = new HashMap<>();
            for (Class<?> aClass : excelData.keySet()) {
                SheetParse sheetParse = getSheetParse(aClass);
                List<?> sheetData = excelData.get(aClass);
                XSSFSheet sheet = workbook.createSheet(StringHelper.isBlank(sheetParse.getSheetName()) ? "Sheet" + sheetParse.getSheetIndex() : sheetParse.getSheetName());
                sheetMap.put(sheetParse, sheet);

                // 创建表头
                int headerRowCount = createHeader(workbook, sheet, sheetParse);

                // 填充数据
                Map<ColumnParse, CellStyle> cellStyleMap = new HashMap<>();
                for (int i = 0; i < sheetData.size(); i++) {
                    XSSFRow row = sheet.createRow(i + headerRowCount); // 跳过表头
                    for (int j = 0; j < sheetParse.getColumns().length; j++) {
                        ColumnParse columnParse = sheetParse.getColumns()[j];
                        if (columnParse == null) continue; // 跳过没有列的列
                        if (columnParse.isRowNo()) continue;
                        XSSFCell cell = row.createCell(columnParse.getColumnIndex());
                        Object cellValue = columnParse.getGetter().apply(sheetData.get(i));
                        setCellValue(workbook, cell, columnParse, cellStyleMap, cellValue);
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

    private static void setCellValue(XSSFWorkbook workbook, XSSFCell cell, ColumnParse columnParse, Map<ColumnParse, CellStyle> cellStyleMap, Object cellValue) {
        // 设置可能存在的来自于模板的单元格样式
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
            CellStyle cellStyle = cellStyleMap.computeIfAbsent(columnParse, c -> {
                CellStyle s = workbook.createCellStyle();
                if (StringHelper.isNotBlank(columnParse.getFormat())) {
                    s.setDataFormat(workbook.createDataFormat().getFormat(columnParse.getFormat()));
                } else {
                    s.setDataFormat((short) 20);
                }
                return s;
            });
            cell.setCellStyle(cellStyle);
            cell.setCellValue((Date) cellValue);
            return;
        }
        if (cellValue instanceof LocalDateTime) {
            CellStyle cellStyle = cellStyleMap.computeIfAbsent(columnParse, c -> {
                CellStyle s = workbook.createCellStyle();
                if (StringHelper.isNotBlank(columnParse.getFormat())) {
                    s.setDataFormat(workbook.createDataFormat().getFormat(columnParse.getFormat()));
                } else {
                    s.setDataFormat((short) 20);
                }
                return s;
            });
            cell.setCellStyle(cellStyle);
            cell.setCellValue((LocalDateTime) cellValue);
            return;
        }
        if (cellValue instanceof LocalDate) {
            CellStyle cellStyle = cellStyleMap.computeIfAbsent(columnParse, c -> {
                CellStyle s = workbook.createCellStyle();
                if (StringHelper.isNotBlank(columnParse.getFormat())) {
                    s.setDataFormat(workbook.createDataFormat().getFormat(columnParse.getFormat()));
                } else {
                    s.setDataFormat((short) 14);
                }
                return s;
            });
            cell.setCellStyle(cellStyle);
            cell.setCellValue((LocalDate) cellValue);
            return;
        }
        if (cellValue instanceof LocalTime) {
//            if (StringHelper.isNotBlank(columnParse.getFormat())) {
//                cell.setCellValue(DateTimeHelper.format((LocalTime) cellValue, columnParse.getFormat()));
//            } else {
//                cell.setCellValue(cellValue.toString());
//            }
            CellStyle cellStyle = cellStyleMap.computeIfAbsent(columnParse, c -> {
                CellStyle s = workbook.createCellStyle();
                if (StringHelper.isNotBlank(columnParse.getFormat())) {
                    s.setDataFormat(workbook.createDataFormat().getFormat(columnParse.getFormat()));
                } else {
                    s.setDataFormat((short) 21);
                }
                return s;
            });
            cell.setCellStyle(cellStyle);
            // excel不支持LocalTime，只能将LocalTime转为Date，再设置format格式只显示时间
            Date date = Date.from(((LocalTime) cellValue).atDate(java.time.LocalDate.of(1970, 1, 1)).atZone(ZoneId.systemDefault()).toInstant());
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
     * 创建表头
     *
     * @return 表头行数
     */
    private static int createHeader(XSSFWorkbook workbook, XSSFSheet sheet, SheetParse sheetParse) {
        if (sheetParse.getDataStartIndex() == 0) {
            return 0;
        } else if (sheetParse.getDataStartIndex() == 1) {
            XSSFRow row = sheet.createRow(0);
            for (int i = 0; i < sheetParse.getColumns().length; i++) {
                ColumnParse columnParse = sheetParse.getColumns()[i];
                if (columnParse == null) continue; // 跳过没有列的列
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
     * 读取Excel文件的某些Sheet页的全部数据，放到Map里
     * <p>返回结果是一个map，map的key是aClass，value是该aClass的List，存放了Excel的对应sheet页的数据</p>
     * <p>只适用于小文件</p>
     *
     * @param file     excel文件
     * @param aClasses 一个aClass对应一个sheet
     * @return 接收所有对应sheet的map
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
     * 读取Excel文件的某些Sheet页的全部数据，放到Map里
     * <p>返回结果是一个map，map的key是aClass，value是该aClass的List，存放了Excel的对应sheet页的数据</p>
     * <p>只适用于小文件</p>
     *
     * @param resourcePath excel文件的classpath路径
     * @param aClasses     一个aClass对应一个sheet
     * @return 接收所有对应sheet的map
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
     * 读取Excel文件的某个Sheet页的全部数据，放到集合里.
     * <p>只适用于小文件</p>
     * <p>Excel的Sheet页和接收实体类的对应关系，由该实体类的注解来配置</p>
     *
     * @param file   文件名
     * @param aClass 接收单个数据的类
     * @param <T>    接收单个数据的类的类型
     * @return 返回数据集合，不可为空
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
     * 读取classpath下的Excel文件的某个Sheet页的全部数据，放到集合里.
     * <p>只适用于小文件</p>
     * <p>Excel的Sheet页和接收实体类的对应关系，由该实体类的注解来配置</p>
     * <p>The resourcePath must start with '/'.</p>
     * <p>Package names are separated by "/".</p>
     *
     * @param resourcePath The resource path of the Excel file.
     * @param aClass       接收单个数据的类
     * @param <T>          接收单个数据的类的类型
     * @return 返回数据集合，不可为空
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
            // 忽略掉可能的标题行
            if (row.getRowNum() < sheetParse.getDataStartIndex()) continue;

            T object = (T) ReflectHelper.instance(sheetParse.getType());
            list.add(object);

            for (Cell cell : row) {
                try {
                    Object cellValue = getCellValue(workbook, cell);
                    ColumnParse columnParse = sheetParse.getColumns()[cell.getColumnIndex()];
                    if (columnParse == null || !columnParse.isReadable()) continue;
                    Object columnValue = getColumnValue(cell, cellValue, columnParse.getValueType(), columnParse.getFieldName(), columnParse.getFormat());
                    if (columnValue != null) sheetParse.getColumns()[cell.getColumnIndex()].getSetter().accept(object, columnValue);

                    sheetParse.getRowNoColumns().forEach(c -> {
                        c.getSetter().accept(object, cell.getRowIndex());
                    });
                } catch (Exception ex) {
                    throw new IllegalStateException(String.format("Met Error while read Cell[%d, %d] of Sheet['%s']", cell.getRowIndex(), cell.getColumnIndex(), sheet.getSheetName()), ex);
                }
            }
        }
        return list;
    }

    private static SheetParse getSheetParse(Class<?> aClass) {
        argNotNull(aClass, "aClass");

        SheetParse sheetParse = sheetParseMap.get(aClass.getCanonicalName());
        if (sheetParse != null) return sheetParse;

        sheetParse = new SheetParse();
        sheetParse.setType(aClass);
        ExcelSheet excelSheet = aClass.getAnnotation(ExcelSheet.class);

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

        sheetParseMap.put(aClass.getCanonicalName(), sheetParse);

        return sheetParse;
    }

    private static Object getColumnValue(Cell cell, Object cellValue, Class<?> columnValueType, String fieldName, String format) {
        if (cellValue == null) return null;

        // 接收类型为String
        if (columnValueType.isAssignableFrom(String.class)) {
            if (cellValue instanceof Double) {
                // Date
                // 处理数字，判断是否为日期类型
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = DateUtil.getJavaDate(cell.getNumericCellValue());
                    if (StringHelper.isBlank(format)) throw new IllegalStateException(String.format("The format of field[%s] is blank.", fieldName));
                    return getDateString(date, format);
                } else {
                    return String.valueOf(cellValue);
                }
            } else {
                return String.valueOf(cellValue);
            }
        }
        // 接收类型为Integer
        else if (columnValueType.isAssignableFrom(Integer.class)) {
            if (cellValue instanceof Double) return ((Double) cellValue).intValue();
            if (cellValue instanceof String) return Integer.valueOf((String) cellValue);
            throw new IllegalStateException(String.format("The value of cell[%d, %d] is illegal, can't be convert to Integer.", cell.getRowIndex(), cell.getColumnIndex()));
        }// 接收类型为Long
        else if (columnValueType.isAssignableFrom(Long.class)) {
            if (cellValue instanceof Double) return ((Double) cellValue).longValue();
            if (cellValue instanceof String) return Long.valueOf((String) cellValue);
            throw new IllegalStateException(String.format("The value of cell[%d, %d] is illegal, can't be convert to Long.", cell.getRowIndex(), cell.getColumnIndex()));
        }// 接收类型为BigInteger
        else if (columnValueType.isAssignableFrom(BigInteger.class)) {
            if (cellValue instanceof Double) return BigInteger.valueOf((long) cellValue);
            if (cellValue instanceof String) return new BigInteger((String) cellValue);
            throw new IllegalStateException(String.format("The value of cell[%d, %d] is illegal, can't be convert to Integer.", cell.getRowIndex(), cell.getColumnIndex()));
        }

        // 接收类型为BigDecimal
        else if (columnValueType.isAssignableFrom(BigDecimal.class)) {
            if (cellValue instanceof Double) return BigDecimal.valueOf((Double) cellValue);
            if (cellValue instanceof String) return new BigDecimal((String) cellValue);
            throw new IllegalStateException(String.format("The value of cell[%d, %d] is illegal, can't be convert to Long.", cell.getRowIndex(), cell.getColumnIndex()));
        }
        // 接收类型为boolean
        else if (columnValueType.isAssignableFrom(Boolean.class)) {
            if (cellValue instanceof Boolean) return cellValue;
            if (cellValue instanceof String) return Boolean.parseBoolean((String) cellValue);
            throw new IllegalStateException(String.format("The value of cell[%d, %d] is illegal, can't be convert to boolean.", cell.getRowIndex(), cell.getColumnIndex()));
        }
        // 接收类型为LocalDate
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
        }// 接收类型为LocalTime
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
        }// 接收类型为LocalDateTime
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
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
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
