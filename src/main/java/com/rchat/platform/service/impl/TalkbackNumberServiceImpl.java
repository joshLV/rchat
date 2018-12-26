package com.rchat.platform.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.rchat.platform.domain.Group;
import com.rchat.platform.domain.GroupSegment;
import com.rchat.platform.domain.TalkbackNumber;
import com.rchat.platform.domain.TalkbackNumberRepository;
import com.rchat.platform.service.TalkbackNumberService;

@Service
public class TalkbackNumberServiceImpl extends AbstractService<TalkbackNumber, String>
		implements TalkbackNumberService {
	@Autowired
	private TalkbackNumberRepository repository;

	@Override
	protected JpaRepository<TalkbackNumber, String> repository() {
		return repository;
	}

	@Override
	public List<TalkbackNumber> create(List<TalkbackNumber> entities) {
		entities.parallelStream().forEach(n -> n.getFullValue());

		return super.create(entities);
	}

	@Override
	public TalkbackNumber create(TalkbackNumber entity) {
		entity.getFullValue();
		return super.create(entity);
	}

	@Override
	public List<TalkbackNumber> findByGroup(Group group) {
		return repository.findByGroupSegment_Group(group, new Sort("value"));
	}

	@Override
	public boolean existsByGroupSegment(GroupSegment segment) {
		return repository.existsByGroupSegment(segment);
	}

	@Override
	public List<Integer> findNumberValuesByGroup(Group group) {
		return repository.findNumberValuesByGroup(group);
	}

}
