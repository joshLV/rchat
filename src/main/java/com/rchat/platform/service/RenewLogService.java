package com.rchat.platform.service;

import com.rchat.platform.domain.Agent;
import com.rchat.platform.domain.Group;
import com.rchat.platform.domain.RenewLog;
import com.rchat.platform.domain.TalkbackUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface RenewLogService extends GenericService<RenewLog, String> {

    List<RenewLog> findYearConsumption(Agent agent);

    List<RenewLog> findSeasonConsumption(Agent agent);

    List<RenewLog> findMonthConsumption(Agent agent);

    List<RenewLog> findConsumption(Agent agent);

    List<RenewLog> findYearConsumption(Group group);

    List<RenewLog> findSeasonConsumption(Group group);

    List<RenewLog> findMonthConsumption(Group group);

    List<RenewLog> findConsumption(Group group);

    List<? extends RenewLog> findYearAccumulation(Agent agent);

    List<? extends RenewLog> findSeasonAccumulation(Agent agent);

    List<? extends RenewLog> findMonthAccumulation(Agent agent);

    List<? extends RenewLog> findAccumulation(Agent agent);

    List<? extends RenewLog> findYearAccumulation(Group group);

    List<? extends RenewLog> findSeasonAccumulation(Group group);

    List<? extends RenewLog> findMonthAccumulation(Group group);

    List<? extends RenewLog> findAccumulation(Group group);

    List<? extends RenewLog> findAccumulation();

    List<? extends RenewLog> findYearAccumulation();

    List<? extends RenewLog> findSeasonAccumulation();

    List<? extends RenewLog> findMonthAccumulation();

    List<? extends RenewLog> findConsumption();

    List<? extends RenewLog> findYearConsumption();

    List<? extends RenewLog> findSeasonConsumption();

    List<? extends RenewLog> findMonthConsumption();
}
