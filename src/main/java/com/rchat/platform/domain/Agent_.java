package com.rchat.platform.domain;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Agent.class)
public class Agent_ extends TimestampedResource_ {
    public static volatile SingularAttribute<Agent, String> id;
    public static volatile SingularAttribute<Agent, String> name;
    public static volatile SingularAttribute<Agent, User> administrator;
    public static volatile SingularAttribute<Agent, Agent> parent;
    public static volatile SingularAttribute<Agent, Integer> level;
}
