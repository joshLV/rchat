package com.rchat.platform.domain;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TalkbackGroupRepositoryCustom {
	Page<TalkbackGroup> search(Optional<Group> group, Optional<Department> department, Optional<String> name,
			Pageable pageable);

	Page<Brief> findBriefs(Server server, Pageable pageable);
}
