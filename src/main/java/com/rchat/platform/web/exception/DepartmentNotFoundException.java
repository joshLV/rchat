package com.rchat.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rchat.platform.exception.ServiceException;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "rchat.exception.departmentNotFound")
public class DepartmentNotFoundException extends ServiceException {

	private static final long serialVersionUID = 3126376720385955657L;

	public DepartmentNotFoundException() {
		super("部门不存在");
	}

}
