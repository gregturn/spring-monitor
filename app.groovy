package com.greglturnquist.springmonitor

import org.springframework.integration.file.tail.OSDelegatingFileTailingMessageProducer

@Grab("org.springframework.integration:spring-integration-java-dsl:1.0.0.BUILD-SNAPSHOT")
@Log
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

@Log
@MessageEndpoint
class MonitorService {

    @ServiceActivator(inputChannel = "tailChannel")
    void handle(String data) {
        log.info(data)
    }
}