package com.rchat.platform.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "t_talkback_user_group")
public class TalkbackUserGroup implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	private TalkbackUserGroupId id;
	@Transient
	private TalkbackUser user;
	@Transient
	private TalkbackGroup group;

	public TalkbackUserGroup() {
	}

	public TalkbackUserGroup(TalkbackUser user, TalkbackGroup group) {
		this(new TalkbackUserGroupId(user, group));
	}

	public TalkbackUserGroup(TalkbackUserGroupId id) {
		super();
		this.id = id;
	}

	public TalkbackUserGroupId getId() {
		return id;
	}

	public void setId(TalkbackUserGroupId id) {
		this.id = id;
	}

	public TalkbackUser getUser() {
		return user;
	}

	public void setUser(TalkbackUser user) {
		this.user = user;
	}

	public TalkbackGroup getGroup() {
		return group;
	}

	public void setGroup(TalkbackGroup group) {
		this.group = group;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof TalkbackUserGroup))
			return false;
		TalkbackUserGroup other = (TalkbackUserGroup) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Embeddable
	public static class TalkbackUserGroupId implements Serializable {
		private static final long serialVersionUID = -6130329840503361097L;
		@Column(name = "talkback_user_id", length = 36, nullable = false)
		private String userId;
		@Column(name = "talkback_group_id", length = 36, nullable = false)
		private String groupId;

		public TalkbackUserGroupId() {
		}

		public TalkbackUserGroupId(TalkbackUser user, TalkbackGroup group) {
			this(user.getId(), group.getId());
		}

		public TalkbackUserGroupId(String userId, String groupId) {
			super();
			this.userId = userId;
			this.groupId = groupId;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getGroupId() {
			return groupId;
		}

		public void setGroupId(String groupId) {
			this.groupId = groupId;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
			result = prime * result + ((userId == null) ? 0 : userId.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof TalkbackUserGroupId))
				return false;
			TalkbackUserGroupId other = (TalkbackUserGroupId) obj;
			if (groupId == null) {
				if (other.groupId != null)
					return false;
			} else if (!groupId.equals(other.groupId))
				return false;
			if (userId == null) {
				if (other.userId != null)
					return false;
			} else if (!userId.equals(other.userId))
				return false;
			return true;
		}

	}
}
