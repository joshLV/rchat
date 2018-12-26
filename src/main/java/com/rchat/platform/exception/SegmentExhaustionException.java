package com.rchat.platform.exception;

/**
 * 号段耗尽异常
 *
 * @author dzhang
 */
public class SegmentExhaustionException extends ServiceException {

    private static final long serialVersionUID = -3349499161704074193L;

    public SegmentExhaustionException() {
        super("号段资源耗尽");
    }
}
