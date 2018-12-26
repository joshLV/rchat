package com.rchat.platform.web.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@MessageMapping("/notify")
public class WebSocketController {
	@MessageMapping("/group")
	@SendTo("/changed/group")
	public Message groupChanged() {
		Message message = new Message(1);
		message.setContent("集团信息变化");
		return message;
	}
}
