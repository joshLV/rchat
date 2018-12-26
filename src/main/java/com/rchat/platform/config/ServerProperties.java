package com.rchat.platform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 语音服务器配置信息
 */
@ConfigurationProperties("rchat.server")
public class ServerProperties {
    /**
     * 容量上线占用率，如果超过这个限制，则不能分配服务器
     */
    private double thresholdRate;

    public double getThresholdRate() {
        return thresholdRate;
    }

    public void setThresholdRate(double thresholdRate) {
        this.thresholdRate = thresholdRate;
    }
}
