package com.rchat.platform.service;

import com.rchat.platform.domain.Brief;
import com.rchat.platform.domain.Business;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BusinessService extends GenericService<Business, String> {
    Page<Brief> findBriefs(Pageable pageable);

    /**
     * 查询系统默认业务
     *
     * @return 默认业务
     */
    Business findDefaultBussiness();

    Page<Business> findByEnabled(Boolean enabled, Pageable pageable);

    void disable(List<String> strings);

    void enable(List<String> strings);
}
