package com.rchat.platform.web.controller.api;

import java.util.List;

import javax.jms.ObjectMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.rchat.platform.common.LogAPI;
import com.rchat.platform.domain.Role;
import com.rchat.platform.jms.RchatMQConfiguration;
import com.rchat.platform.jms.RchatMessage;
import com.rchat.platform.service.RoleService;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
	@Autowired
	private RoleService roleService;
	@Autowired
	private JmsTemplate jmsTemplate;

	@GetMapping(params = "name")
	public List<Role> search(@RequestParam String name) {
		return roleService.search(name);
	}

	@GetMapping(params = { "page", "size" })
	public Page<Role> pageableIndex(Pageable pageable) {
		Page<Role> roles = roleService.findAll(pageable);

		return roles;
	}

	@GetMapping
	public List<Role> index() {
		return roleService.findAll();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@LogAPI("创建角色")
	public Role create(@RequestBody Role role) {
		Role _role = roleService.create(role);

		jmsTemplate.send(RchatMQConfiguration.RCHAT_CACHE_QUEUE, (session) -> {
			RchatMessage msg = new RchatMessage(_role);
			ObjectMessage message = session.createObjectMessage(msg);
			return message;
		});

		return _role;
	}

	@DeleteMapping("/{id}")
	@LogAPI("删除角色")
	@ResponseStatus(code = HttpStatus.OK, reason = "删除角色成功")
	public void delete(@PathVariable String id) {
		roleService.delete(id);
	}

	@PutMapping
	@LogAPI("更细角色")
	public Role update(@RequestBody Role role) {
		Role _role = roleService.update(role);
		return _role;
	}

	@GetMapping("/{id}")
	public Role getRole(@PathVariable String id) {
		return roleService.findOne(id).get();
	}
}
