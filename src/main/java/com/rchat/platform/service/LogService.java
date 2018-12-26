package com.rchat.platform.service;

import com.rchat.platform.domain.Log;
import com.rchat.platform.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LogService extends GenericService<Log, String> {

    /**
     * 查询指定用户日志信息
     *
     * @param user     用户
     * @param pageable 分页参数
     * @return 日志分页数据
     */
    Page<Log> findAll(User user, Pageable pageable);
}
