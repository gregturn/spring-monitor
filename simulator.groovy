package com.greglturnquist.springmonitor

@EnableScheduling
@Log
class LightningData {

    @Scheduled(fixedRate = 5000L)
    void buzz() {
        log.info("Activity...")
    }
    
}

@EnableScheduling
@Log
class WeatherForecast {

    @Scheduled(fixedRate = 3000L)
    void buzz() {
        log.info("Activity...")
    }
    
}