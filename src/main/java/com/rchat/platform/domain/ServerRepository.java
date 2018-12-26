package com.rchat.platform.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ServerRepository extends JpaRepository<Server, String> {
    @Query("select s from Server s inner join Group g on g.server.id=s.id where g=?1")
    Optional<Server> findByGroup(Group group);

    Optional<Server> findByIpAddress(String ipAddress);

    Optional<Server> findByDomain(String domain);

    @Query(value = "SELECT s.* FROM t_server s JOIN t_group g ON g.server_id = s.id WHERE g.id IN (SELECT u.group_id FROM t_talkback_user u JOIN t_talkback_number n ON u.talkback_number_id = n.id WHERE n.full_value = :username AND u.`password` = :password) LIMIT 1", nativeQuery = true)
    Optional<Server> findByTalkbackUser(@Param("username") String username, @Param("password") String password);
}
