package com.greglturnquist.springmonitor

import groovy.util.logging.*
import org.joda.time.*
import org.springframework.integration.file.tail.OSDelegatingFileTailingMessageProducer

@Grab("org.springframework.integration:spring-integration-java-dsl:1.0.0.BUILD-SNAPSHOT")
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

@Slf4j
@MessageEndpoint
@EnableScheduling
class MonitorService {

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
            log.error("It has been ${duration.toStandardSeconds().seconds} seconds since last update!")
        } else {
            log.info("It has been ${duration.toStandardSeconds().seconds} seconds since last update")
        }
    }
}