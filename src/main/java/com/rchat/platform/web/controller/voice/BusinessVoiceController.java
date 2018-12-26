package com.rchat.platform.web.controller.voice;

import com.rchat.platform.domain.Brief;
import com.rchat.platform.domain.Business;
import com.rchat.platform.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/voice/bussiness")
public class BusinessVoiceController {
    @Autowired
    private BusinessService businessService;

    @GetMapping
    public Page<Brief> briefs(@PageableDefault Pageable pageable) {
        return businessService.findBriefs(pageable);
    }

    @GetMapping(params = "ids")
    public List<Business> list(String[] ids) {
        return businessService.findAllInternal(Arrays.asList(ids));
    }
}
