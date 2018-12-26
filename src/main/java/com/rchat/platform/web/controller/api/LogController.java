package com.rchat.platform.web.controller.api;

import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.domain.Log;
import com.rchat.platform.domain.LogDetail;
import com.rchat.platform.service.LogDetailService;
import com.rchat.platform.service.LogService;
import com.rchat.platform.web.exception.LogNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
public class LogController {
    @Autowired
    private LogService logService;
    @Autowired
    private LogDetailService logDetailService;

    @GetMapping
    public Page<Log> page(@PageableDefault Pageable pageable) {
        if (RchatUtils.isAgentAdmin()) {
            return logService.findAll(pageable);
        } else {
            return logService.findAll(RchatUtils.currentUser(), pageable);
        }
    }

    @GetMapping("/{id}/details")
    public Page<LogDetail> logDetails(@PathVariable String id, @PageableDefault Pageable pageable) {
        assertLogExists(id);

        return logDetailService.findByLog(new Log(id), pageable);
    }

    private void assertLogExists(String id) {
        boolean exists = logService.exists(id);
        if (!exists)
            throw new LogNotFoundException();
    }
}
