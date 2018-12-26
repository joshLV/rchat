package com.rchat.platform.web.controller;

import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.domain.User;
import com.rchat.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("uiUserController")
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserCache userCache;

    @GetMapping("/password.html")
    public String passwortForm() {
        return "password";
    }

    @PostMapping("/password")
    public String resetPassword(String newPassword, String oldPassword) {
        User user = RchatUtils.currentUser();

        boolean valid = RchatUtils.isPasswordValid(oldPassword, user.getPassword(), user.getSalt());
        if (!valid) {
            return "redirect:/users/password.html";
        }
        boolean updated = userService.updatePassword(user.getId(), newPassword);

        // 如果密码更新成功，需要通知缓存，删除已登录的用户信息
        if (updated) {
            userCache.removeUserFromCache(user.getName());
        }
        return "redirect:/logout";
    }
}
