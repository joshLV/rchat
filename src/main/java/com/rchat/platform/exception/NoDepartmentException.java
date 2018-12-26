package com.rchat.platform.exception;
/**
 * 集团不存在部门异常
 * @author wukeyang
 *
 */
public class NoDepartmentException extends ServiceException {

    private static final long serialVersionUID = -3349499161704074193L;

    public NoDepartmentException() {
        super("该集团不存在部门");
    }
}
