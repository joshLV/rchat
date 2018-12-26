package com.rchat.platform.service;

import java.util.List;

import com.rchat.platform.domain.Group;
import com.rchat.platform.domain.GroupSegment;
import com.rchat.platform.domain.TalkbackNumber;
import org.springframework.data.jpa.repository.Query;

public interface TalkbackNumberService extends GenericService<TalkbackNumber, String> {

	List<TalkbackNumber> findByGroup(Group group);

	boolean existsByGroupSegment(GroupSegment segment);

    List<Integer> findNumberValuesByGroup(Group group);
}
