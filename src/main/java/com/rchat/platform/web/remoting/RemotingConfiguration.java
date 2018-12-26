package com.rchat.platform.web.remoting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.web.HttpRequestHandler;

import com.rchat.platform.service.UserService;

@Configuration
public class RemotingConfiguration {
	@Autowired
	private UserService userService;

	@Bean("/httpUserService")
	public HttpRequestHandler httpUserService() {
		HttpInvokerServiceExporter exporter = new HttpInvokerServiceExporter();
		exporter.setService(userService);
		exporter.setServiceInterface(UserService.class);

		return exporter;
	}

}
