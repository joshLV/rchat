package com.rchat.platform.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, String> {
	List<Authority> findByRole(Role role);

	List<Authority> findByRoleIn(List<Role> roles);
}
