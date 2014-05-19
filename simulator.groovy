package com.greglturnquist.springmonitor

import groovy.util.logging.*

@EnableScheduling
@Slf4j
class LightningData {

    @Scheduled(fixedRate = 5000L)
    void buzz() {
        log.info("Simulated activity...")
    }
    
}

@EnableScheduling
@Slf4j
class WeatherForecast {

    @Scheduled(fixedRate = 3000L)
    void buzz() {
        log.info("Simulated activity...")
    }
    
}