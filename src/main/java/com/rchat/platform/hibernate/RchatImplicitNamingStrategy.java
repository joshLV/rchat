package com.rchat.platform.hibernate;

import org.hibernate.boot.model.naming.EntityNaming;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;

/**
 * hibernate生成表时，表名字的生成策略，即为实体名字小写，然后加上 “t_” 前缀，例如 User -> t_user
 * 
 * @author dzhang
 *
 */
public class RchatImplicitNamingStrategy extends SpringImplicitNamingStrategy {

	private static final long serialVersionUID = -4575096101070775926L;

	@Override
	protected String transformEntityName(EntityNaming entityNaming) {
		String tableName = super.transformEntityName(entityNaming);

		return String.format("t_%s", tableName.toLowerCase());
	}
}
