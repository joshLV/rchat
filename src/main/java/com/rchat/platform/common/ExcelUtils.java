package com.rchat.platform.common;

import com.rchat.platform.domain.User;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

public class ExcelUtils {
    public static void main(String[] args) throws IOException {
        FileOutputStream out = new FileOutputStream("hello.xlsx");
        export(out, Collections.emptyList(), Collections.emptyList(), User.class);
        out.close();
    }

    public static <T> void export(OutputStream out, List<String> headers, List<T> data, Class<T> clazz) {
        try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {

            SXSSFSheet sheet = workbook.createSheet();

            Field[] fields = clazz.getDeclaredFields();
            SXSSFRow header = sheet.createRow(0);
            for (int i = 0; i < fields.length; i++) {
                SXSSFCell cell = header.createCell(i);
                cell.setCellValue(fields[i].getName());
            }

            for (int i = 0; i < data.size(); i++) {
                SXSSFRow row = sheet.createRow(i);
            }

            try {
                workbook.write(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
