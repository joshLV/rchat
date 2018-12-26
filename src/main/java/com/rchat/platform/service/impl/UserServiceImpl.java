package com.rchat.platform.service.impl;

import com.rchat.platform.aop.ResourceType;
import com.rchat.platform.aop.SecurityMethod;
import com.rchat.platform.aop.SecurityService;
import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.domain.User;
import com.rchat.platform.domain.UserRepository;
import com.rchat.platform.service.RoleService;
import com.rchat.platform.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@SecurityService(ResourceType.USER)
public class UserServiceImpl extends AbstractService<User, String> implements UserService {

    @Autowired
    private UserRepository repository;
    @Autowired
    private RoleService roleService;

    @Override
    protected JpaRepository<User, String> repository() {
        return repository;
    }

    @Override
    @Transactional
    public List<User> create(List<User> entities) {
        // 对每个实体进行加密
        entities.parallelStream().map(this::encodePassword);
        return super.create(entities);
    }

    @Override
    @Transactional
    public User create(User entity) {
        encodePassword(entity);
        return super.create(entity);
    }

    @Override
    @Transactional
	@SecurityMethod(false)
    public User update(User entity) {
        entity.setRoles(roleService.findByUser(entity));

        return super.update(entity);
    }

    @Override
    @Transactional
    public List<User> update(List<User> entities) {
        entities.forEach(u -> u.setRoles(roleService.findByUser(u)));

        return super.update(entities);
    }

    /**
     * 对密码进行加密
     *
     * @param user 待加密处理的用户
     * @return 加密之后的用户
     */
    private User encodePassword(User user) {
        // 生成随机盐
        String salt = RandomStringUtils.randomAlphanumeric(32);
        user.setSalt(salt);
        // 编码密码
        user.setPassword(RchatUtils.encodePassword(user.getPassword(), salt));
        return user;
    }

    @Override
    @SecurityMethod(false)
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Override
    @Transactional
    @SecurityMethod(false)
    public User logout(User user) {
        user.setUpdatedAt(new Date());
        user.setOnline(false);

        repository.setOnline(user.getId(), user.isOnline(), user.getUpdatedAt());

        return user;
    }

    @Override
    @Transactional
    @SecurityMethod(false)
    public User login(User user) {
        user.setUpdatedAt(new Date());
        user.setOnline(true);

        repository.setOnline(user.getId(), user.isOnline(), user.getUpdatedAt());

        return user;
    }

    @Override
    @Transactional
    public boolean updatePassword(String id, String password) {
        String salt = RandomStringUtils.randomAlphanumeric(32);
        String newPassword = RchatUtils.encodePassword(password, salt);

        return 1 == repository.updatePassword(id, newPassword, salt);
    }
}
