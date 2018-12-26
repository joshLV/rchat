package com.rchat.platform.web.controller;

import javax.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.rchat.platform.exception.AuthenticationNotFoundException;
import com.rchat.platform.exception.NoRightAccessException;

@ControllerAdvice
public class GlobalControllerExceptionHandler extends ResponseEntityExceptionHandler {
	@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "数据集是空的")
	@ExceptionHandler(EmptyResultDataAccessException.class)
	public void handleNotFound() {
		// Nothing to do
	}

	@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "rchat.exception.invalidInput")
	@ExceptionHandler(IllegalArgumentException.class)
	public void handleIllegalArgumentException() {

	}

	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "rchat.exception.objectRetrievalFailure")
	@ExceptionHandler(ObjectRetrievalFailureException.class)
	public void handleObjectRetrievalFailure() {

	}

	@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "rchat.exception.constraintViolation")
	@ExceptionHandler(ConstraintViolationException.class)
	public void handlConstraintViolation() {
	}

	@ResponseStatus(code = HttpStatus.CONFLICT, reason = "rchat.exception.dataIntegrityViolation")
	@ExceptionHandler(DataIntegrityViolationException.class)
	public void handleConflict() {
		// Nothing to do
	}

	@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "无权限操作")
	@ExceptionHandler(NoRightAccessException.class)
	public void handleNoRight() {

	}

	@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "权限实体不存在，确保已经登录系统")
	@ExceptionHandler(AuthenticationNotFoundException.class)
	public void handleAuthenticationNotFound() {

	}
}
