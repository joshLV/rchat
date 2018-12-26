package com.rchat.platform.web.config;

import java.lang.reflect.Method;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.rchat.platform.common.LogAPI;
import com.rchat.platform.common.LogContext;
import com.rchat.platform.common.LogContextHolder;
import com.rchat.platform.domain.Log;
import com.rchat.platform.domain.OperationStatus;
import com.rchat.platform.service.LogService;

@Configuration
public class MvcConfiguration extends WebMvcConfigurerAdapter {
	@Autowired
	private LogService logService;

	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LogContextHandlerInterceptor(logService)).addPathPatterns("/api/**");
	}

	private static final class LogContextHandlerInterceptor extends HandlerInterceptorAdapter {
		private static final org.apache.commons.logging.Log logger = LogFactory
				.getLog(LogContextHandlerInterceptor.class);
		private LogService logService;

		public LogContextHandlerInterceptor(LogService logService) {
			this.logService = logService;
		}

		@Override
		public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
				throws Exception {

			HandlerMethod h = (HandlerMethod) handler;
			Method method = h.getMethod();

			Optional.ofNullable(AnnotationUtils.findAnnotation(method, LogAPI.class)).ifPresent(lm -> {
				logger.info("################ 初始化日志上下文 ############");
				LogContext ctx = LogContextHolder.initializeLogContext();
				Log log = ctx.getLog();
				log.setResource(lm.resource());
				log.setOperation(lm.operation());
				log.setUrl(request.getRequestURI());
			});

			return super.preHandle(request, response, handler);
		}

		@Override
		public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
				ModelAndView modelAndView) throws Exception {
			Optional.ofNullable(LogContextHolder.getLogContext()).ifPresent(ctx -> {
				Log log = ctx.getLog();
				log.setStatus(OperationStatus.SUCCESSFUL);

				logger.info("################ 保存日志 ############");
				logService.create(log);
				
				LogContextHolder.clearContext();
			});

			super.postHandle(request, response, handler, modelAndView);
		}

		@Override
		public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
				Exception _ex) throws Exception {
			Optional.ofNullable(LogContextHolder.getLogContext()).ifPresent(ctx -> {
				// 操作失败
				Optional.ofNullable(request.getAttribute(DispatcherServlet.EXCEPTION_ATTRIBUTE)).ifPresent(e -> {
					Exception ex = (Exception) e;
					logger.error("操作失败", ex);
					Optional.ofNullable(ctx.getLog()).ifPresent(log -> {
						log.setMessage(ex.getMessage());
						log.setStatus(OperationStatus.FAILED);
						logService.create(log);
					});
				});

			});
			super.afterCompletion(request, response, handler, _ex);
		}

		@Override
		public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response,
				Object handler) throws Exception {
			super.afterConcurrentHandlingStarted(request, response, handler);
		}
	}
}
