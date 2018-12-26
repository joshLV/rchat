package com.rchat.platform.service.impl;

import java.util.List;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rchat.platform.aop.ResourceType;
import com.rchat.platform.aop.SecurityMethod;
import com.rchat.platform.aop.SecurityService;
import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.domain.Agent;
import com.rchat.platform.domain.FileType;
import com.rchat.platform.domain.Group;
import com.rchat.platform.domain.GroupFile;
import com.rchat.platform.domain.GroupFileRepository;
import com.rchat.platform.service.GroupFileService;
import com.rchat.platform.service.UserService;

@Service
@Transactional(readOnly = true)
@SecurityService(ResourceType.GROUP)
public class GroupFileServiceImpl extends AbstractService<GroupFile, String> implements GroupFileService {
    private static final Log log = LogFactory.getLog(GroupFileServiceImpl.class);

    @Autowired
    private JpaRepository<GroupFile, String> repository;
    
    @Autowired
    private GroupFileRepository groupFileRepository;
    
    @Autowired
    private JmsTemplate jms;

    @Override
    @Transactional
	@SecurityMethod(false)
    public GroupFile update(GroupFile entity) {
        return super.update(entity);
    }
    
    @Override
    @SecurityMethod(false)
    public Optional<GroupFile> findOne(String id) {
        Optional<GroupFile> o = super.findOne(id);
        return o;
    }

    @Override
    @Transactional
    @SecurityMethod(false)
    public GroupFile create(GroupFile entity) {        
        return super.create(entity);
    }

    @Override
    @SecurityMethod(false)
    public boolean delete(GroupFile entity) {       	
    	return super.delete(entity);
    }

    @Override
    protected JpaRepository<GroupFile, String> repository() {
        return repository;
    }
    
    @Override
    @SecurityMethod(false)
    public List<GroupFile> findByFileId(String fileId) {
        return groupFileRepository.findByFileId(fileId);
    }
    
    @Override
    @SecurityMethod(false)
    public List<GroupFile> findByGroupAndType(Group group, FileType type) {
        return groupFileRepository.findByGroupAndType(group, type);
    }
}
