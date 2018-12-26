package com.rchat.platform.exception;

/**
 * 不能删除的异常
 */
public class CannotDeleteExeption extends ServiceException {
    public CannotDeleteExeption(String message) {
        super(message);
    }
}
