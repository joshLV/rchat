package com.rchat.platform.web.controller;

import com.rchat.platform.domain.Log;
import com.rchat.platform.domain.LogDetail;
import com.rchat.platform.service.LogDetailService;
import com.rchat.platform.service.LogService;
import com.rchat.platform.web.exception.LogNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

public class LogController {
    @Autowired
    private LogService logService;
    @Autowired
    private LogDetailService logDetailService;

    @RequestMapping
    public String index(@PageableDefault Pageable pageable, Model model) {
        Page<Log> logPage = logService.findAll(pageable);

        model.addAttribute("logPage", logPage);

        return "log";
    }

    @RequestMapping("/{id}/details")
    public String details(@PathVariable String id, Model model) {
        Optional<Log> o = logService.findOne(id);
        Log log = o.orElseThrow(LogNotFoundException::new);

        List<LogDetail> details = logDetailService.findByLog(log);

        model.addAttribute("details", details);

        return "logDetail";
    }
}
