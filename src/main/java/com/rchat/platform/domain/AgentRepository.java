package com.rchat.platform.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentRepository extends JpaRepository<Agent, String>, AgentRepositoryCustom {
	List<Agent> findByParent(Agent parent);

	List<Agent> findByParentIsNull();

	boolean existsByParent(Agent agent);

	long countByParent(Agent agent);

	Optional<Agent> findByAdministrator(User user);

	Page<Agent> findByParent(Agent parent, Pageable pageable);

	Page<Agent> findById(String id, Pageable pageable);

	Page<Agent> findByAdministratorNameLike(String name, Pageable pageable);

	Page<Agent> findByAdministratorUsernameLike(String username, Pageable pageable);

	Page<Agent> findByAdministratorUsernameLikeAndAdministratorNameLike(String username, String name,
			Pageable pageable);

    List<Agent> findByLevel(Integer level);
}
