package com.rchat.platform.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, String>, GroupRepositoryCustom {
    Page<Group> findByAgent(Agent agent, Pageable pageable);

    boolean existsByAgent(Agent agent);

    Optional<Group> findByAdministrator(User user);

    List<Group> findByAgent(Agent agent);

    Optional<Group> findByIdAndAgent(String id, Agent agent);

    @Modifying
    @Query("update Group g set g.server = ?2 where g.id in (?1)")
    void setServer(List<String> groupIds, Server server);

    boolean existsByIdAndServerNotNull(String id);
}
