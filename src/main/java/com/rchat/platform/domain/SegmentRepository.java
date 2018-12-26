package com.rchat.platform.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SegmentRepository extends JpaRepository<Segment, String>, SegmentRepositoryCustom {

	/**
	 * 查找代理商号段，包括保留号段
	 * 
	 * @return
	 */
	@Query(value = "select * from t_segment where (group_id is null and agent_id is null) or agent_id is not null", nativeQuery = true)
	List<? extends Segment> findAgentAvailableSegments();

	@Query(value = "select count(1) from t_segment where group_id is not null and value=?", nativeQuery = true)
	boolean existsByValue(Integer value);

}
