package com.rchat.platform.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface GroupRepositoryCustom extends GroupSummaryRepository {
    Page<Group> search(Optional<String> agentName, Optional<String> groupName, Optional<String> adminName,
                       Optional<String> adminUsername, Optional<Boolean> status, Pageable pageable);

    Page<Brief> findBriefs(Server server, Pageable pageable);
}
