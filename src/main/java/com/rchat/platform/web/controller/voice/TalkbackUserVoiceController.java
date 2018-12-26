package com.rchat.platform.web.controller.voice;

import com.rchat.platform.domain.Brief;
import com.rchat.platform.domain.Server;
import com.rchat.platform.domain.TalkbackUser;
import com.rchat.platform.service.ServerService;
import com.rchat.platform.service.TalkbackUserService;
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
@RequestMapping("/voice/talkback-users")
public class TalkbackUserVoiceController {
    @Autowired
    private TalkbackUserService talkbackUserService;
    @Autowired
    private ServerService serverService;

    @GetMapping("/login")
    public Server login(String username, String password) {
        Server server = serverService.findByTalkbackUser(username, password).orElseThrow(ServerNotFoundException::new);
        return server;
    }

    @GetMapping
    public Page<Brief> findAll(@RequestParam(required = false) String domain, @RequestParam(required = false) String ipAddress, @PageableDefault Pageable pageable) {
        Server server;
        if (Optional.ofNullable(domain).isPresent()) {
            server = serverService.findByDomain(domain).orElseThrow(ServerNotFoundException::new);
        } else if (Optional.ofNullable(ipAddress).isPresent()) {
            server = serverService.findByIp(ipAddress).orElseThrow(ServerNotFoundException::new);
        } else {
            server = null;
        }
        return talkbackUserService.findBriefs(server, pageable);
    }

    @GetMapping(params = "ids")
    public List<TalkbackUser> findByIds(String[] ids) {
        return talkbackUserService.findAllInternal(Arrays.asList(ids));
    }
}
