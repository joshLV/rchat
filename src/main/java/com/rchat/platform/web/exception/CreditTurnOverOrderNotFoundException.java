package com.rchat.platform.web.exception;

import com.rchat.platform.exception.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "上缴额度订单不存在")
public class CreditTurnOverOrderNotFoundException extends ServiceException {
    private static final long serialVersionUID = 700260756125890629L;

    public CreditTurnOverOrderNotFoundException() {
        super("上缴额度订单不存在");
    }
}
