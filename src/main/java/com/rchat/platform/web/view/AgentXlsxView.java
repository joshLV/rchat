package com.rchat.platform.web.view;

import com.rchat.platform.domain.Agent;
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

public class AgentXlsxView extends AbstractXlsxStreamingView {
    private static final String[] headers = {"序号", "名称", "等级", "代理商类型", "所属代理商", "区域", "创建时间"};
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String filename;
    private List<Agent> agents;

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setAgents(List<Agent> agents) {
        this.agents = agents;
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
        for (int i = 0; i < agents.size(); i++) {
            Agent agent = agents.get(i);
            Row row = sheet.createRow(i + 1);
            String[] values = {String.valueOf(i + 1), agent.getName(), String.valueOf(agent.getLevel()), agent.getType().toString(), agent.getParent().getName(), agent.getRegion(), dateFormat.format(agent.getCreatedAt())};
            for (int j = 0; j < values.length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(values[j]);
            }
        }
    }
}
