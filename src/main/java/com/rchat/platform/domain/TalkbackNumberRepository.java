package com.rchat.platform.domain;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TalkbackNumberRepository extends JpaRepository<TalkbackNumber, String>, TalkbackNumberRepositoryCustom {
    List<TalkbackNumber> findByGroupSegment_Group(Group group, Sort sort);

    boolean existsByGroupSegment(GroupSegment segment);

    @Query("select value from TalkbackNumber n where n.groupSegment.group=?1")
    List<Integer> findNumberValuesByGroup(Group group);
}
