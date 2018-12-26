package com.rchat.platform.domain;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 额度订单状态
 *
 * @author dzhang
 */
public enum CreditOrderStatus {
    COMPLETED {
        @Override
        public String toString() {
            return "完全下发";
        }
    }, PARTIAL {
        @Override
        public String toString() {
            return "部分下发";
        }
    }, UNREVIEWED {
        @Override
        public String toString() {
            return "未审核";
        }
    }, CANCELED {
        @Override
        public String toString() {
            return "已撤销";
        }
    };

    @JsonValue
    public int value() {
        return ordinal();
    }
}
