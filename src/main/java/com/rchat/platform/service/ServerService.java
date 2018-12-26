package com.rchat.platform.service;

import com.rchat.platform.domain.Group;
import com.rchat.platform.domain.Server;

import java.util.Optional;

public interface ServerService extends GenericService<Server, String> {
    Optional<Server> findByGroup(Group group);

    Optional<Server> findByIp(String serverIp);

    Optional<Server> findByDomain(String domain);

    /**
     * 根据对讲账号查找所在服务器
     *
     * @param username 对讲账号
     * @param password 对讲密码
     * @return 语音服务器地址
     */
    Optional<Server> findByTalkbackUser(String username, String password);
}
