package com.rchat.platform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 资源找不到错误
 *
 * @author dzhang
 * @since 2017-02-27 20:21:20
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "资源找不到")
public class ResourceNotFoundException extends ServiceException {

	private static final long serialVersionUID = 1L;

}
