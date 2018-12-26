package com.rchat.platform.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupFileRepository extends JpaRepository<GroupFile, String>{

	List<GroupFile> findByFileId(String fileId);
	
	List<GroupFile> findByGroupAndType(Group group, FileType Type);
}
