package com.rchat.platform.domain;

/**
 * 额度上缴状态，所有额度不能直接上缴，必须经过上级代理商的确认才能确认完成上缴过程，得到上级代理确认之后，上缴额度生效
 */
public enum FileType {
    File {
        @Override
        public String toString() {
            return "文件";
        }
    }, Voice {
        @Override
        public String toString() {
            return "语音";
        }
    }, Intercom {
        @Override
        public String toString() {
            return "对讲";
        }
    }, Vedio {
        @Override
        public String toString() {
            return "视频";
        }
    }
}
