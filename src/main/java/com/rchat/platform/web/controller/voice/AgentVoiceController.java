package com.rchat.platform.web.controller.voice;

import com.rchat.platform.domain.Agent;
import com.rchat.platform.domain.AgentType;
import com.rchat.platform.domain.Brief;
import com.rchat.platform.service.AgentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/voice/agents")
public class AgentVoiceController {
    @Autowired
    private AgentService agentService;

    @GetMapping
    public Page<Brief> briefs(@PageableDefault Pageable pageable) {
        return agentService.findBriefs(pageable);
    }

    @GetMapping(params = "ids")
    public List<AgentDto> list(String[] ids) {
        List<Agent> agents = agentService.findAllInternal(Arrays.asList(ids));
        return agents.parallelStream().map(a -> {
            AgentDto dto = new AgentDto();
            BeanUtils.copyProperties(a, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    public class AgentDto implements Serializable {
        private static final long serialVersionUID = -3404029685534244209L;
        private String id;
        /**
         * 代理商名称
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
         * 代理区域
         */
        private String region;

        /**
         * 额度单价
         */
        private Double creditUnitPrice;

        /**
         * 代理商类型
         */
        private AgentType type = AgentType.NORMAL;

        /**
         * 代理商等级
         */
        private int level;

        /**
         * 代理商等级
         */
        private Date updatedAt;

        /**
         * 上级代理，如果是平台，则没有上级代理
         */
        private Agent parent;

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

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public Double getCreditUnitPrice() {
            return creditUnitPrice;
        }

        public void setCreditUnitPrice(Double creditUnitPrice) {
            this.creditUnitPrice = creditUnitPrice;
        }

        public AgentType getType() {
            return type;
        }

        public void setType(AgentType type) {
            this.type = type;
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

        public Agent getParent() {
            return parent;
        }

        public void setParent(Agent parent) {
            this.parent = parent;
        }

    }
}
