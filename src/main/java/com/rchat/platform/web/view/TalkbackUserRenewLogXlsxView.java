package com.rchat.platform.web.view;

import com.rchat.platform.domain.TalkbackUserRenewLog;
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

public class TalkbackUserRenewLogXlsxView extends AbstractXlsxStreamingView {
    private static final String[] headers = {"序号", "账号", "充值时间", "所属代理商", "所属集团", "账号有效期","基础业务费用"};
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private List<TalkbackUserRenewLog> renewLogs;
    private String filename;

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("content-disposition", String.format("attachment;filename=%s.xlsx", URLEncoder.encode(filename, "UTF-8")));

        Sheet sheet = workbook.createSheet("对讲账号续费日志");

        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
        }
        for (int i = 0; i < renewLogs.size(); i++) {
            TalkbackUserRenewLog log = renewLogs.get(i);
            Row row = sheet.createRow(i + 1);
            String[] values = {String.valueOf(i + 1), log.getNumber(), dateFormat.format(log.getUpdatedAt()), log.getAgent().getName(), log.getGroup().getName(), dateFormat.format(log.getDeadline()), log.getBasicCredit()+""};
            for (int j = 0; j < values.length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(values[j]);
            }
        }
    }

    public void setRenewLogs(List<TalkbackUserRenewLog> renewLogs) {
        this.renewLogs = renewLogs;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
