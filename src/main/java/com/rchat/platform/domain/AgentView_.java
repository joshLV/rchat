package com.rchat.platform.domain;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(AgentView.class)
public class AgentView_ {
	public static volatile SingularAttribute<AgentView, AgentViewId> viewId;
	public static volatile SingularAttribute<AgentView, String> name;
	public static volatile SingularAttribute<AgentView, User> administrator;
}
