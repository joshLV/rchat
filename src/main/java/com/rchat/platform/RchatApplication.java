package com.rchat.platform;

import com.rchat.platform.config.ServerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.support.SpringBootServletInitializer;

import com.rchat.platform.config.AuthorityProperties;
import com.rchat.platform.config.IndexProperties;

@SpringBootApplication
@EnableConfigurationProperties({ IndexProperties.class, AuthorityProperties.class, ServerProperties.class })
public class RchatApplication extends SpringBootServletInitializer {
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(RchatApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(RchatApplication.class, args);
	}
}
