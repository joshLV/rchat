package com.rchat.platform.web.controller.voice;

import com.rchat.platform.domain.*;
import com.rchat.platform.service.DepartmentService;
import com.rchat.platform.service.ServerService;
import com.rchat.platform.web.exception.ServerNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/voice/departments")
public class DepartmentVoiceController {
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private ServerService serverService;

    @GetMapping
    public Page<Brief> findAll(@RequestParam(required = false) String ipAddress, @RequestParam(required = false) String domain, @PageableDefault Pageable pageable) {
        Server server;
        if (Optional.ofNullable(domain).isPresent()) {
            server = serverService.findByDomain(domain).orElseThrow(ServerNotFoundException::new);
        } else if (Optional.ofNullable(ipAddress).isPresent()) {
            server = serverService.findByIp(ipAddress).orElseThrow(ServerNotFoundException::new);
        } else {
            server = null;
        }
        return departmentService.findBriefs(server, pageable);
    }

    @GetMapping(params = "ids")
    public List<DepartmentDto> list(String[] ids) {
        List<Department> departments = departmentService.findAllInternal(Arrays.asList(ids));

        return departments.parallelStream().map(d -> {
            DepartmentDto dto = new DepartmentDto();
            BeanUtils.copyProperties(d, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    public class DepartmentDto implements Serializable {
        private static final long serialVersionUID = -549978600262988808L;
        private String id;
        /**
         * 部门名称
         */
        private String name;
        /**
         * 联系人
         */
        private String linkman;
        /**
         * 联系电话
         */
        private String phone;
        /**
         * 联系邮箱
         */
        private String email;

        /**
         * 所属集团，如果group_id 不为空，说明是集团直属的部门
         */
        private Group group;

        /**
         * 部门权限
         */
        private DepartmentPrivilege privilege;

        private int level = 1;

        /**
         * 代理商等级
         */
        private Date updatedAt;

        /**
         * 所属部门
         */
        private Department parent;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLinkman() {
            return linkman;
        }

        public void setLinkman(String linkman) {
            this.linkman = linkman;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Group getGroup() {
            return group;
        }

        public void setGroup(Group group) {
            this.group = group;
        }

        public DepartmentPrivilege getPrivilege() {
            return privilege;
        }

        public void setPrivilege(DepartmentPrivilege privilege) {
            this.privilege = privilege;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public Date getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(Date updatedAt) {
            this.updatedAt = updatedAt;
        }

        public Department getParent() {
            return parent;
        }

        public void setParent(Department parent) {
            this.parent = parent;
        }


    }
}
