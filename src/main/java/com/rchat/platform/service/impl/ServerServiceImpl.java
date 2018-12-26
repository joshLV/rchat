package com.rchat.platform.service.impl;

import com.rchat.platform.aop.SecurityMethod;
import com.rchat.platform.domain.Group;
import com.rchat.platform.domain.Server;
import com.rchat.platform.domain.ServerRepository;
import com.rchat.platform.service.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServerServiceImpl extends AbstractService<Server, String> implements ServerService {
    @Autowired
    private ServerRepository repository;

    @Override
    protected JpaRepository<Server, String> repository() {
        return repository;
    }

    @Override
    public Optional<Server> findByGroup(Group group) {
        return repository.findByGroup(group);
    }

    @Override
    @SecurityMethod(false)
    public Optional<Server> findByIp(String ipAddress) {
        return repository.findByIpAddress(ipAddress);
    }

    @Override
    @SecurityMethod(false)
    public Optional<Server> findByDomain(String domain) {
        return repository.findByDomain(domain);
    }

    @Override
    @SecurityMethod(false)
    public Optional<Server> findByTalkbackUser(String username, String password) {
        return repository.findByTalkbackUser(username, password);
    }
}
