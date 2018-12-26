package com.rchat.platform.jms;

import java.io.Serializable;
import java.util.EventObject;

public class RchatMessage extends EventObject implements Serializable {

	private static final long serialVersionUID = 6899568668974351887L;

	public RchatMessage(Object source) {
		super(source);
	}

}
