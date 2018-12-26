package com.rchat.platform.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceGroupRepository extends JpaRepository<ResourceGroup, String> {

}
