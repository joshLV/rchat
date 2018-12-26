package com.rchat.platform.domain;

import java.io.Serializable;

/**
 * 综述统计类
 */
public class Summary implements Serializable {
    private static final long serialVersionUID = -3769168589180523743L;
    /**
     * 账号总数
     */
    private long userAmount;
    /**
     * 已过期账号总数
     */
    private long expiredUserAmount;
    /**
     * 当日过期的总数
     */
    private long newExpiredUserAmmount;
    /**
     * 新开账号数量
     */
    private long newUserAmount;
    /**
     * 未激活账号总数
     */
    private long nonactiveUserAmount;
    /**
     * 当日激活账号总数
     */
    private long newActivatedUserAmmout;
    /**
     * 快过期账号总数
     */
    private long expiringUserAmount;
    /**
     * 集团总数
     */
    private long groupAmount;
    /**
     * 一级代理商总数
     */
    private long firstAgentAmount;
    /**
     * 二级代理商总数
     */
    private long secondAgentAmount;
    /**
     * 三级代理商总数
     */
    private long thirdAgentAmount;
    /**
     * 四级代理商总数
     */
    private long forthAgentAmount;
    /**
     * 五级代理商总数
     */
    private long fifthAgentAmount;
    /**
     * 新增代理商总数
     */
    private long newAgentAmount;

    /**
     * 当日额度消费总量
     */
    private long creditConsumption;
    /**
     * 新增集团数量
     */
    private long newGroupAmount;
    /**
     * 累计额度
     */
    private long creditAccumulation;
    private long creditRemaint;


    public long getUserAmount() {
        return userAmount;
    }

    public void setUserAmount(long userAmount) {
        this.userAmount = userAmount;
    }

    public long getExpiredUserAmount() {
        return expiredUserAmount;
    }

    public void setExpiredUserAmount(long expiredUserAmount) {
        this.expiredUserAmount = expiredUserAmount;
    }

    public long getNewExpiredUserAmmount() {
        return newExpiredUserAmmount;
    }

    public void setNewExpiredUserAmmount(long newExpiredUserAmmount) {
        this.newExpiredUserAmmount = newExpiredUserAmmount;
    }

    public long getNonactiveUserAmount() {
        return nonactiveUserAmount;
    }

    public void setNonactiveUserAmount(long nonactiveUserAmount) {
        this.nonactiveUserAmount = nonactiveUserAmount;
    }

    public long getExpiringUserAmount() {
        return expiringUserAmount;
    }

    public void setExpiringUserAmount(long expiringUserAmount) {
        this.expiringUserAmount = expiringUserAmount;
    }

    public long getGroupAmount() {
        return groupAmount;
    }

    public void setGroupAmount(long groupAmount) {
        this.groupAmount = groupAmount;
    }

    public long getFirstAgentAmount() {
        return firstAgentAmount;
    }

    public void setFirstAgentAmount(long firstAgentAmount) {
        this.firstAgentAmount = firstAgentAmount;
    }

    public long getSecondAgentAmount() {
        return secondAgentAmount;
    }

    public void setSecondAgentAmount(long secondAgentAmount) {
        this.secondAgentAmount = secondAgentAmount;
    }

    public long getThirdAgentAmount() {
        return thirdAgentAmount;
    }

    public void setThirdAgentAmount(long thirdAgentAmount) {
        this.thirdAgentAmount = thirdAgentAmount;
    }

    public long getForthAgentAmount() {
        return forthAgentAmount;
    }

    public void setForthAgentAmount(long forthAgentAmount) {
        this.forthAgentAmount = forthAgentAmount;
    }

    public long getFifthAgentAmount() {
        return fifthAgentAmount;
    }

    public void setFifthAgentAmount(long fifthAgentAmount) {
        this.fifthAgentAmount = fifthAgentAmount;
    }

    public long getNewAgentAmount() {
        return newAgentAmount;
    }

    public void setNewAgentAmount(long newAgentAmount) {
        this.newAgentAmount = newAgentAmount;
    }

    public long getNewUserAmount() {
        return newUserAmount;
    }

    public void setNewUserAmount(long newUserAmount) {
        this.newUserAmount = newUserAmount;
    }

    public long getNewActivatedUserAmmout() {
        return newActivatedUserAmmout;
    }

    public void setNewActivatedUserAmmout(long newActivatedUserAmmout) {
        this.newActivatedUserAmmout = newActivatedUserAmmout;
    }

    public long getCreditConsumption() {
        return creditConsumption;
    }

    public void setCreditConsumption(long creditConsumption) {
        this.creditConsumption = creditConsumption;
    }

    public long getCreditAccumulation() {
        return creditAccumulation;
    }

    public void setCreditAccumulation(long creditAccumulation) {
        this.creditAccumulation = creditAccumulation;
    }

    public void setNewGroupAmount(long newGroupAmount) {
        this.newGroupAmount = newGroupAmount;
    }

    public long getNewGroupAmount() {
        return newGroupAmount;
    }

    public void setCreditRemaint(long creditRemaint) {
        this.creditRemaint = creditRemaint;
    }

    public long getCreditRemaint() {
        return creditRemaint;
    }
}
