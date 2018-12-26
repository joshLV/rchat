package com.rchat.platform.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ServerStatus {
    /**
     * 在线
     */
    ONLINE{
        @Override
        public String toString() {
            return "在线";
        }
    },
    /**
     * 离线
     */
    OFFLINE{
        @Override
        public String toString() {
            return "离线";
        }
    },
    /**
     * 异常
     */
    ABNORMALITY{
        @Override
        public String toString() {
            return "异常";
        }
    };

    @JsonValue
    public int value() {
        return ordinal();
    }
}
