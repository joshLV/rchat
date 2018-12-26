package com.rchat.platform.web.controller.voice;

import com.rchat.platform.domain.Brief;
import com.rchat.platform.domain.Server;
import com.rchat.platform.domain.TalkbackGroup;
import com.rchat.platform.service.ServerService;
import com.rchat.platform.service.TalkbackGroupService;
import com.rchat.platform.web.exception.ServerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/voice/talkback-groups")
public class TalkbackGroupVoiceController {
    @Autowired
    private TalkbackGroupService groupService;
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
        return groupService.findBriefs(server, pageable);
    }

    @GetMapping(params = "ids")
    public List<TalkbackGroup> list(String[] ids) {
        return groupService.findAllInternal(Arrays.asList(ids));
    }
}
