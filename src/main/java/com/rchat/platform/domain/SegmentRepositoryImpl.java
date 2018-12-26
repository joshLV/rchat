package com.rchat.platform.domain;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

public class SegmentRepositoryImpl implements SegmentRepositoryCustom {
	@Autowired
	private JdbcTemplate jdbc;

	@Override
	public boolean updateAgentSegment(AgentSegment segment) {
		Object[] args = { segment.getAgent().getId(), new Date(), segment.getId() };
		String sql = "update t_segment set disc = 'agent', agent_id = ?, group_id = null, updated_at = ? where id = ?";
		int updated = jdbc.update(sql, args);

		return updated > 0;
	}

	@Override
	public boolean updateGroupSegment(GroupSegment segment) {
		Object[] args = { segment.getGroup().getId(), new Date(), segment.getId() };
		String sql = "update t_segment set disc = 'group', agent_id = null, group_id = ?, updated_at = ? where id = ?";
		int updated = jdbc.update(sql, args);

		return updated > 0;
	}

	@Override
	public boolean recycle(Segment segment) {
		Object[] args = { new Date(), segment.getId() };
		String sql = "update t_segment set agent_id = null, group_id = null, updated_at = ? where id = ?";
		int updated = jdbc.update(sql, args);

		return updated > 0;
	}

	@Override
	public boolean existsAgentSegment(int value) {
		String sql = "select id from t_segment where `disc`='agent' and `value`=? limit 0,1";
		ResultSetExtractor<Boolean> extractor = r -> r.next();
		return jdbc.query(sql, new Object[] { value }, extractor);
	}

	@Override
	public boolean existsGroupSegment(int value) {
		String sql = "select id from t_segment where `disc`='group' and `value`=? limit 0,1";
		ResultSetExtractor<Boolean> extractor = r -> r.next();
		return jdbc.query(sql, new Object[] { value }, extractor);
	}

}
