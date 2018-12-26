package com.rchat.platform.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BusinessRepositoryCustom {
    Page<Brief> findBriefs(Pageable pageable);

    void setEnabled(Boolean aFalse, List<String> ids);
}
