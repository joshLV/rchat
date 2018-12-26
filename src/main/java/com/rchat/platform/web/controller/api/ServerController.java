package com.rchat.platform.web.controller.api;

import com.rchat.platform.domain.Group;
import com.rchat.platform.domain.Server;
import com.rchat.platform.jms.TopicNameConstants;
import com.rchat.platform.service.GroupService;
import com.rchat.platform.service.ServerService;
import com.rchat.platform.service.TalkbackUserService;
import com.rchat.platform.web.exception.ServerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/servers")
public class ServerController {
    @Autowired
    private ServerService serverService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private TalkbackUserService talkbackUserService;
    @Autowired
    private JmsTemplate jms;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Server create(@RequestBody Server server) {
        return serverService.create(server);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK, reason = "删除服务器成功")
    public void delete(@PathVariable String id) {
        assertExists(id);
        serverService.delete(id);
    }

    private void assertExists(String id) {
        if (!serverService.exists(id))
            throw new ServerNotFoundException();
    }

    @PutMapping("/{id}")
    public Server update(@PathVariable String id, @RequestBody Server server) {
        assertExists(id);

        server.setId(id);
        return serverService.update(server);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK, reason = "分配服务器成功")
    public void patchServer(@PathVariable String id, @RequestBody List<String> groupIds) {
        //List<Group> groups = groupIds.parallelStream().map(groupId -> groupService.findOne(groupId).orElseThrow(GroupNotFoundException::new)).collect(Collectors.toList());
    	List<Group> groups = new ArrayList<Group>(); 
    	for (String groupId : groupIds) {   			
    		Optional<Group> group = groupService.findOne(groupId);
			if(group.isPresent()){
				groups.add(group.get());
			}
		}
        Server server = serverService.findOne(id).orElseThrow(ServerNotFoundException::new);
        groupService.setServer(groups, server);

        groups.parallelStream().forEach(group -> jms.convertAndSend(TopicNameConstants.GROUP_SERVER_CHANGED, new GroupServerChangedEvent(group, group.getServer(), server)));
    }

    private class GroupServerChangedEvent {
        private Group group;
        private Server oldServer;
        private Server newServer;

        public GroupServerChangedEvent(Group group, Server oldServer, Server newServer) {
            this.group = group;
            this.oldServer = oldServer;
            this.newServer = newServer;
        }

        public Group getGroup() {
            return group;
        }

        public void setGroup(Group group) {
            this.group = group;
        }

        public Server getOldServer() {
            return oldServer;
        }

        public void setOldServer(Server oldServer) {
            this.oldServer = oldServer;
        }

        public Server getNewServer() {
            return newServer;
        }

        public void setNewServer(Server newServer) {
            this.newServer = newServer;
        }
    }

    @GetMapping("/{id}")
    public Server details(@PathVariable String id) {
        Server server = serverService.findOne(id).orElseThrow(ServerNotFoundException::new);
        setRemanentCapacity(server);
        return server;
    }

    @GetMapping
    public Page<Server> page(@PageableDefault Pageable pageable) {
        Page<Server> servers = serverService.findAll(pageable);
        servers.forEach(this::setRemanentCapacity);
        return servers;
    }

    /**
     * 填充服务剩余容量
     *
     * @param server 语音服务器
     */
    private void setRemanentCapacity(Server server) {
        // 根据服务器找出当前共有多少对讲账号
        long ammount = talkbackUserService.countByServer(server);
        // 计算剩余容量，有可能为负数
        server.setRemanentCapacity(server.getMaxCapacity() - ammount);
    }
}
