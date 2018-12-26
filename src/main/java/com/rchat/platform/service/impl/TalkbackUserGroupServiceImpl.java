package com.rchat.platform.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.rchat.platform.domain.TalkbackUserGroup;
import com.rchat.platform.domain.TalkbackUserGroup.TalkbackUserGroupId;
import com.rchat.platform.domain.TalkbackUserGroupRepository;
import com.rchat.platform.service.TalkbackUserGroupService;

@Service
public class TalkbackUserGroupServiceImpl extends AbstractService<TalkbackUserGroup, TalkbackUserGroupId>
		implements TalkbackUserGroupService {

	@Autowired
	private TalkbackUserGroupRepository repository;

	@Override
	protected JpaRepository<TalkbackUserGroup, TalkbackUserGroupId> repository() {
		return repository;
	}

}