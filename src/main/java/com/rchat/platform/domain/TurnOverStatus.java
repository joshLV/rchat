package com.rchat.platform.domain;

/**
 * 额度上缴状态，所有额度不能直接上缴，必须经过上级代理商的确认才能确认完成上缴过程，得到上级代理确认之后，上缴额度生效
 */
public enum TurnOverStatus {
    NO_ACK {
        @Override
        public String toString() {
            return "未确认审批";
        }
    }, ACK {
        @Override
        public String toString() {
            return "已确认上缴";
        }
    }
}
