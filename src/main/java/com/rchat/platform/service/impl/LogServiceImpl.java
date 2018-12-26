package com.rchat.platform.service.impl;

import com.rchat.platform.domain.Log;
import com.rchat.platform.domain.LogReposiotry;
import com.rchat.platform.domain.User;
import com.rchat.platform.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class LogServiceImpl extends AbstractService<Log, String> implements LogService {
    @Autowired
    private LogReposiotry repository;

    @Override
    protected JpaRepository<Log, String> repository() {
        return repository;
    }

    @Override
    public Page<Log> findAll(User user, Pageable pageable) {
        return repository.findByOperator(user, pageable);
    }
}
