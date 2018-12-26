package com.rchat.platform.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TalkbackNumberRepositoryCustom {
	Page<Brief> findBriefs(Pageable pageable);
}
