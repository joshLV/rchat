package com.rchat.platform.service;

import java.util.List;

import com.rchat.platform.domain.Log;
import com.rchat.platform.domain.LogDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LogDetailService extends GenericService<LogDetail, String> {
	List<LogDetail> findByLog(Log log);

    Page<LogDetail> findByLog(Log log, Pageable pageable);
}
