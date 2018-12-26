package com.rchat.platform.domain;

public interface AgentSummaryRepository {
    long countTotalAmount(Agent parent, int level);

    long countNewAmount(Agent parent);
}
