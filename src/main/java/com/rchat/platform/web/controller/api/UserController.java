package com.rchat.platform.web.controller.api;

import com.rchat.platform.common.LogAPI;
import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.domain.Role;
import com.rchat.platform.domain.User;
import com.rchat.platform.exception.PasswordNotValidException;
import com.rchat.platform.service.UserService;
import com.rchat.platform.web.controller.Status;
import com.rchat.platform.web.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserCache userCache;

    @PostMapping
    @LogAPI("创建用户")
    @ResponseStatus(code = HttpStatus.OK, reason = "用户创建成功")
    public User create(@Validated @RequestBody User user) {
        return userService.create(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK, reason = "删除用户成功")
    @LogAPI("删除用户")
    public void delete(@PathVariable String id) {
        userService.delete(id);
    }

    @PutMapping("/{id}/password")
    @LogAPI("更新密码")
    public Status updatePassword(@PathVariable String id, @RequestParam String newPassword,
                                 @RequestParam String oldPassword) {
        Optional<User> o = userService.findOne(id);
        User user = o.orElseThrow(UserNotFoundException::new);

        boolean valid = RchatUtils.isPasswordValid(oldPassword, user.getPassword(), user.getSalt());
        if (!valid) {
            throw new PasswordNotValidException();
        }

        boolean updated = userService.updatePassword(id, newPassword);

        // 如果密码更新成功，需要通知缓存，删除已登录的用户信息
        if (updated) {
            userCache.removeUserFromCache(user.getName());
        }

        return updated ? new Status(HttpStatus.OK, "密码更新成功") : new Status(HttpStatus.INTERNAL_SERVER_ERROR, "密码更新失败");
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable String id) {
        Optional<User> o = userService.findOne(id);
        return o.orElseThrow(UserNotFoundException::new);
    }

    @GetMapping("/me")
    public User me() {
        return RchatUtils.currentUser();
    }

    @GetMapping
    public List<User> index() {
        return userService.findAll();
    }

    @GetMapping("/{userId}/roles")
    public List<Role> roles(@PathVariable String userId) {
        Optional<User> o = userService.findOne(userId);
        return o.orElseThrow(UserNotFoundException::new).getRoles();
    }
}
