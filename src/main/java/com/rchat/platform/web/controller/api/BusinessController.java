package com.rchat.platform.web.controller.api;

import com.rchat.platform.common.LogAPI;
import com.rchat.platform.domain.Business;
import com.rchat.platform.exception.NoRightAccessException;
import com.rchat.platform.exception.ResourceNotFoundException;
import com.rchat.platform.jms.TopicNameConstants;
import com.rchat.platform.service.BusinessService;
import com.rchat.platform.web.exception.BusinessNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/api/businesses")
public class BusinessController {
    @Autowired
    private BusinessService businessService;
    @Autowired
    private JmsTemplate jms;

    @GetMapping
    public Page<Business> list(@RequestParam(required = false) Boolean enabled, @PageableDefault Pageable pageable) {
        if (Optional.ofNullable(enabled).isPresent()) {
            return businessService.findByEnabled(enabled, pageable);
        } else
            return businessService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public Business get(@PathVariable String id) {
        Optional<Business> o = businessService.findOne(id);
        return o.orElseThrow(ResourceNotFoundException::new);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @LogAPI("创建业务配置")
    public Business create(@RequestBody Business business) {
        Business createdBusiness = businessService.create(business);
        jms.convertAndSend(TopicNameConstants.BUSINESS_CREATE, createdBusiness);
        return createdBusiness;
    }

    @DeleteMapping("/{id}")
    @LogAPI("删除业务配置")
    public boolean delete(@PathVariable String id) {
        Business business = businessService.findOne(id).orElseThrow(BusinessNotFoundException::new);
        if (business.isInternal()) {
            throw new NoRightAccessException();
        }
        boolean deleted = businessService.delete(business);
        jms.convertAndSend(TopicNameConstants.BUSINESS_DELETE, business);
        return deleted;
    }

    @PatchMapping("/enable")
    @LogAPI("使能业务")
    @ResponseStatus(value = HttpStatus.OK, reason = "使能业务成功")
    public void enableBussiness(String[] ids) {
        businessService.enable(Arrays.asList(ids));
    }

    @PatchMapping("/disable")
    @LogAPI("禁用业务")
    @ResponseStatus(value = HttpStatus.OK, reason = "禁用业务成功")
    public void disableBussiness(String[] ids) {
        businessService.disable(Arrays.asList(ids));
    }


    @PutMapping("/{id}")
    @LogAPI("更新业务配置")
    public Business update(@PathVariable String id, @RequestBody Business business) {
        Business o = businessService.findOne(id).orElseThrow(BusinessNotFoundException::new);
        if (o.isInternal()) {
            business.setEnabled(Boolean.TRUE);
            business.setInternal(Boolean.TRUE);
        }

        business.setId(id);
        Business updatedBusiness = businessService.update(business);
        jms.convertAndSend(TopicNameConstants.BUSINESS_UPDATE, updatedBusiness);
        return updatedBusiness;
    }
}
