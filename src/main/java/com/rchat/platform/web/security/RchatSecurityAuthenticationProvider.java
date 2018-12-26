package com.rchat.platform.web.security;

import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.domain.User;
import com.rchat.platform.service.UserService;
import com.rchat.platform.web.exception.PasswordInvalidException;
import com.rchat.platform.web.exception.UsernameNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class RchatSecurityAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    private static final Log log = LogFactory.getLog(RchatSecurityAuthenticationProvider.class);

    private final UserService userService;

    public RchatSecurityAuthenticationProvider(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        SecurityUser principal = SecurityUser.class.cast(userDetails);
        User user = principal.getUser();

        if (!RchatUtils.isPasswordValid(authentication.getCredentials().toString(), user.getPassword(),
                user.getSalt())) {
            throw new PasswordInvalidException();
        }

        userService.login(user);
        log.info(String.format("%s 登录系统！！！", user.getName()));
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        Optional<User> o = userService.findByUsername(username);

        User user = o.orElseThrow(UsernameNotFoundException::new);

        // 如果已经登录
        // if (user.isOnline()) {
        // throw new UserLogonException();
        // }

        return new SecurityUser(user);
    }
}
