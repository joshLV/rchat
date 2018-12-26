package com.rchat.platform.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.rchat.platform.domain.TalkbackUserGroup.TalkbackUserGroupId;

@Repository
public interface TalkbackUserGroupRepository extends JpaRepository<TalkbackUserGroup, TalkbackUserGroupId> {

	@Query("select ug from TalkbackUserGroup ug where ug.id.groupId = ?1")
	List<TalkbackUserGroup> findByGroupId(String groupId);

}
