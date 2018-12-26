package com.rchat.platform.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.rchat.platform.domain.Log;
import com.rchat.platform.domain.LogDetail;
import com.rchat.platform.domain.LogDetailRepository;
import com.rchat.platform.service.LogDetailService;

@Service
public class LogDetailServiceImpl extends AbstractService<LogDetail, String> implements LogDetailService {
	@Autowired
	private LogDetailRepository repository;

	@Override
	public List<LogDetail> findByLog(Log log) {
		List<LogDetail> details = repository.findByLog(log);

		return details.parallelStream().sorted().collect(Collectors.toList());
	}

	@Override
	public Page<LogDetail> findByLog(Log log, Pageable pageable) {
		return repository.findByLog(log, pageable);
	}

	@Override
	protected JpaRepository<LogDetail, String> repository() {
		return repository;
	}

}
