package com.rchat.platform.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogReposiotry extends JpaRepository<Log, String> {

    /**
     * 查询指定操作员的相关日志信息
     *
     * @param user     操作员
     * @param pageable 分页信息
     * @return 分页日志
     */
    Page<Log> findByOperator(User user, Pageable pageable);
}
