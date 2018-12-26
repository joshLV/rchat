package com.rchat.platform.web.exception;

import com.rchat.platform.exception.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "rchat.exception.serverNotFound")
public class ServerNotFoundException extends ServiceException {
}
