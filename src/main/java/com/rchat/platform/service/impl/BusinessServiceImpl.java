package com.rchat.platform.service.impl;

import com.rchat.platform.aop.OperationType;
import com.rchat.platform.aop.ResourceType;
import com.rchat.platform.aop.SecurityMethod;
import com.rchat.platform.aop.SecurityService;
import com.rchat.platform.domain.Brief;
import com.rchat.platform.domain.Business;
import com.rchat.platform.domain.BusinessRepository;
import com.rchat.platform.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@SecurityService(ResourceType.SERVICE)
public class BusinessServiceImpl extends AbstractService<Business, String> implements BusinessService {
    @Autowired
    private BusinessRepository businessRepository;

    @Override
    protected JpaRepository<Business, String> repository() {
        return businessRepository;
    }

    @Override
    @SecurityMethod(false)
    public Page<Brief> findBriefs(Pageable pageable) {
        return businessRepository.findBriefs(pageable);
    }

    @Override
    public Business findDefaultBussiness() {
        return businessRepository.findByInternal(true);
    }

    @Override
    public Page<Business> findByEnabled(Boolean enabled, Pageable pageable) {
        return businessRepository.findByEnabled(enabled, pageable);
    }

    @Override
    @Transactional
    @SecurityMethod(operation = OperationType.UPDATE)
    public void disable(List<String> ids) {
        businessRepository.setEnabled(Boolean.FALSE, ids);
    }

    @Override
    @Transactional
    @SecurityMethod(operation = OperationType.UPDATE)
    public void enable(List<String> ids) {
        businessRepository.setEnabled(Boolean.TRUE, ids);
    }
}
