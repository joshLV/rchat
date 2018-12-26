package com.rchat.platform.web.controller.api.view;

public class SummaryView {
    /**
     * id
     */
    private String id;
    /**
     * 名称
     */
    private String name;
    /**
     * 等级
     */
    private Integer level;
    /**
     * 区域
     */
    private String region;
    /**
     * 号段
     */
    private String segment;
    /**
     * 累计额度
     */
    private Long creditAccumulation;
    /**
     * 剩余额度
     */
    private Long creditRemaint;
    /**
     * 消耗额度总数
     */
    private Long creditConsumption;
    /**
     * 对讲账号总数
     */
    private Long userAmount;
    /**
     * 未激活对讲账号总数
     */
    private Long nonactiveUserAmount;
    /**
     * 即将过期对讲账号总数
     */
    private Long epxiringUserAmount;
    /**
     * 过期对讲账号总数
     */
    private Long expiredUserAmount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public Long getCreditAccumulation() {
        return creditAccumulation;
    }

    public void setCreditAccumulation(Long creditAccumulation) {
        this.creditAccumulation = creditAccumulation;
    }

    public Long getCreditConsumption() {
        return creditConsumption;
    }

    public void setCreditConsumption(Long creditConsumption) {
        this.creditConsumption = creditConsumption;
    }

    public Long getCreditRemaint() {
        return creditRemaint;
    }

    public void setCreditRemaint(Long creditRemaint) {
        this.creditRemaint = creditRemaint;
    }

    public Long getUserAmount() {
        return userAmount;
    }

    public void setUserAmount(Long userAmount) {
        this.userAmount = userAmount;
    }

    public Long getNonactiveUserAmount() {
        return nonactiveUserAmount;
    }

    public void setNonactiveUserAmount(Long nonactiveUserAmount) {
        this.nonactiveUserAmount = nonactiveUserAmount;
    }

    public Long getEpxiringUserAmount() {
        return epxiringUserAmount;
    }

    public void setEpxiringUserAmount(Long epxiringUserAmount) {
        this.epxiringUserAmount = epxiringUserAmount;
    }

    public Long getExpiredUserAmount() {
        return expiredUserAmount;
    }

    public void setExpiredUserAmount(Long expiredUserAmount) {
        this.expiredUserAmount = expiredUserAmount;
    }
}

