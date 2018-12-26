package com.rchat.platform.web.view;

import com.rchat.platform.domain.CreditAccountRenewLog;
import com.rchat.platform.domain.RenewLog;
import com.rchat.platform.domain.TalkbackUserRenewLog;
import org.apache.commons.lang3.ArrayUtils;
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

/**
 * 充值或者续费日志
 */
public class CreditXlsxView extends AbstractXlsxStreamingView {
    private static final String[] headers = {"序号", "出账账号", "入账账号", "充值金额", "充值时间"};
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private List<? extends RenewLog> renewLogs;
    private String filename;

    public List<? extends RenewLog> getRenewLogs() {
        return renewLogs;
    }

    public void setRenewLogs(List<? extends RenewLog> renewLogs) {
        this.renewLogs = renewLogs;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("content-disposition", String.format("attachment;filename=%s.xlsx", URLEncoder.encode(filename, "UTF-8")));

        Sheet sheet = workbook.createSheet("额度记录");

        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
        }
        for (int i = 0; i < renewLogs.size(); i++) {
            RenewLog log = renewLogs.get(i);
            Row row = sheet.createRow(i + 1);
            String[] values;
            if (log instanceof CreditAccountRenewLog) {
                CreditAccountRenewLog _log = (CreditAccountRenewLog) log;
                values = new String[]{String.valueOf(i + 1), _log.getCreditAccount().getName(), _log.getToCreditAccount().getName(), String.valueOf(_log.getCredit()), dateFormat.format(_log.getCreatedAt())};
            } else if (log instanceof TalkbackUserRenewLog) {
                TalkbackUserRenewLog _log = (TalkbackUserRenewLog) log;
                values = new String[]{String.valueOf(i + 1), _log.getCreditAccount().getName(), _log.getNumber(), String.valueOf(_log.getCredit()), dateFormat.format(_log.getCreatedAt())};
            } else {
                values = ArrayUtils.EMPTY_STRING_ARRAY;
            }

            for (int j = 0; j < values.length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(values[j]);
            }
        }
    }
}
