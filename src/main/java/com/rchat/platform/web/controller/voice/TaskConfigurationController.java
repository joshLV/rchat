package com.rchat.platform.web.controller.voice;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.rchat.platform.domain.TaskConfiguration;
import com.rchat.platform.jms.TopicNameConstants;
import com.rchat.platform.service.TaskConfigurationService;

@RestController
@RequestMapping("/voice/task-configs")
public class TaskConfigurationController {
	@Autowired
	private TaskConfigurationService taskConfigService;
	@Autowired
	private JmsTemplate jms;

	@PostMapping
	public TaskConfiguration create(@RequestBody TaskConfiguration cfg) {
		TaskConfiguration configuration = taskConfigService.create(cfg);

		jms.convertAndSend(TopicNameConstants.TASK_CONFIG_CREATE, configuration);
		return configuration;
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(code = HttpStatus.OK, reason = "删除任务配置成功")
	public void delete(@PathVariable String id) {
		Optional<TaskConfiguration> o = taskConfigService.findOne(id);

		o.ifPresent(cfg -> {
			taskConfigService.delete(cfg);

			jms.convertAndSend(TopicNameConstants.TASK_CONFIG_UPDATE, cfg);
		});

	}

	@PutMapping("/{id}")
	public TaskConfiguration update(@PathVariable String id, @RequestBody TaskConfiguration cfg) {
		cfg.setId(id);

		TaskConfiguration configuration = taskConfigService.update(cfg);

		jms.convertAndSend(TopicNameConstants.TASK_CONFIG_UPDATE, configuration);
		return configuration;
	}

	@GetMapping
	public Page<TaskConfiguration> configs(@PageableDefault Pageable pageable) {
		return taskConfigService.findAll(pageable);
	}
}
