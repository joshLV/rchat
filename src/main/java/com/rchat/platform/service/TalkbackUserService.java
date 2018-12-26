package com.rchat.platform.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.rchat.platform.aop.OperationType;
import com.rchat.platform.aop.SecurityMethod;
import com.rchat.platform.domain.Agent;
import com.rchat.platform.domain.Brief;
import com.rchat.platform.domain.BusinessRent;
import com.rchat.platform.domain.Department;
import com.rchat.platform.domain.Group;
import com.rchat.platform.domain.GroupSegment;
import com.rchat.platform.domain.Server;
import com.rchat.platform.domain.TalkbackGroup;
import com.rchat.platform.domain.TalkbackRole;
import com.rchat.platform.domain.TalkbackUser;

/**
 * 对讲账号业务接口
 *
 * @author dzhang
 */
public interface TalkbackUserService extends GenericService<TalkbackUser, String> {

    Page<TalkbackUser> findByGroup(Group group, Pageable pageable);

    Page<TalkbackUser> findByDepartments(List<Department> departments, Pageable pageable);

    /**
     * 回收指定对讲账号，回收后，对讲账号落到所在集团名下，即所在部门为空
     *
     * @param user 对讲账号
     * @return 如果回收成功 <code>true</code>，否则<code>false</code>
     */
    boolean recycle(TalkbackUser user);

    /**
     * 禁用指定对讲用户
     *
     * @param user 对讲账号
     * @return 如果禁用成功，返回<code>true</code>，否则，返回<code>false</code>
     */
    boolean disable(TalkbackUser user);

    /**
     * 启用指定对讲用户
     *
     * @param user 对象账号
     * @return 如果启用成功，返回<code>true</code>
     */
    boolean enable(TalkbackUser user);

    /**
     * 回收指定对讲账号，回收后，对讲账号落到所在集团名下，即所在部门为空
     *
     * @param users 对讲账号列表
     * @return
     */
    boolean recycle(List<TalkbackUser> users);

    /**
     * 禁用指定对讲用户
     *
     * @param users
     * @return
     */
    boolean disable(List<TalkbackUser> users);

    /**
     * 启用指定对讲用户
     *
     * @param users
     * @return
     */
    boolean enable(List<TalkbackUser> users);

    /**
     * 对对讲账号续费，续费不过是延长使用时间
     *
     * @param talkbackUser  需要续费对讲账号
     * @param businessRents 需要续费的业务
     * @return 续费成功，不成功会抛出各种异常
     */
    boolean renew(TalkbackUser talkbackUser, List<BusinessRent> businessRents);

    @Transactional
    @SecurityMethod(operation = OperationType.UPDATE)
    void batchRenew(List<TalkbackUser> users, List<BusinessRent> businessRents);

    Page<TalkbackUser> findByTalkbackGroup(TalkbackGroup talkbackGroup, Pageable pageable);

    /**
     * 对讲账号搜索
     *
     * @param fullValue    账号
     * @param department   部门
     * @param group        所属集团
     * @param shortValue   短号
     * @param name         中文名
     * @param activated    激活状态
     * @param agent        所属代理商
     * @param role         账号类型
     * @param createdStart 开户开始时间
     * @param createdEnd   开户结束时间
     * @param renewStart   续费开始时间
     * @param renewEnd     续费结束时间
     * @param pageable     分页信息
     * @return 对讲账号分页数据
     */
    Page<TalkbackUser> search(Optional<String> fullValue, Optional<TalkbackRole> role, Optional<String> shortValue, Optional<String> name, Optional<Boolean> activated, 
    						  Optional<Department> department, Optional<Group> group, Optional<Agent> agent, Optional<Date> createdStart, Optional<Date> createdEnd,
                              Optional<Date> renewStart, Optional<Date> renewEnd, Pageable pageable);

    List<TalkbackUser> findByIdIn(List<String> userIds);

    long countByTalkbackGroup(TalkbackGroup group);

    Page<Brief> findBriefs(Pageable pageable);

    List<TalkbackUser> findBySegment(GroupSegment segment);

    boolean existsBySegment(GroupSegment segment);

    long countByGroup(Group g);

    long countByServer(Server server);

    Page<TalkbackUser> statExpiredTalkbackUsers(String groupName, String agentName, String fullValue, Pageable pageable);

    Page<TalkbackUser> statExpiredTalkbackUsers(String fullValue, Pageable pageable);
    
    Page<TalkbackUser> statActivatedTalkbackUsers(String groupName, String agentName, String fullValue, Pageable pageable);

    Page<TalkbackUser> statActivatedTalkbackUsers(String fullValue, Pageable pageable);

    Page<TalkbackUser> statWarningTalkbackUsers(String groupName, String agentName, String fullValue, Pageable pageable);

    Page<TalkbackUser> statWarningTalkbackUsers(String fullValue, Pageable pageable);

    long countExpiringAmount();

    long countExpiredAmount();

    /**
     * 即将过期账号
     */
    List<TalkbackUser> findExpiring();

    /**
     * 过期账号
     */
    List<TalkbackUser> findExpired();

    /**
     * 新增账号
     */
    List<TalkbackUser> findNew();

    /**
     * 未激活账号
     */
    List<TalkbackUser> findNonactivated();
    
    /**
     * 已激活账号
     */
    List<TalkbackUser> findActivated();

    /**
     * 当日激活账号
     */
    List<TalkbackUser> findNewActivated();

    /**
     * 当日过期账号
     */
    List<TalkbackUser> findNewExpired();

    Page<Brief> findBriefs(Server server, Pageable pageable);

    List<TalkbackUser> findExpiring(Department department);

    List<TalkbackUser> findExpiring(Group group);

    List<TalkbackUser> findExpiring(Agent agent);

    List<TalkbackUser> findNonactivated(Department department);

    List<TalkbackUser> findNonactivated(Group group);

    List<TalkbackUser> findNonactivated(Agent agent);
    
    List<TalkbackUser> findActivated(Department department);

    List<TalkbackUser> findActivated(Group group);

    List<TalkbackUser> findActivated(Agent agent);

    List<TalkbackUser> findNewExpired(Department department);

    List<TalkbackUser> findNewExpired(Group group);

    List<TalkbackUser> findNewExpired(Agent agent);

    List<TalkbackUser> findNewActivated(Department department);

    List<TalkbackUser> findNewActivated(Group group);

    List<TalkbackUser> findNewActivated(Agent agent);

    List<TalkbackUser> findNew(Department department);

    List<TalkbackUser> findNew(Group group);

    List<TalkbackUser> findNew(Agent agent);

    List<TalkbackUser> findExpired(Department department);

    List<TalkbackUser> findExpired(Group group);

    List<TalkbackUser> findExpired(Agent agent);

    List<TalkbackUser> findAll(Department department);

    List<TalkbackUser> findAll(Group group);

    List<TalkbackUser> findAll(Agent agent);
    /**
     * 修改该账号的默认群组
     * @param talkBackGroupsId
     * @param talkBackUserIds
     * @return boolean
     */
	boolean changeDefaultGruops(String talkBackGroupsId, String talkBackUserIds);

	Page<TalkbackUser> page(Optional<String> fullValue, Optional<TalkbackRole> role, Optional<String> shortValue,
			Optional<String> name, Optional<Boolean> activated, Optional<String> departmentId, Optional<String> groupId,
			Optional<String> agentId, Optional<Date> createdStart, Optional<Date> createdEnd, Optional<Date> renewStart,
			Optional<Date> renewEnd, Pageable pageable);
}
