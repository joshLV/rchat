package com.rchat.platform.domain;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 代理商类型
 *
 * @author dzhang
 */
public enum AgentType {
    /**
     * 普通代理商
     */
    NORMAL {
        @Override
        public String toString() {
            return "普通代理商";
        }
    },
    /**
     * 终端代理商
     */
    TERMINAL {
        @Override
        public String toString() {
            return "终端代理商";
        }
    },
    /**
     * 融洽平台
     */
    RCHAT {
        @Override
        public String toString() {
            return "平台代理商";
        }
    };

    @JsonValue
    public int value() {
        return ordinal();
    }
}
