package com.rchat.platform.web.view;

import com.rchat.platform.domain.TalkbackUser;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class TalkbackUserXlsxView extends AbstractXlsxStreamingView {
    private static final String[] headers = {"序号","账号ID", "账号","短号码", "中文名", "代理商", "集团", "部门", "到期时间"};
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private List<TalkbackUser> users;
    private String filename;

    public void setUsers(List<TalkbackUser> users) {
        this.users = users;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("content-disposition", String.format("attachment;filename=%s.xlsx", URLEncoder.encode(filename, "UTF-8")));

        Sheet sheet = workbook.createSheet("对讲账号");

        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
        }
        for (int i = 0; i < users.size(); i++) {
            TalkbackUser user = users.get(i);
            Row row = sheet.createRow(i + 1);
            String dName = "";
            if(null != user && null != user.getDepartment() && null != user.getDepartment().getName()){           	
            	dName = user.getDepartment().getName();
            }
            String[] values = {String.valueOf(i + 1),user.getId(), user.getNumber().getFullValue(), user.getNumber().getShortValue(), user.getName(), user.getGroup().getAgent().getName(), user.getGroup().getName(), dName, dateFormat.format(user.getDeadline())};
            for (int j = 0; j < values.length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(values[j]);
            }
        }
    }
}
