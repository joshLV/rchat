package com.rchat.platform.web.controller.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.util.Streams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rchat.platform.common.LogAPI;
import com.rchat.platform.common.RchatEnv;
import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.common.ToolsUtil;
import com.rchat.platform.config.ServerProperties;
import com.rchat.platform.domain.Business;
import com.rchat.platform.domain.BusinessRent;
import com.rchat.platform.domain.Department;
import com.rchat.platform.domain.Group;
import com.rchat.platform.domain.GroupSegment;
import com.rchat.platform.domain.Server;
import com.rchat.platform.domain.TalkbackGroup;
import com.rchat.platform.domain.TalkbackGroupType;
import com.rchat.platform.domain.TalkbackNumber;
import com.rchat.platform.domain.TalkbackUser;
import com.rchat.platform.domain.TalkbackUserRenewLog;
import com.rchat.platform.exception.NoDepartmentException;
import com.rchat.platform.exception.NoRightAccessException;
import com.rchat.platform.jms.TopicNameConstants;
import com.rchat.platform.service.DepartmentService;
import com.rchat.platform.service.DepartmentViewService;
import com.rchat.platform.service.GroupSegmentService;
import com.rchat.platform.service.GroupService;
import com.rchat.platform.service.ServerService;
import com.rchat.platform.service.TalkbackGroupService;
import com.rchat.platform.service.TalkbackNumberService;
import com.rchat.platform.service.TalkbackUserRenewLogService;
import com.rchat.platform.service.TalkbackUserService;
import com.rchat.platform.web.controller.Status;
import com.rchat.platform.web.exception.DataInputInvalidException;
import com.rchat.platform.web.exception.GroupNotFoundException;
import com.rchat.platform.web.exception.IdNotMatchException;
import com.rchat.platform.web.exception.NoAvailableNumberException;
import com.rchat.platform.web.exception.NumberInsufficiencyException;
import com.rchat.platform.web.exception.SegmentNotFoundException;
import com.rchat.platform.web.exception.TalkbackGroupNotFoundException;
import com.rchat.platform.web.exception.TalkbackUserDisabledException;
import com.rchat.platform.web.exception.TalkbackUserNotFoundException;
import com.rchat.platform.web.exception.UnknownActionException;

import io.swagger.annotations.ApiOperation;

@Controller
@RestController
@RequestMapping("/api/talkback-users")
public class TalkbackUserController {
    /**
     * 随机生成
     */
    private static final int STRATEGY_RANDOM = 0;
    /**
     * 顺序生成
     */
    private static final int STRATEGY_SEQUENCE = 1;
    @Autowired
    private RchatEnv env;
    @Autowired
    private TalkbackUserService talkbackUserService;
    @Autowired
    private TalkbackGroupService talkbackGroupService;
    @Autowired
    private TalkbackUserRenewLogService talkbackUserRenewLogService;
    @Autowired
    private DepartmentViewService departmentViewService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private TalkbackNumberService talkbackNumberService;
    @Autowired
    private GroupSegmentService groupSegmentService;
    @Autowired
    private ServerService serverService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private ServerProperties serverProperties;
    @Autowired
    private JmsTemplate jms;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @LogAPI("创建对讲账号")
    public TalkbackUser create(@RequestBody TalkbackUser user) {
        TalkbackNumber number = user.getNumber();
        if (number.getGroupSegment() == null && number.getFullValue() == null) {
            throw new DataInputInvalidException();
        }

        if (RchatUtils.isGroupAdmin()) {
            user.setGroup(env.currentGroup());
        }
        // 如果指定的部门，则创建对讲账号归入部门的默认群组中。
        setDefaultTalkbackGroup(user);
        Optional<TalkbackGroup> o = talkbackGroupService.findByDepartmentAndType(user.getDepartment(), TalkbackGroupType.DEFAULT);
        TalkbackGroup g = o.orElseThrow(TalkbackGroupNotFoundException::new);
        user.setTalkbackGroupId(g.getId());
        
        // 验证所属集团的号段的合法性
        Optional.ofNullable(number.getGroupSegment()).ifPresent(n -> {
            List<GroupSegment> segments = groupSegmentService.findByGroup(user.getGroup());

            GroupSegment groupSegment = groupSegmentService.findOne(number.getGroupSegment().getId())
                    .orElseThrow(SegmentNotFoundException::new);

            boolean contains = segments.contains(groupSegment);
            if (!contains) {
                throw new SegmentNotFoundException();
            }

            number.setGroupSegment(groupSegment);
        });

        // 如果 没有设置密码的话，将完整号段作为密码
        if (user.getPassword() == null) {
            user.setPassword(number.getFullValue());
        }

        // 如果没有指定名称，将完整的号码作为名称
        if (user.getName() == null)
            user.setName(number.getFullValue());

        TalkbackUser u = talkbackUserService.create(user);
        Group group = groupService.findOne(u.getGroup().getId()).orElseThrow(GroupNotFoundException::new);
        u.setGroup(group);
        jms.convertAndSend(TopicNameConstants.TALKBACK_USER_CREATE, Collections.singletonList(u));

        setServerIfNecessary(u.getGroup(), 1);
        return u;
    }

    /**
     * @param ids 对讲账号id列表
     */
    @PatchMapping("/disable")
    @LogAPI("禁用对讲账号")
    @ResponseStatus(code = HttpStatus.OK, reason = "禁用对讲账号成功")
    public void disable(@RequestBody List<String> ids) {
        List<TalkbackUser> users = ids.parallelStream().map(TalkbackUser::new).collect(Collectors.toList());
        talkbackUserService.disable(users);
        List<TalkbackUser> sendUsers = new ArrayList<>();
        for (String id : ids) {
            Optional<TalkbackUser> o = talkbackUserService.findOne(id);
            TalkbackUser user = o.orElseThrow(TalkbackUserNotFoundException::new);
            sendUsers.add(user);
        }

        jms.convertAndSend(TopicNameConstants.TALKBACK_USER_UPDATE, sendUsers);
    }

    /**
     * @param ids 对讲账号id列表
     */
    @PatchMapping("/enable")
    @LogAPI("启用对讲账号")
    @ResponseStatus(code = HttpStatus.OK, reason = "${rchat.message.enableTalkbackUser}")
    public void enable(@RequestBody List<String> ids) {
        List<TalkbackUser> users = ids.parallelStream().map(TalkbackUser::new).collect(Collectors.toList());
        talkbackUserService.enable(users);
        List<TalkbackUser> sendUsers = new ArrayList<>();
        for (String id : ids) {
            Optional<TalkbackUser> o = talkbackUserService.findOne(id);
            TalkbackUser user = o.orElseThrow(TalkbackUserNotFoundException::new);
            sendUsers.add(user);
        }
        jms.convertAndSend(TopicNameConstants.TALKBACK_USER_UPDATE, sendUsers);
    }

    /**
     * @param ids 对讲账号id列表
     */
    @PatchMapping("/recycle")
    @LogAPI("回收对讲账号")
    @ResponseStatus(code = HttpStatus.OK, reason = "回收对讲账号成功")
    public void recycle(@RequestBody List<String> ids) {
        List<TalkbackUser> users = ids.parallelStream().map(TalkbackUser::new).collect(Collectors.toList());
        talkbackUserService.recycle(users);

        jms.convertAndSend(TopicNameConstants.TALKBACK_USER_UPDATE, users);
    }

    @PatchMapping(path = "/{id}", params = "action")
    @LogAPI("更新对讲账号")
    public Status patch(@PathVariable String id, @RequestParam String action,
                        @RequestBody(required = false) List<BusinessRent> businessRents) {
        assertTalkbackUserExists(id);

        switch (action) {
            case "recycle":
                talkbackUserService.recycle(new TalkbackUser(id));
                return new Status(HttpStatus.OK, "回收对讲账号成功");
            case "disable":
                talkbackUserService.disable(new TalkbackUser(id));
                return new Status(HttpStatus.OK, "禁用对讲账号成功");
            case "enable":
                talkbackUserService.enable(new TalkbackUser(id));
                return new Status(HttpStatus.OK, "启用对讲账号成功");
            case "renew":
                Optional.ofNullable(businessRents).ifPresent(rents -> {
                    Optional<TalkbackUser> o = talkbackUserService.findOne(id);
                    TalkbackUser user = o.orElseThrow(TalkbackUserNotFoundException::new);
                    if (!user.isEnabled()) {
                        throw new TalkbackUserDisabledException();
                    }
                    talkbackUserService.renew(user, rents);
                    jms.convertAndSend(TopicNameConstants.TALKBACK_USER_UPDATE, Collections.singletonList(user));
                });
                return new Status(HttpStatus.OK, "续费成功");
            default:
                throw new UnknownActionException();
        }
    }

    private static class BatchRenewDto {
        private List<String> talkbackUserIds;
        private List<BusinessRentDto> bussinessRents;

        public List<String> getTalkbackUserIds() {
            return talkbackUserIds;
        }

        public void setTalkbackUserIds(List<String> talkbackUserIds) {
            this.talkbackUserIds = talkbackUserIds;
        }

        public List<BusinessRentDto> getBussinessRents() {
            return bussinessRents;
        }

        public void setBussinessRents(List<BusinessRentDto> bussinessRents) {
            this.bussinessRents = bussinessRents;
        }
    }

    private static class BusinessRentDto {
        /**
         * 业务Id
         */
        private String businessId;
        /**
         * 充值数量
         */
        private Integer creditMonths;

        public String getBusinessId() {
            return businessId;
        }

        public void setBusinessId(String businessId) {
            this.businessId = businessId;
        }

        public Integer getCreditMonths() {
            return creditMonths;
        }

        public void setCreditMonths(Integer creditMonths) {
            this.creditMonths = creditMonths;
        }
    }

    @PatchMapping("/batch-renew")
    @LogAPI("批量续费")
    @ResponseStatus(code = HttpStatus.OK, reason = "批量充值成功")
    public void batchRenew(@RequestBody BatchRenewDto dto) {
        List<TalkbackUser> users = talkbackUserService.findAll(dto.getTalkbackUserIds()).parallelStream()
                .filter(TalkbackUser::isEnabled).collect(Collectors.toList());
        List<BusinessRent> businessRents = dto.getBussinessRents().parallelStream().map(rent -> {
            BusinessRent businessRent = new BusinessRent();
            businessRent.setCreditMonths(rent.getCreditMonths());
            businessRent.setBusiness(new Business(rent.getBusinessId()));
            return businessRent;
        }).collect(Collectors.toList());
        talkbackUserService.batchRenew(users, businessRents);
        jms.convertAndSend(TopicNameConstants.TALKBACK_USER_UPDATE, users);
    }

    @PostMapping(params = {"count", "strategy"})
    @ResponseStatus(HttpStatus.CREATED)
    @LogAPI("批量创建对讲账号")
    public List<TalkbackUser> create(@RequestParam int count, @RequestParam int strategy,
                                     @RequestParam(defaultValue = "4") int shortValueLength, @Validated @RequestBody TalkbackUser user) {
        if (shortValueLength < 4 || shortValueLength > 6) {
            throw new DataInputInvalidException();
        }

        if (RchatUtils.isGroupAdmin()) {
            user.setGroup(env.currentGroup());
        }
        // 对讲账号一定是属于集团的        
        setServerIfNecessary(user.getGroup(), count);
        Group group = user.getGroup();
        Optional<Server> servers = serverService.findByGroup(group);
        servers.ifPresent(user.getGroup()::setServer);
        // 取出集团所有号段
        List<GroupSegment> segments = groupSegmentService.findByGroup(group);
        GroupSegment groupSegment = groupSegmentService.findOne(user.getNumber().getGroupSegment().getId())
                .orElseThrow(SegmentNotFoundException::new);

        boolean contains = segments.contains(groupSegment);
        if (!contains) {
            throw new SegmentNotFoundException();
        }

        user.getNumber().setGroupSegment(groupSegment);

        // 如果指定的部门，则创建对讲账号归入部门的默认群组中。
        setDefaultTalkbackGroup(user);

        // 集团的所有对讲号码
//        List<TalkbackNumber> numbers = talkbackNumberService.findByGroup(group);
//        List<Integer> _unavailableNumbers = talkbackNumberService.findNumberValuesByGroup(group);
        List<Integer> unavailableNumbers = talkbackNumberService.findNumberValuesByGroup(group); //numbers.parallelStream().map(TalkbackNumber::getValue).collect(Collectors.toList());

        List<Integer> availableNumbers = new ArrayList<>();

        // 终结号码
        int end;

        if (group.isVip()) {
            end = 1000000;
        } else {
            end = 10000;
        }
        for (int n = 1; n < end; n++) {
            availableNumbers.add(n);
        }

        // 剔除不可用的号码
        availableNumbers.removeAll(unavailableNumbers);

        if (availableNumbers.isEmpty()) {
            throw new NoAvailableNumberException();
        }

        if (availableNumbers.size() < count) {
            throw new NumberInsufficiencyException();
        }

        List<TalkbackUser> users = new ArrayList<>();

        switch (strategy) {
            case STRATEGY_RANDOM:
                for (int i = 0; i < count; i++) {
                    int index = RandomUtils.nextInt(0, availableNumbers.size());
                    Integer value = availableNumbers.remove(index);

                    TalkbackNumber number = new TalkbackNumber();
                    number.setValue(value);
                    number.setGroupSegment(groupSegment);

                    TalkbackUser u = new TalkbackUser(user);
                    u.setNumber(number);
                    u.setDeadline(new Date(0));
                    users.add(u);
                }
                break;
            case STRATEGY_SEQUENCE:
                for (int i = 0; i < count; i++) {
                    Integer value = availableNumbers.get(i);

                    TalkbackNumber number = new TalkbackNumber();
                    number.setValue(value);
                    number.setGroupSegment(groupSegment);

                    TalkbackUser u = new TalkbackUser(user);
                    u.setNumber(number);
                    u.setDeadline(new Date(0));
                    users.add(u);
                }
                break;
            default:
                break;
        }

        // 如果没有指定密码
        if (user.getPassword() == null) {
            users.parallelStream().forEach(u -> u.setPassword(u.getNumber().getFullValue()));
        }
        // 如果没有指定名称
        if (user.getName() == null)
            users.parallelStream().forEach(u -> u.setName(u.getNumber().getFullValue()));
        //判断不存在部门
        if(ToolsUtil.isEmpty(user.getDepartment())){
        	throw new NoDepartmentException();
        }
        
        users.parallelStream().forEach(u -> setShortValue(u.getNumber(), shortValueLength));
        
        Optional<TalkbackGroup> o = talkbackGroupService.findByDepartmentAndType(user.getDepartment(), TalkbackGroupType.DEFAULT);
        TalkbackGroup g = o.orElseThrow(TalkbackGroupNotFoundException::new);
        users.parallelStream().forEach(u -> u.setTalkbackGroupId(g.getId()));
        users.parallelStream().forEach(newu -> newu.setFullValue(newu.getNumber().getFullValue()));
        List<TalkbackUser> list = talkbackUserService.create(users);
        jms.convertAndSend(TopicNameConstants.TALKBACK_USER_CREATE, list);

        return list;
    }

    /**
     * 看看需不需要设置集团的语音服务器
     *
     * @param group          指定集团
     * @param additionalSize 新增对讲用户数量
     */
    private void setServerIfNecessary(Group group, long additionalSize) {
        // 如果已经分配了语音服务器，就不管了，如果没有分配，则想办法分配
        if (!groupService.existsServer(group)) {
            Optional<Server> server = getServer();
            // 如果有服务器，则只会返回一个服务器
            server.ifPresent(s -> {
                if (canAllocate(additionalSize, s)) {
                    allocateServer(group, s);
                }
            });
        }
    }

    /**
     * 查询语音服务器
     *
     * @return 剩余容量最多的语音服务器分
     */
    private Optional<Server> getServer() {
        List<Server> servers = serverService.findAll();
        return servers.parallelStream().map(this::setRemanetCapacity).max(this::compareServer);
    }

    /**
     * 设置剩余容量
     *
     * @param server 语音服务器
     * @return 带剩余容量的语音服务器
     */
    private Server setRemanetCapacity(Server server) {
        server.setRemanentCapacity(server.getMaxCapacity() - talkbackUserService.countByServer(server));
        return server;
    }

    /**
     * 返回最大剩余容量的服务器
     *
     * @param s1 服务器
     * @param s2 服务器
     * @return 比较结果
     */
    private int compareServer(Server s1, Server s2) {
        // == -1 表示还没有统计结果
        return Long.compare(s1.getRemanentCapacity(), s2.getRemanentCapacity());
    }

    /**
     * 分配服务器
     *
     * @param group  未分配语音服务器的集团
     * @param server 语音服务器
     */
    private void allocateServer(Group group, Server server) {
        groupService.setServer(Collections.singletonList(group), server);
    }

    /**
     * 判断是否可以分配服务器
     *
     * @param additionalSize 新增对讲账号数量
     * @param server         语音服务器
     * @return 如果能分配，返回<code>true</code>，否则，返回<code>false</code>
     */
    private boolean canAllocate(long additionalSize, Server server) {
        // 获取当前服务器对讲账号总数
        long remanentCapacity = server.getRemanentCapacity();
        long maxCapacity = server.getMaxCapacity();
        // 如果小于服务器 容量上限百分比
        return maxCapacity > 0 &&
                (remanentCapacity - additionalSize + 0.0) / maxCapacity >= (1 - serverProperties.getThresholdRate());
    }

    /**
     * 设置默认对讲群组
     *
     * @param user 对讲账号
     */
    private void setDefaultTalkbackGroup(@Validated @RequestBody TalkbackUser user) {
        Optional.ofNullable(user.getDepartment()).ifPresent(d -> {
            Optional<TalkbackGroup> o = talkbackGroupService.findByDepartmentAndType(d, TalkbackGroupType.DEFAULT);
            TalkbackGroup g = o.orElseThrow(TalkbackGroupNotFoundException::new);

            List<TalkbackGroup> groups = Optional.ofNullable(user.getGroups()).orElse(new ArrayList<>());
            groups.add(g);

            user.setGroups(groups);
        });
    }

    private void setShortValue(TalkbackNumber number, int shortValueLength) {
        String fullValue = number.getFullValue();

        number.setShortValue(StringUtils.substring(fullValue, -shortValueLength));
    }

    @DeleteMapping("/{id}")
    @LogAPI("删除对讲账号")
    @ResponseStatus(code = HttpStatus.OK, reason = "删除对讲账号成功")
    public void delete(@PathVariable String id) {
        Optional<TalkbackUser> o = talkbackUserService.findOne(id);
        TalkbackUser user = o.orElseThrow(TalkbackUserNotFoundException::new);
        //此处用于删除测试数据临时添加的功能,根据需求取消
        if (RchatUtils.isRchatAdmin()) {
	        List<TalkbackUserRenewLog> list = talkbackUserRenewLogService.findRenewLogs(null, null, null, null, user.getNumber().getFullValue());
	        talkbackUserRenewLogService.delete(list);
        }
        talkbackUserService.delete(user);

        jms.convertAndSend(TopicNameConstants.TALKBACK_USER_DELETE, Collections.singletonList(user));
    }

    @PostMapping("/checked")
    @LogAPI("批量删除对讲账号")
    @ResponseStatus(code = HttpStatus.OK, reason = "批量删除账号成功")
    public void deleteAll(@RequestBody List<String> ids) {
        List<TalkbackUser> users = talkbackUserService.findAll(ids);
        for (int i = 0,size = users.size(); i < size; i++) {	
            if (RchatUtils.isRchatAdmin()) {
            	//此处用于删除测试数据临时添加的功能,根据需求取消
    	        List<TalkbackUserRenewLog> list = talkbackUserRenewLogService.findRenewLogs(null, null, null, null, users.get(i).getNumber().getFullValue());
    	        talkbackUserRenewLogService.delete(list);
            }
        	talkbackUserService.delete(users.get(i));
		}
        jms.convertAndSend(TopicNameConstants.TALKBACK_USER_DELETE, users);
    }

    @PutMapping("/{id}")
    @LogAPI("更新对讲账号")
    public TalkbackUser update(@PathVariable String id, @Validated @RequestBody TalkbackUser user) {
        if (!id.equals(user.getId())) {
            throw new IdNotMatchException();
        }

        assertTalkbackUserExists(id);

        user = talkbackUserService.update(user);
        jms.convertAndSend(TopicNameConstants.TALKBACK_USER_UPDATE, Collections.singletonList(user));

        return user;
    }

    private void assertTalkbackUserExists(String id) {
        boolean exists = talkbackUserService.exists(id);
        if (!exists)
            throw new TalkbackUserNotFoundException();
    }

    @GetMapping
    public Page<TalkbackUser> list(@PageableDefault Pageable pageable) {
        if (RchatUtils.isRchatAdmin()) {
            return talkbackUserService.findAll(pageable);
        } else if (RchatUtils.isGroupAdmin()) {
            return talkbackUserService.findByGroup(env.currentGroup(), pageable);
        } else if (RchatUtils.isDepartmentAdmin()) {
            Department parent = env.currentDepartment();
            List<Department> departments = departmentViewService.findChildren(parent);
            departments.add(parent);

            return talkbackUserService.findByDepartments(departments, pageable);
        } else {
            throw new NoRightAccessException();
        }
    }

    @GetMapping("/{id}")
    public TalkbackUser one(@PathVariable String id) {
        Optional<TalkbackUser> o = talkbackUserService.findOne(id);

        return o.orElseThrow(TalkbackUserNotFoundException::new);
    }
    
    
	/**
	 * 读取出filePath中的所有数据信息
	 * 
	 * @param filePath
	 *            excel文件的绝对路径
	 * 
	 */
	@PostMapping("/excel")
	public void getDataFromExcel(@RequestParam(value = "file", required = true) MultipartFile file,HttpServletRequest request,String groupId) {
		try {
			// 判断是否为excel类型文件
			System.out.println(file.getName() + "---" + file.getContentType() + "---" + file.getOriginalFilename());
			String filePath = "";
			
			if (!file.isEmpty()){
				//使用StreamsAPI方式拷贝文件
//			    Streams.copy(file.getInputStream(),new FileOutputStream(filePath),true);
				String realPath = request.getSession().getServletContext().getRealPath("/");
				filePath = realPath+"/" + file.getOriginalFilename();
				file.transferTo(new File(filePath));
			}
			if (!filePath.endsWith(".xls") && !filePath.endsWith(".xlsx")) {
				System.out.println("文件不是excel类型");
			}

			FileInputStream fis = null;
			Workbook wookbook = null;

			try {
				// 获取一个绝对地址的流
				fis = new FileInputStream(filePath);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				// 2003版本的excel，用.xls结尾
				wookbook = new HSSFWorkbook(fis);// 得到工作簿

			} catch (Exception ex) {
				try {
					// 2007版本的excel，用.xlsx结尾
					fis = new FileInputStream(filePath);
					wookbook = new XSSFWorkbook(fis);// 得到工作簿
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// 得到一个工作表
			Sheet sheet = wookbook.getSheetAt(0);

			// 获得表头
			Row rowHead = sheet.getRow(0);

			// 判断表头是否正确
//			if (rowHead.getPhysicalNumberOfCells() != 8) {
//				System.out.println("表头的数量不对!");
//			}

			// 获得数据的总行数
			int totalRowNum = sheet.getLastRowNum();

			// 要获得属性
			String name = "";
			String id = "";
			List<TalkbackUser> list = new ArrayList<TalkbackUser>();
			// 获得所有数据
			for (int i = 1; i <= totalRowNum; i++) {
				// 获得第i行对象
				Row row = sheet.getRow(i);

				// 获得获得第i行第0列的 String类型对象
				Cell cell = row.getCell((short) 1);
				id = cell.getStringCellValue().toString();
				// 获得获得第i行第4列的 String类型对象
				Cell cell1 = row.getCell((short) 4);
				name= cell1.getStringCellValue().toString();

				// 获得一个数字类型的数据
//				cell = row.getCell((short) 1);
//				id = cell.getStringCellValue().toString();
				Optional<TalkbackUser> opuser =talkbackUserService.findOne(id);
				TalkbackUser user=opuser.get();
				user.setName(name);
				 if (RchatUtils.isRchatAdmin()) {
					 list.add(user);
			     } else if (RchatUtils.isGroupAdmin()&& user.getGroup().getId().equals(groupId)) {
			    	 list.add(user);
			     }
			}
			if(ToolsUtil.isNotEmpty(list)){
				talkbackUserService.update(list);
				jms.convertAndSend(TopicNameConstants.TALKBACK_USER_UPDATE, list);
			}
		} catch (TalkbackUserNotFoundException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 修改该账号的默认群组
	 * @param talkBackGroupsId
	 * @param talkBackUserIds
	 */
	@ApiOperation(value = "修改该账号的默认群组")
    @RequestMapping(value = "/changeDefaultGruops", method = RequestMethod.GET)
	public void changeDefaultGruops( @RequestParam("talkBackGroupsId")String talkBackGroupsId, @RequestParam("talkBackUserIds")String talkBackUserIds) {		//talkbackUserService.changeDefaultGruops(talkBackGroupsId, talkBackUserIds);
		Optional<TalkbackGroup> talkbackGroups= talkbackGroupService.findOne(talkBackGroupsId);
		if(ToolsUtil.isEmpty(talkbackGroups)){
			try {
				throw new Exception("不存在该群组");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//判断是否有该账号
		String strs[]=talkBackUserIds.split(",");
		List<TalkbackUser> talkbackUsers=new ArrayList<TalkbackUser>();
		for (String string : strs) {
			Optional<TalkbackUser> talkbackUser=talkbackUserService.findOne(string);
			if(ToolsUtil.isNotEmpty(talkbackUser)){
				TalkbackUser newTalkbackUser=talkbackUser.get();
				newTalkbackUser.setTalkbackGroupId(talkBackGroupsId);
//				talkbackUserService.update(newTalkbackUser);
				talkbackUsers.add(newTalkbackUser);
			}
				
		}
		talkbackUserService.update(talkbackUsers);
		jms.convertAndSend(TopicNameConstants.TALKBACK_USER_UPDATE, talkbackUsers);
	}
}
