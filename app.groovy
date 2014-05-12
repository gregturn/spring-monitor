package com.greglturnquist.springmonitor

import groovy.util.logging.*
import org.joda.time.*
import org.springframework.integration.file.tail.OSDelegatingFileTailingMessageProducer
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.TaskScheduler
import org.springframework.messaging.simp.SimpMessagingTemplate

@Grab("joda-time:joda-time:2.3")
@Slf4j
@Configuration
@EnableIntegrationPatterns
class Monitor {

    @Bean
    OSDelegatingFileTailingMessageProducer tailer() {
        def tailer = new OSDelegatingFileTailingMessageProducer()
        tailer.file = new File('sim.log')
        tailer.outputChannel = tailChannel()
        tailer
    }
    
    @Bean
    MessageChannel tailChannel() {
        new DirectChannel()
    }
    
}

@Configuration
@Slf4j
@MessageEndpoint
@EnableScheduling
class MonitorService implements SchedulingConfigurer {

    @Autowired
    TaskScheduler taskScheduler
    
    @Autowired
    SimpMessagingTemplate template
    
    DateTime latest = new DateTime()

    @ServiceActivator(inputChannel = "tailChannel")
    void handle(String data) {
        if (data.contains("LightningData")) {
            latest = new DateTime(Date.parse("yyyy-MM-dd HH:mm:ss.SSS", data[0..22]))
        }
    }
    
    @Scheduled(fixedRate = 5000L)
    void check() {
        def now = new DateTime()
        def duration = new Duration(latest, now)
        if (duration.toStandardSeconds().seconds >= 10) {
            def status = "It has been ${duration.toStandardSeconds().seconds} seconds since last update!"
            log.error(status)
            template.convertAndSend("/topic/status", new Alarm([severity: "BAD", description: status]))
        } else {
            def status = "It has been ${duration.toStandardSeconds().seconds} seconds since last update"
            log.info(status)
            template.convertAndSend("/topic/status", new Alarm([severity: "GOOD", description: status]))
        }
    }
   
    @Override
    void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.taskScheduler = this.taskScheduler
    }
}

@Slf4j
@Grab("thymeleaf-spring4")
@Controller
class HomeController {

    @RequestMapping("/")
    def index() {
        return "index"
    }
    
}

class Alarm {
    String severity
    String description
}

@Grab("org.webjars:stomp-websocket:2.3.1")
@Grab("org.webjars:sockjs-client:0.3.4")
@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
    
    @Override
    void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic")
        config.setApplicationDestinationPrefixes("/app")
    }
    
    @Override
    void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/spring-monitor").withSockJS()
    }
}