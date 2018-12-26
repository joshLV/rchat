package com.rchat.platform.domain;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogDetailRepository extends JpaRepository<LogDetail, String> {
	List<LogDetail> findByLog(Log log);

    Page<LogDetail> findByLog(Log log, Pageable pageable);
}
