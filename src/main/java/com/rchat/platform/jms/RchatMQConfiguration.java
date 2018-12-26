package com.rchat.platform.jms;

import javax.jms.Queue;
import javax.jms.Topic;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
@EnableJms
public class RchatMQConfiguration implements JmsListenerConfigurer {

	public static final String RCHAT_TOPIC = "rchat.topic";
	public static final String RCHAT_CACHE_QUEUE = "rchat.cache.queue";

	@Bean
	public MessageConverter jacksonJmsMessageConverter() {
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();

		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");

		return converter;
	}

	@Override
	public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {
	}

	@Bean
	public Queue agentCreate() {
		return new ActiveMQQueue(QueueNameConstants.AGENT_CREATE);
	}

	@Bean
	public Queue agentDelete() {
		return new ActiveMQQueue(QueueNameConstants.AGENT_DELETE);
	}

	@Bean
	public Queue agentUpdate() {
		return new ActiveMQQueue(QueueNameConstants.AGENT_UPDATE);
	}

	@Bean
	public Queue groupCreate() {
		return new ActiveMQQueue(QueueNameConstants.GROUP_CREATE);
	}

	@Bean
	public Queue groupDelete() {
		return new ActiveMQQueue(QueueNameConstants.GROUP_DELETE);
	}

	@Bean
	public Queue groupUpdate() {
		return new ActiveMQQueue(QueueNameConstants.GROUP_UPDATE);
	}

	@Bean
	public Queue departmentCreate() {
		return new ActiveMQQueue(QueueNameConstants.DEPARTMENT_CREATE);
	}

	@Bean
	public Queue departmentDelete() {
		return new ActiveMQQueue(QueueNameConstants.DEPARTMENT_DELETE);
	}

	@Bean
	public Queue departmentUpdate() {
		return new ActiveMQQueue(QueueNameConstants.DEPARTMENT_UPDATE);
	}

	@Bean
	public Queue talkbackGroupCreate() {
		return new ActiveMQQueue(QueueNameConstants.TALKBACK_GROUP_CREATE);
	}

	@Bean
	public Queue talkbackGroupDelete() {
		return new ActiveMQQueue(QueueNameConstants.TALKBACK_GROUP_DELETE);
	}

	@Bean
	public Queue talkbackGroupUpdate() {
		return new ActiveMQQueue(QueueNameConstants.TALKBACK_GROUP_UPDATE);
	}

	@Bean
	public Queue talkbackUserCreate() {
		return new ActiveMQQueue(QueueNameConstants.TALKBACK_USER_CREATE);
	}

	@Bean
	public Queue talkbackUserDelete() {
		return new ActiveMQQueue(QueueNameConstants.TALKBACK_USER_DELETE);
	}

	@Bean
	public Queue talkbackUserUpdate() {
		return new ActiveMQQueue(QueueNameConstants.TALKBACK_USER_UPDATE);
	}

	@Bean
	public Topic agentCreateTopic() {
		return new ActiveMQTopic(TopicNameConstants.AGENT_CREATE);
	}

	@Bean
	public Topic agentDeleteTopic() {
		return new ActiveMQTopic(TopicNameConstants.AGENT_DELETE);
	}

	@Bean
	public Topic agentUpdateTopic() {
		return new ActiveMQTopic(TopicNameConstants.AGENT_UPDATE);
	}

	@Bean
	public Topic groupCreateTopic() {
		return new ActiveMQTopic(TopicNameConstants.GROUP_CREATE);
	}

	@Bean
	public Topic groupDeleteTopic() {
		return new ActiveMQTopic(TopicNameConstants.GROUP_DELETE);
	}

	@Bean
	public Topic groupUpdateTopic() {
		return new ActiveMQTopic(TopicNameConstants.GROUP_UPDATE);
	}

	@Bean
	public Topic departmentCreateTopic() {
		return new ActiveMQTopic(TopicNameConstants.DEPARTMENT_CREATE);
	}

	@Bean
	public Topic departmentDeleteTopic() {
		return new ActiveMQTopic(TopicNameConstants.DEPARTMENT_DELETE);
	}

	@Bean
	public Topic departmentUpdateTopic() {
		return new ActiveMQTopic(TopicNameConstants.DEPARTMENT_UPDATE);
	}

	@Bean
	public Topic talkbackGroupCreateTopic() {
		return new ActiveMQTopic(TopicNameConstants.TALKBACK_GROUP_CREATE);
	}

	@Bean
	public Topic talkbackGroupDeleteTopic() {
		return new ActiveMQTopic(TopicNameConstants.TALKBACK_GROUP_DELETE);
	}

	@Bean
	public Topic talkbackGroupUpdateTopic() {
		return new ActiveMQTopic(TopicNameConstants.TALKBACK_GROUP_UPDATE);
	}

	@Bean
	public Topic talkbackUserCreateTopic() {
		return new ActiveMQTopic(TopicNameConstants.TALKBACK_USER_CREATE);
	}

	@Bean
	public Topic talkbackUserDeleteTopic() {
		return new ActiveMQTopic(TopicNameConstants.TALKBACK_USER_DELETE);
	}

	@Bean
	public Topic talkbackUserUpdateTopic() {
		return new ActiveMQTopic(TopicNameConstants.TALKBACK_USER_UPDATE);
	}

	@Bean
	public Topic talkbackConfigCreateTopic() {
		return new ActiveMQTopic(TopicNameConstants.TASK_CONFIG_CREATE);
	}

	@Bean
	public Topic talkbackConfigDeleteTopic() {
		return new ActiveMQTopic(TopicNameConstants.TASK_CONFIG_DELETE);
	}

	@Bean
	public Topic talkbackConfigUpdateTopic() {
		return new ActiveMQTopic(TopicNameConstants.TASK_CONFIG_UPDATE);
	}

	@Bean
	public Topic rchatTopic() {
		return new ActiveMQTopic(RCHAT_TOPIC);
	}
}
