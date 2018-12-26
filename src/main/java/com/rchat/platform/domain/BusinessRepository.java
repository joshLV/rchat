package com.rchat.platform.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 业务Repository
 *
 * @author dzhang
 * @since 2017-04-04 16:51:55
 */
@Repository
public interface BusinessRepository extends JpaRepository<Business, String>, BusinessRepositoryCustom {
    List<Business> findByName(String name);

    List<Business> findByCode(String code);

    /**
     * 根据是否是默认业务查询，整个系统中只有一个默认业务
     *
     * @param internal 默认业务
     * @return 默认业务
     */
    Business findByInternal(boolean internal);

    Page<Business> findByEnabled(Boolean enabled, Pageable pageable);
}
