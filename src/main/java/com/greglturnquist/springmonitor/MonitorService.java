package com.greglturnquist.springmonitor;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@MessageEndpoint
@EnableScheduling
public class MonitorService implements SchedulingConfigurer {

	private static Logger log = LoggerFactory.getLogger(MonitorService.class);

	@Autowired
	private TaskScheduler taskScheduler;

	@Autowired
	private SimpMessagingTemplate template;

	private DateTime latest = new DateTime();

	@ServiceActivator(inputChannel = "tailChannel")
	public void handle(String data) {
		log.info("Got " + data);
		//if (data.contains("LightningData")) {
		//    latest = new DateTime(Date.parse("yyyy-MM-dd HH:mm:ss.SSS", data[0..22]))
		//}
	}

	@Scheduled(fixedRate = 5000L)
	public void check() {
		DateTime now = new DateTime();
		Duration duration = new Duration(latest, now);
		if (duration.toStandardSeconds().getSeconds() >= 10) {
			String status = "It has been " + duration.toStandardSeconds().getSeconds() + " seconds since last update!";
			log.error(status);
			template.convertAndSend("/topic/status", new Alarm("BAD", status));
		} else {
			String status = "It has been " + duration.toStandardSeconds().getSeconds() + " seconds since last update";
			log.info(status);
			template.convertAndSend("/topic/status", new Alarm("GOOD", status));
		}
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setTaskScheduler(this.taskScheduler);
	}

}
