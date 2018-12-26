package com.rchat.platform.domain;

import java.io.Serializable;

public class CreditSummary implements Serializable {
    private static final long serialVersionUID = 5020666824658867369L;
    /**
     * 剩余额度
     */
    private Long creditRemaint;
    /**
     * 累计额度
     */
    private Long creditAccumulation;
    /**
     * 今天购买的额度
     */
    private Long creditTodayAccumulation;
    /**
     * 本月购买额度
     */
    private Long creditMonthAccumulation;
    /**
     * 本季购买额度
     */
    private Long creditSeasonAccumulation;
    /**
     * 本年购买额度
     */
    private Long creditYearAccumulation;
    /**
     * 消耗额度
     */
    private Long creditConsumption;
    /**
     * 今日消耗额度
     */
    private Long creditTodayConsumption;
    /**
     * 本月消耗额度
     */
    private Long creditMonthConsumption;
    /**
     * 本季度消耗额度
     */
    private Long creditSeasonConsumption;
    /**
     * 本年消耗额度
     */
    private Long creditYearConsumption;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getCreditRemaint() {
        return creditRemaint;
    }

    public void setCreditRemaint(Long creditRemaint) {
        this.creditRemaint = creditRemaint;
    }

    public Long getCreditAccumulation() {
        return creditAccumulation;
    }

    public void setCreditAccumulation(Long creditAccumulation) {
        this.creditAccumulation = creditAccumulation;
    }

    public Long getCreditTodayAccumulation() {
        return creditTodayAccumulation;
    }

    public void setCreditTodayAccumulation(Long creditTodayAccumulation) {
        this.creditTodayAccumulation = creditTodayAccumulation;
    }

    public Long getCreditMonthAccumulation() {
        return creditMonthAccumulation;
    }

    public void setCreditMonthAccumulation(Long creditMonthAccumulation) {
        this.creditMonthAccumulation = creditMonthAccumulation;
    }

    public Long getCreditSeasonAccumulation() {
        return creditSeasonAccumulation;
    }

    public void setCreditSeasonAccumulation(Long creditSeasonAccumulation) {
        this.creditSeasonAccumulation = creditSeasonAccumulation;
    }

    public Long getCreditYearAccumulation() {
        return creditYearAccumulation;
    }

    public void setCreditYearAccumulation(Long creditYearAccumulation) {
        this.creditYearAccumulation = creditYearAccumulation;
    }

    public Long getCreditConsumption() {
        return creditConsumption;
    }

    public void setCreditConsumption(Long creditConsumption) {
        this.creditConsumption = creditConsumption;
    }

    public Long getCreditTodayConsumption() {
        return creditTodayConsumption;
    }

    public void setCreditTodayConsumption(Long creditTodayConsumption) {
        this.creditTodayConsumption = creditTodayConsumption;
    }

    public Long getCreditMonthConsumption() {
        return creditMonthConsumption;
    }

    public void setCreditMonthConsumption(Long creditMonthConsumption) {
        this.creditMonthConsumption = creditMonthConsumption;
    }

    public Long getCreditSeasonConsumption() {
        return creditSeasonConsumption;
    }

    public void setCreditSeasonConsumption(Long creditSeasonConsumption) {
        this.creditSeasonConsumption = creditSeasonConsumption;
    }

    public Long getCreditYearConsumption() {
        return creditYearConsumption;
    }

    public void setCreditYearConsumption(Long creditYearConsumption) {
        this.creditYearConsumption = creditYearConsumption;
    }
}
