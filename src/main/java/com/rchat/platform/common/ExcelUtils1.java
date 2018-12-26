package com.rchat.platform.common;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtils1 {
    //默认列宽度
    private final int DEFAULT_COLUMN_SIZE = 30;
    //刷新写入硬盘数据阀值
    private final int flushRows = 1000;
    //声明一个模板工作薄(写入流式数据)
    private Workbook writeDataWorkBook;
    //样式列表
    private Map<String, CellStyle> cellStyleMap;
    //Excel当前数据行数(将要写入数据的索引数)
    private int currentRowNum = 0;
    //数据输出流
    private OutputStream outputStream;

    /**
     * 断言Excel文件写入之前的条件
     *
     * @param directory 目录
     * @param fileName  文件名
     * @return file
     * @throws IOException IO异常
     */
    private File assertFile(String directory, String fileName) throws IOException {
        File tmpFile = new File(directory + File.separator + fileName + ".xlsx");
        if (tmpFile.exists()) {
            if (tmpFile.isDirectory()) {
                throw new IOException("File '" + tmpFile + "' exists but is a directory");
            }
            if (!tmpFile.canWrite()) {
                throw new IOException("File '" + tmpFile + "' cannot be written to");
            }
        } else {
            File parent = tmpFile.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    throw new IOException("Directory '" + parent + "' could not be created");
                }
            }
        }
        return tmpFile;
    }

    /**
     * 日期转化为字符串,格式为yyyy-MM-dd HH:mm:ss
     */
    private String getCnDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    /**
     * Excel 导出，POI实现，先写入Excel标题，与writeExcelData配合使用
     * 先使用writeExcelTitle再使用writeExcelData
     *
     * @param directory   目录
     * @param fileName    文件名
     * @param sheetName   sheetName
     * @param columnNames 列名集合
     * @param sheetTitle  表格标题
     */
    public void writeExcelTitle(String directory, String fileName, String sheetName, List<String> columnNames,
                                String sheetTitle) throws Exception {
        File tmpFile = assertFile(directory, fileName);
        exportExcelTitle(tmpFile, sheetName, columnNames, sheetTitle);
        loadTplWorkbook(tmpFile);
    }

    /**
     * Excel 导出，POI实现，写入Excel数据行列，与writeExcelTitle配合使用
     * 先使用writeExcelTitle再使用writeExcelData
     *
     * @param directory 目录
     * @param fileName  文件名
     * @param sheetName sheetName
     * @param objects   数据信息
     */
    public void writeExcelData(String directory, String fileName, String sheetName, List<List<Object>> objects)
            throws IOException {
        File tmpFile = assertFile(directory, fileName);
        outputStream = new FileOutputStream(tmpFile);
        exportExcelData(sheetName, objects);
    }

    /**
     * 释放资源
     */
    public void dispose() throws Exception {
        try {
            if (writeDataWorkBook != null) {
                writeDataWorkBook.write(outputStream);
            }
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
            if (cellStyleMap != null) {
                cellStyleMap.clear();
            }
            cellStyleMap = null;
            outputStream = null;
            writeDataWorkBook = null;
        } catch (IOException e) {
            throw new Exception(e);
        }
    }

    /**
     * 导出字符串数据
     *
     * @param file        文件名
     * @param columnNames 表头
     * @param sheetTitle  sheet页Title
     */
    private void exportExcelTitle(File file, String sheetName, List<String> columnNames,
                                  String sheetTitle) throws Exception {
        Workbook tplWorkBook = new XSSFWorkbook();
        Map<String, CellStyle> cellStyleMap = styleMap(tplWorkBook);
        // 表头样式
        CellStyle headStyle = cellStyleMap.get("head");
        // 生成一个表格
        Sheet sheet = tplWorkBook.getSheet(sheetName);
        if (sheet == null) {
            sheet = tplWorkBook.createSheet(sheetName);
        }
        //最新Excel列索引,从0开始
        //int lastRowIndex = sheet.getLastRowNum();
        // 设置表格默认列宽度
        sheet.setDefaultColumnWidth(DEFAULT_COLUMN_SIZE);
        // 合并单元格
        sheet.addMergedRegion(new CellRangeAddress(currentRowNum, currentRowNum, 0, columnNames.size() - 1));
        // 产生表格标题行
        Row rowMerged = sheet.createRow(currentRowNum);
        Cell mergedCell = rowMerged.createCell(0);
        mergedCell.setCellStyle(headStyle);
        mergedCell.setCellValue(new XSSFRichTextString(sheetTitle));
        //写入成功一行数据递增行数
        currentRowNum = currentRowNum + 1;
        // 产生表格表头列标题行
        Row row = sheet.createRow(currentRowNum);
        for (int i = 0; i < columnNames.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(headStyle);
            RichTextString text = new XSSFRichTextString(columnNames.get(i));
            cell.setCellValue(text);
        }
        //写入成功一行数据递增行数
        currentRowNum = currentRowNum + 1;
        try {
            OutputStream ops = new FileOutputStream(file);
            tplWorkBook.write(ops);
            ops.flush();
            ops.close();
        } catch (IOException e) {
            throw new Exception(e);
        }
    }

    /**
     * 加载模板文件
     */
    private void loadTplWorkbook(File file) throws Exception {
        try {
            XSSFWorkbook tplWorkBook = new XSSFWorkbook(new FileInputStream(file));
            writeDataWorkBook = new SXSSFWorkbook(tplWorkBook, flushRows);
            cellStyleMap = styleMap(writeDataWorkBook);
        } catch (IOException e) {
            throw new Exception("Excel模板文件不存在");
        }
    }

    /**
     * 导出字符串数据
     *
     * @param objects 目标数据
     */
    private void exportExcelData(String sheetName, List<List<Object>> objects) throws IOException {
        // 正文样式
        CellStyle contentStyle = cellStyleMap.get("content");
        //正文整数样式
        CellStyle contentIntegerStyle = cellStyleMap.get("integer");
        //正文带小数整数样式
        CellStyle contentDoubleStyle = cellStyleMap.get("double");
        // 生成一个表格
        Sheet sheet = writeDataWorkBook.getSheet(sheetName);
        if (sheet == null) {
            throw new IOException("读取Excel模板错误");
        }
        // 设置表格默认列宽度
        sheet.setDefaultColumnWidth(DEFAULT_COLUMN_SIZE);
        // 遍历集合数据,产生数据行,前两行为标题行与表头行
        for (List<Object> dataRow : objects) {
            Row row = sheet.createRow(currentRowNum);
            for (int j = 0; j < dataRow.size(); j++) {
                Cell contentCell = row.createCell(j);
                Object dataObject = dataRow.get(j);
                if (dataObject != null) {
                    if (dataObject instanceof Integer) {
                        contentCell.setCellStyle(contentIntegerStyle);
                        contentCell.setCellValue(Integer.parseInt(dataObject.toString()));
                    } else if (dataObject instanceof Double) {
                        contentCell.setCellStyle(contentDoubleStyle);
                        contentCell.setCellValue(Double.parseDouble(dataObject.toString()));
                    } else if (dataObject instanceof Long && dataObject.toString().length() == 13) {
                        contentCell.setCellStyle(contentStyle);
                        contentCell.setCellValue(getCnDate(new Date(Long.parseLong(dataObject.toString()))));
                    } else if (dataObject instanceof Date) {
                        contentCell.setCellStyle(contentStyle);
                        contentCell.setCellValue(getCnDate((Date) dataObject));
                    } else {
                        contentCell.setCellStyle(contentStyle);
                        contentCell.setCellValue(dataObject.toString());
                    }
                } else {
                    contentCell.setCellStyle(contentStyle);
                    // 设置单元格内容为字符型
                    contentCell.setCellValue("");
                }
            }
            //写入成功一行数据递增行数
            currentRowNum = currentRowNum + 1;
        }
        ((SXSSFSheet) sheet).flushRows(flushRows);
    }

    /**
     * 创建单元格表头样式
     *
     * @param workbook 工作薄
     */
    private CellStyle createCellHeadStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 设置边框样式
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        //设置对齐样式
        style.setAlignment(HorizontalAlignment.CENTER);
        // 生成字体
        Font font = workbook.createFont();
        // 表头样式
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        // 把字体应用到当前的样式
        style.setFont(font);
        return style;
    }

    /**
     * 创建单元格正文样式
     *
     * @param workbook 工作薄
     */
    private CellStyle createCellContentStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 设置边框样式
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        //设置对齐样式
        style.setAlignment(HorizontalAlignment.CENTER);
        // 生成字体
        Font font = workbook.createFont();
        // 正文样式
        style.setFillPattern(FillPatternType.NO_FILL);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        font.setBold(false);
        // 把字体应用到当前的样式
        style.setFont(font);
        return style;
    }

    /**
     * 单元格样式(Integer)列表
     */
    private CellStyle createCellContent4IntegerStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 设置边框样式
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        //设置对齐样式
        style.setAlignment(HorizontalAlignment.CENTER);
        // 生成字体
        Font font = workbook.createFont();
        // 正文样式
        style.setFillPattern(FillPatternType.NO_FILL);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        font.setBold(false);
        // 把字体应用到当前的样式
        style.setFont(font);
        style.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));//数据格式只显示整数
        return style;
    }

    /**
     * 单元格样式(Double)列表
     */
    private CellStyle createCellContent4DoubleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 设置边框样式
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        //设置对齐样式
        style.setAlignment(HorizontalAlignment.CENTER);
        // 生成字体
        Font font = workbook.createFont();
        // 正文样式
        style.setFillPattern(FillPatternType.NO_FILL);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        font.setBold(false);
        // 把字体应用到当前的样式
        style.setFont(font);
        style.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));//保留两位小数点
        return style;
    }

    /**
     * 单元格样式列表
     */
    private Map<String, CellStyle> styleMap(Workbook workbook) {
        Map<String, CellStyle> styleMap = new LinkedHashMap<>();
        styleMap.put("head", createCellHeadStyle(workbook));
        styleMap.put("content", createCellContentStyle(workbook));
        styleMap.put("integer", createCellContent4IntegerStyle(workbook));
        styleMap.put("double", createCellContent4DoubleStyle(workbook));
        return styleMap;
    }

}
