package com.rchat.platform.domain;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(TalkbackNumber.class)
public class TalkbackNumber_ extends TimestampedResource_ {
	public static volatile SingularAttribute<TalkbackNumber, String> id;
	public static volatile SingularAttribute<TalkbackNumber, Integer> value;
	public static volatile SingularAttribute<TalkbackNumber, String> fullValue;
	public static volatile SingularAttribute<TalkbackNumber, String> shortValue;
}
