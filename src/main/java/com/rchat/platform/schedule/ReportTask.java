package com.rchat.platform.schedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 报表任务（示例）
 *
 * @author dzhang
 * @since 2017-03-08 21:00:06
 */
@Component
public class ReportTask {
	private static final Log log = LogFactory.getLog(ReportTask.class);
	// @Autowired
	// private SimpMessagingTemplate template;

	@Scheduled(fixedDelay = 1000)
	public void groupReport() {
		log.debug("groupReport");
		// template.convertAndSend("/changed/group1", new Date());
	}
}
