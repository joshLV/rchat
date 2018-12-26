package com.rchat.platform.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.rchat.platform.aop.OperationType;
import com.rchat.platform.aop.ResourceType;
import com.rchat.platform.aop.SecurityMethod;
import com.rchat.platform.aop.SecurityService;
import com.rchat.platform.common.BeanSelfAware;
import com.rchat.platform.common.RchatEnv;
import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.common.ToolsUtil;
import com.rchat.platform.domain.Agent;
import com.rchat.platform.domain.Brief;
import com.rchat.platform.domain.Business;
import com.rchat.platform.domain.BusinessRent;
import com.rchat.platform.domain.CreditAccount;
import com.rchat.platform.domain.Department;
import com.rchat.platform.domain.Group;
import com.rchat.platform.domain.GroupSegment;
import com.rchat.platform.domain.RenewLogRepository;
import com.rchat.platform.domain.Server;
import com.rchat.platform.domain.TalkbackGroup;
import com.rchat.platform.domain.TalkbackGroupRepository;
import com.rchat.platform.domain.TalkbackNumber;
import com.rchat.platform.domain.TalkbackRole;
import com.rchat.platform.domain.TalkbackUser;
import com.rchat.platform.domain.TalkbackUserRenewLog;
import com.rchat.platform.domain.TalkbackUserRepository;
import com.rchat.platform.exception.NoRightAccessException;
import com.rchat.platform.service.AgentCreditAccountService;
import com.rchat.platform.service.BusinessRentService;
import com.rchat.platform.service.BusinessService;
import com.rchat.platform.service.CreditAccountService;
import com.rchat.platform.service.GroupCreditAccountService;
import com.rchat.platform.service.TalkbackNumberService;
import com.rchat.platform.service.TalkbackUserService;
import com.rchat.platform.web.exception.CreditInsufficiencyException;

@Service
@SecurityService(ResourceType.TALKBACK_USER)
public class TalkbackUserServiceImpl extends AbstractService<TalkbackUser, String>
        implements TalkbackUserService, BeanSelfAware<TalkbackUserService> {
    @Autowired
    private RchatEnv env;
    @Autowired
    private TalkbackUserRepository repository;
    @Autowired
    private TalkbackGroupRepository talkbackGroupRepository;
    @Autowired
    private RenewLogRepository renewLogRepository;
    @Autowired
    private TalkbackNumberService talkbackNumberService;
    @Autowired
    private BusinessService businessService;
    @Autowired
    private BusinessRentService businessRentService;
    @Autowired
    private AgentCreditAccountService agentCreditAccountService;
    @Autowired
    private GroupCreditAccountService groupCreditAccountService;
    @Autowired
    private CreditAccountService creditAccountService;
    private TalkbackUserService self;

    @Override
    protected JpaRepository<TalkbackUser, String> repository() {
        return repository;
    }

    @Override
    @Transactional
    public List<TalkbackUser> create(List<TalkbackUser> users) {
        Date now = new Date();
        // 获取默认业务
        Business defaultBussiness = businessService.findDefaultBussiness();

        users.parallelStream().forEach(u -> {
            TalkbackNumber number = u.getNumber();
            number.setCreatedAt(now);
            number.setUpdatedAt(now);
            BusinessRent businessRents;
            if (null != u.getBusinessRents() && u.getBusinessRents().size() > 0) {
                List<BusinessRent> bts = new ArrayList<>();
                for (int i = 0; i < u.getBusinessRents().size(); i++) {
                    businessRents = new BusinessRent(u, u.getBusinessRents().get(i).getBusiness());
                    businessRents.setCreatedAt(now);
                    businessRents.setUpdatedAt(now);
                    businessRents.setDeadline(new Date(0));
                    bts.add(businessRents);
                }
                u.setBusinessRents(bts);
            } else {
                businessRents = new BusinessRent(u, defaultBussiness);
                businessRents.setCreatedAt(now);
                businessRents.setUpdatedAt(now);
                businessRents.setDeadline(new Date(0));
                u.setBusinessRents(Collections.singletonList(businessRents));
            }
        });


        return super.create(users);
    }

    @Override
    @Transactional
    public TalkbackUser create(TalkbackUser entity) {
        TalkbackNumber number = talkbackNumberService.create(entity.getNumber());
        entity.setNumber(number);

        // 获取默认业务
        Date now = new Date();
        Business defaultBussiness = businessService.findDefaultBussiness();
        BusinessRent businessRents;
        if (null != entity.getBusinessRents() && entity.getBusinessRents().size() > 0) {
            List<BusinessRent> bts = new ArrayList<>();
            for (int i = 0; i < entity.getBusinessRents().size(); i++) {
                businessRents = new BusinessRent(entity, entity.getBusinessRents().get(i).getBusiness());
                businessRents.setCreatedAt(now);
                businessRents.setUpdatedAt(now);
                businessRents.setDeadline(new Date(0));
                bts.add(businessRents);
            }
            entity.setBusinessRents(bts);
        } else {
            businessRents = new BusinessRent(entity, defaultBussiness);
            businessRents.setCreatedAt(now);
            businessRents.setUpdatedAt(now);
            businessRents.setDeadline(new Date(0));
            entity.setBusinessRents(Collections.singletonList(businessRents));
        }

        return super.create(entity);
    }

    @Override
    public Page<TalkbackUser> findByGroup(Group group, Pageable pageable) {
        return repository.findByGroup(group, pageable);
    }

    @Override
    public Page<TalkbackUser> findByDepartments(List<Department> departments, Pageable pageable) {
        return repository.findByDepartmentIn(departments, pageable);
    }

    @Override
    @Transactional
    @SecurityMethod(operation = OperationType.UPDATE)
    public boolean recycle(TalkbackUser user) {
        return repository.recycle(user.getId()) > 0;
    }

    @Override
    @Transactional
    @SecurityMethod(operation = OperationType.UPDATE)
    public boolean disable(TalkbackUser user) {
        return repository.setEnabled(user.getId(), false) > 0;
    }

    @Override
    @Transactional
    @SecurityMethod(operation = OperationType.UPDATE)
    public boolean enable(TalkbackUser user) {
        return repository.setEnabled(user.getId(), true) > 0;
    }

    @Override
    @Transactional
    @SecurityMethod(operation = OperationType.UPDATE)
    public boolean renew(TalkbackUser talkbackUser, List<BusinessRent> businessRents) {
        // 查找此对讲账号开通的所有业务
        List<BusinessRent> existsBusinessRents = getExistsBusinessRents(talkbackUser, businessRents);
        doRenew(talkbackUser, existsBusinessRents);
        return true;
    }

    private List<BusinessRent> getExistsBusinessRents(TalkbackUser talkbackUser, List<BusinessRent> businessRents) {
    	List<BusinessRent> br = businessRentService.findByTalkbackUser(talkbackUser);
        return businessRentService.findByTalkbackUser(talkbackUser).parallelStream()
                .filter(rent -> rent.getBusiness().isEnabled())
                .filter(rent -> {
                    BusinessRent existsRent = null;
                    for (BusinessRent r : businessRents) {
                        if (r.getBusiness().getId().equals(rent.getBusiness().getId())) {
                            existsRent = r;
                            break;
                        }
                    }
                    boolean exists = existsRent != null;
                    if (exists) {
                        rent.setCreditMonths(existsRent.getCreditMonths());
                    }

                    return exists;
                }).collect(Collectors.toList());
    }

    private void doRenew(TalkbackUser talkbackUser, List<BusinessRent> businessRents) {
        Map<String, BusinessRent> rentMap = businessRents.parallelStream().collect(Collectors.toMap(BusinessRent::getId, b -> b));

        // 当前时间
        final Date now = new Date();
        
        // 充值日志
        TalkbackUserRenewLog log = new TalkbackUserRenewLog();
        
        // 续费，就是将deadline往后加时间
        businessRents.parallelStream().forEach(rent -> {
            BusinessRent businessRent = rentMap.get(rent.getId());

            Date deadline;
            long bc = 0;
            if (talkbackUser.isActivated()) {
                deadline = businessRent.getDeadline();
                if (deadline.before(now))
                    deadline = now;
            } else {
                deadline = now;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(deadline);
            calendar.add(Calendar.MONTH, rent.getCreditMonths());

            rent.setDeadline(calendar.getTime());
            rent.setBusiness(businessRent.getBusiness());
            if (rent.getBusiness().isInternal()) {
                talkbackUser.setDeadline(rent.getDeadline());
                bc = rent.getBusiness().getCreditPerMonth()*rent.getCreditMonths();
                log.setBasicCredit(bc);
            }
        });

        // 总共要消耗的额度数
        long totalCredit = businessRents.parallelStream()
                .mapToLong(b -> b.getCreditMonths() * b.getBusiness().getCreditPerMonth()).sum();

        // 查询额度账号
        CreditAccount account;
        if (RchatUtils.isAgentAdmin() || RchatUtils.isRchatAdmin()) {
            account = agentCreditAccountService.findByAgent(env.currentAgent());
        } else if (RchatUtils.isGroupAdmin()) {
            account = groupCreditAccountService.findByGroup(env.currentGroup());
        } else {
            throw new NoRightAccessException();
        }
        log.setCreditAccount(account);

        // 如果要消费的额度大于额度账号剩余额度，抛出异常
        if (totalCredit > account.getCredit()) {
            throw new CreditInsufficiencyException();
        }

        // 首先减掉额度账户额度
        account.reduceCredit(totalCredit);
        creditAccountService.update(account);

        log.setCredit(totalCredit);
        // 更新对讲账号业务
        businessRentService.update(businessRents);

        // 更新最后一次充值时间
        talkbackUser.setLastRenewed(now);
        // 如果之前账号没有激活过，则将对讲账号置为激活状态，并且设置激活日期
        // 这个日期只会在第一次充值的时候更新，往后就不在更新了，用来统计当月或者当前的激活用户数量
        if (!talkbackUser.isActivated()) {
            talkbackUser.setActivated(true);
            talkbackUser.setActivatedAt(now);
        }
        self.update(talkbackUser);
        // 创建充值记录
        log.setUser(talkbackUser);
        log.setCreatedAt(now);
        log.setUpdatedAt(now);
        renewLogRepository.save(log);
    }

    @Transactional
    @SecurityMethod(operation = OperationType.UPDATE)
    @Override
    public void batchRenew(List<TalkbackUser> users, List<BusinessRent> businessRents) {
        for (TalkbackUser user : users) {
            List<BusinessRent> existsBusinessRents = getExistsBusinessRents(user, businessRents);
            doRenew(user, existsBusinessRents);
        }
    }

    @Override
    public Page<TalkbackUser> findByTalkbackGroup(TalkbackGroup group, Pageable pageable) {
        return repository.findByGroups(group, pageable);
    }

    @Override
    public Page<TalkbackUser> search(Optional<String> fullValue, Optional<TalkbackRole> role, Optional<String> shortValue, Optional<String> name, Optional<Boolean> activated,
                                     Optional<Department> department, Optional<Group> group, Optional<Agent> agent, Optional<Date> createdStart,
                                     Optional<Date> createdEnd, Optional<Date> renewStart, Optional<Date> renewEnd, Pageable pageable) {
        return repository.search(fullValue, role, shortValue, name, activated, department, group, agent, createdStart, createdEnd, renewStart,
                renewEnd, pageable);
    }

    @Override
    public List<TalkbackUser> findByIdIn(List<String> userIds) {
        return repository.findByIdIn(userIds);
    }

    @Override
    public long countByTalkbackGroup(TalkbackGroup group) {
        return repository.countByGroupsIn(Collections.singletonList(group));
    }

    @Override
    @SecurityMethod(false)
    public Page<Brief> findBriefs(Pageable pageable) {
        return repository.findBriefs(pageable);
    }

    @Override
    public List<TalkbackUser> findBySegment(GroupSegment segment) {
        return repository.findByNumberGroupSegment(segment);
    }

    @Override
    public boolean existsBySegment(GroupSegment segment) {
        return repository.existsByNumberGroupSegment(segment);
    }

    @Override
    public long countByGroup(Group g) {
        return repository.countByGroup(g);
    }

    @Override
    public long countByServer(Server server) {
        return repository.countByGroupServer(server);
    }

    @Override
    @SecurityMethod(operation = OperationType.RETRIEVE)
    public Page<TalkbackUser> statExpiredTalkbackUsers(String groupName, String agentName, String fullValue, Pageable pageable) {
        return repository.statExpiredTalkbackUsers(groupName, agentName, fullValue, pageable);
    }

    @Override
    @SecurityMethod(operation = OperationType.RETRIEVE)
    public Page<TalkbackUser> statExpiredTalkbackUsers(String fullValue, Pageable pageable) {
        if (RchatUtils.isRchatAdmin()) {
            return repository.statExpiredTalkbackUsers((Agent) null, null, fullValue, pageable);
        } else if (RchatUtils.isAgentAdmin()) {
            return repository.statExpiredTalkbackUsers(env.currentAgent(), null, fullValue, pageable);
        } else if (RchatUtils.isGroupAdmin()) {
            return repository.statExpiredTalkbackUsers(null, env.currentGroup(), fullValue, pageable);
        } else {
            return new PageImpl<>(Collections.emptyList());
        }
    }
    
    @Override
    @SecurityMethod(operation = OperationType.RETRIEVE)
    public Page<TalkbackUser> statActivatedTalkbackUsers(String groupName, String agentName, String fullValue, Pageable pageable) {
        return repository.statActivatedTalkbackUsers(groupName, agentName, fullValue, pageable);
    }

    @Override
    @SecurityMethod(operation = OperationType.RETRIEVE)
    public Page<TalkbackUser> statActivatedTalkbackUsers(String fullValue, Pageable pageable) {
        if (RchatUtils.isRchatAdmin()) {
            return repository.statActivatedTalkbackUsers((Agent) null, null, fullValue, pageable);
        } else if (RchatUtils.isAgentAdmin()) {
            return repository.statActivatedTalkbackUsers(env.currentAgent(), null, fullValue, pageable);
        } else if (RchatUtils.isGroupAdmin()) {
            return repository.statActivatedTalkbackUsers(null, env.currentGroup(), fullValue, pageable);
        } else {
            return new PageImpl<>(Collections.emptyList());
        }
    }

    @Override
    @SecurityMethod(operation = OperationType.RETRIEVE)
    public Page<TalkbackUser> statWarningTalkbackUsers(String groupName, String agentName, String fullValue, Pageable pageable) {
        return repository.statExpiringTalkbackUsers(groupName, agentName, fullValue, pageable);
    }

    @Override
    @SecurityMethod(operation = OperationType.RETRIEVE)
    public Page<TalkbackUser> statWarningTalkbackUsers(String fullValue, Pageable pageable) {
        if (RchatUtils.isRchatAdmin()) {
            return repository.statExpiringTalkbackUsers((Agent) null, null, fullValue, pageable);
        } else if (RchatUtils.isAgentAdmin()) {
            return repository.statExpiringTalkbackUsers(env.currentAgent(), null, fullValue, pageable);
        } else if (RchatUtils.isGroupAdmin()) {
            return repository.statExpiringTalkbackUsers(null, env.currentGroup(), fullValue, pageable);
        } else {
            return new PageImpl<>(Collections.emptyList());
        }
    }

    @Override
    public long countExpiringAmount() {
        if (RchatUtils.isRchatAdmin()) {
            return repository.countExpiringAmount(null, null, null);
        } else if (RchatUtils.isAgentAdmin()) {
            return repository.countExpiringAmount(env.currentAgent(), null, null);
        } else if (RchatUtils.isGroupAdmin()) {
            return repository.countExpiringAmount(null, env.currentGroup(), null);
        } else if (RchatUtils.isDepartmentAdmin()) {
            return repository.countExpiringAmount(null, null, env.currentDepartment());
        } else {
            return 0;
        }
    }

    @Override
    public long countExpiredAmount() {
        if (RchatUtils.isRchatAdmin()) {
            return repository.countExpiredAmount(null, null, null);
        } else if (RchatUtils.isAgentAdmin()) {
            return repository.countExpiredAmount(env.currentAgent(), null, null);
        } else if (RchatUtils.isGroupAdmin()) {
            return repository.countExpiredAmount(null, env.currentGroup(), null);
        } else if (RchatUtils.isDepartmentAdmin()) {
            return repository.countExpiredAmount(null, null, env.currentDepartment());
        } else {
            return 0;
        }
    }

    @Override
    public List<TalkbackUser> findExpiring() {
        if (RchatUtils.isRchatAdmin()) {
            return repository.findExpiring(null, null, null);
        } else if (RchatUtils.isAgentAdmin()) {
            return repository.findExpiring(env.currentAgent(), null, null);
        } else if (RchatUtils.isGroupAdmin()) {
            return repository.findExpiring(null, env.currentGroup(), null);
        } else if (RchatUtils.isDepartmentAdmin()) {
            return repository.findExpiring(null, null, env.currentDepartment());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<TalkbackUser> findExpired() {
        if (RchatUtils.isRchatAdmin()) {
            return repository.findExpired(null, null, null);
        } else if (RchatUtils.isAgentAdmin()) {
            return repository.findExpired(env.currentAgent(), null, null);
        } else if (RchatUtils.isGroupAdmin()) {
            return repository.findExpired(null, env.currentGroup(), null);
        } else if (RchatUtils.isDepartmentAdmin()) {
            return repository.findExpired(null, null, env.currentDepartment());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<TalkbackUser> findNew() {
        if (RchatUtils.isRchatAdmin()) {
            return repository.findNew(null, null, null);
        } else if (RchatUtils.isAgentAdmin()) {
            return repository.findNew(env.currentAgent(), null, null);
        } else if (RchatUtils.isGroupAdmin()) {
            return repository.findNew(null, env.currentGroup(), null);
        } else if (RchatUtils.isDepartmentAdmin()) {
            return repository.findNew(null, null, env.currentDepartment());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<TalkbackUser> findNonactivated() {
        if (RchatUtils.isRchatAdmin()) {
            return repository.findNonactivated(null, null, null);
        } else if (RchatUtils.isAgentAdmin()) {
            return repository.findNonactivated(env.currentAgent(), null, null);
        } else if (RchatUtils.isGroupAdmin()) {
            return repository.findNonactivated(null, env.currentGroup(), null);
        } else if (RchatUtils.isDepartmentAdmin()) {
            return repository.findNonactivated(null, null, env.currentDepartment());
        } else {
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<TalkbackUser> findActivated() {
        if (RchatUtils.isRchatAdmin()) {
            return repository.findActivated(null, null, null);
        } else if (RchatUtils.isAgentAdmin()) {
            return repository.findActivated(env.currentAgent(), null, null);
        } else if (RchatUtils.isGroupAdmin()) {
            return repository.findActivated(null, env.currentGroup(), null);
        } else if (RchatUtils.isDepartmentAdmin()) {
            return repository.findActivated(null, null, env.currentDepartment());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<TalkbackUser> findNewActivated() {
        if (RchatUtils.isRchatAdmin()) {
            return repository.findNewActivated(null, null, null);
        } else if (RchatUtils.isAgentAdmin()) {
            return repository.findNewActivated(env.currentAgent(), null, null);
        } else if (RchatUtils.isGroupAdmin()) {
            return repository.findNewActivated(null, env.currentGroup(), null);
        } else if (RchatUtils.isDepartmentAdmin()) {
            return repository.findNewActivated(null, null, env.currentDepartment());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<TalkbackUser> findNewExpired() {
        if (RchatUtils.isRchatAdmin()) {
            return repository.findNewExpired(null, null, null);
        } else if (RchatUtils.isAgentAdmin()) {
            return repository.findNewExpired(env.currentAgent(), null, null);
        } else if (RchatUtils.isGroupAdmin()) {
            return repository.findNewExpired(null, env.currentGroup(), null);
        } else if (RchatUtils.isDepartmentAdmin()) {
            return repository.findNewExpired(null, null, env.currentDepartment());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    @SecurityMethod(false)
    public Page<Brief> findBriefs(Server server, Pageable pageable) {
        return repository.findBriefs(server, pageable);
    }

    @Override
    public List<TalkbackUser> findExpiring(Department department) {
        return repository.findExpiring(null, null, department);
    }

    @Override
    public List<TalkbackUser> findExpiring(Group group) {
        return repository.findExpiring(null, group, null);
    }

    @Override
    public List<TalkbackUser> findExpiring(Agent agent) {
        return repository.findExpiring(agent, null, null);
    }

    @Override
    public List<TalkbackUser> findNonactivated(Department department) {
        return repository.findNonactivated(null, null, department);
    }

    @Override
    public List<TalkbackUser> findNonactivated(Group group) {
        return repository.findNonactivated(null, group, null);
    }

    @Override
    public List<TalkbackUser> findNonactivated(Agent agent) {
        return repository.findNonactivated(agent, null, null);
    }
    
    @Override
    public List<TalkbackUser> findActivated(Department department) {
        return repository.findActivated(null, null, department);
    }

    @Override
    public List<TalkbackUser> findActivated(Group group) {
        return repository.findActivated(null, group, null);
    }
    
    @Override
    public List<TalkbackUser> findActivated(Agent agent) {
        return repository.findActivated(agent, null, null);
    }

    @Override
    public List<TalkbackUser> findNewExpired(Department department) {
        return repository.findNewExpired(null, null, department);
    }

    @Override
    public List<TalkbackUser> findNewExpired(Group group) {
        return repository.findNewExpired(null, group, null);
    }

    @Override
    public List<TalkbackUser> findNewExpired(Agent agent) {
        return repository.findNewExpired(agent, null, null);
    }

    @Override
    public List<TalkbackUser> findNewActivated(Department department) {
        return repository.findNewActivated(null, null, department);
    }

    @Override
    public List<TalkbackUser> findNewActivated(Group group) {
        return repository.findNewActivated(null, group, null);
    }

    @Override
    public List<TalkbackUser> findNewActivated(Agent agent) {
        return repository.findNewActivated(agent, null, null);
    }

    @Override
    public List<TalkbackUser> findNew(Department department) {
        return repository.findNew(null, null, department);
    }

    @Override
    public List<TalkbackUser> findNew(Group group) {
        return repository.findNew(null, group, null);
    }

    @Override
    public List<TalkbackUser> findNew(Agent agent) {
        return repository.findNew(agent, null, null);
    }

    @Override
    public List<TalkbackUser> findExpired(Department department) {
        return repository.findExpired(null, null, department);
    }

    @Override
    public List<TalkbackUser> findExpired(Group group) {
        return repository.findExpired(null, group, null);
    }

    @Override
    public List<TalkbackUser> findExpired(Agent agent) {
        return repository.findExpired(agent, null, null);
    }

    @Override
    public List<TalkbackUser> findAll(Department department) {
        return repository.findAll(null, null, department);
    }

    @Override
    public List<TalkbackUser> findAll(Group group) {
        return repository.findAll(null, group, null);
    }

    @Override
    public List<TalkbackUser> findAll(Agent agent) {
        return repository.findAll(agent, null, null);
    }

    @Override
    public List<TalkbackUser> findAll() {
        if (RchatUtils.isRchatAdmin()) {
            return repository.findAll(null, null, null);
        } else if (RchatUtils.isAgentAdmin()) {
            return repository.findAll(env.currentAgent(), null, null);
        } else if (RchatUtils.isGroupAdmin()) {
            return repository.findAll(null, env.currentGroup(), null);
        } else if (RchatUtils.isDepartmentAdmin()) {
            return repository.findAll(null, null, env.currentDepartment());
        } else {
            throw new NoRightAccessException();
        }
    }

    @Override
    @SecurityMethod(false)
    public void setBeanSelf(TalkbackUserService self) {
        this.self = self;
    }

    @Override
    @Transactional
    @SecurityMethod(operation = OperationType.UPDATE)
    public boolean recycle(List<TalkbackUser> users) {
        return repository.recycle(users);
    }

    @Override
    @Transactional
    @SecurityMethod(operation = OperationType.UPDATE)
    public boolean disable(List<TalkbackUser> users) {
        return repository.setEnabled(users, false);
    }

    @Override
    
    @SecurityMethod(operation = OperationType.UPDATE)
    public boolean enable(List<TalkbackUser> users) {
        return repository.setEnabled(users, true);
    }
    /**
     * 修改该账号的默认群组
     * @param talkBackGroupsId
     * @param talkBackUserIds
     * @return boolean
     */
    @Transactional
    @SecurityMethod(false)
	@Override
	public boolean changeDefaultGruops(String talkBackGroupsId, String talkBackUserIds) {
		//判断是否有该群组
		TalkbackGroup talkbackGroup=talkbackGroupRepository.findOne(talkBackGroupsId);
		if(ToolsUtil.isEmpty(talkbackGroup)){
			return false;
		} 
		//判断是否有该账号
		String strs[]=talkBackUserIds.split(",");
		for (String string : strs) {
			TalkbackUser talkbackUser=repository.findOne(string);
			if(ToolsUtil.isNotEmpty(talkbackUser)){
				talkbackUser.setTalkbackGroupId(talkBackGroupsId);
				repository.updateDefaultGruops(string,talkBackGroupsId);
			}
		}
		return true;
	}

    @SecurityMethod(false)
	@Override
	public Page<TalkbackUser> page(Optional<String> fullValue, Optional<TalkbackRole> role, Optional<String> shortValue,
			Optional<String> name, Optional<Boolean> activated, Optional<String> departmentId, Optional<String> groupId,
			Optional<String> agentId, Optional<Date> createdStart, Optional<Date> createdEnd, Optional<Date> renewStart,
			Optional<Date> renewEnd, Pageable pageable) {
		return repository.page(fullValue,role,shortValue,name,activated,departmentId,groupId,agentId,createdStart,createdEnd,renewStart,renewEnd,pageable);
	}

}
