package com.greglturnquist.springmonitor;

import java.io.File;

import org.springframework.context.annotation.Bean;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.file.tail.OSDelegatingFileTailingMessageProducer;

public class Monitor {

    @Bean
    public OSDelegatingFileTailingMessageProducer tailer() {
        OSDelegatingFileTailingMessageProducer tailer = new OSDelegatingFileTailingMessageProducer();
        tailer.setFile(new File("sim.log"));
		tailer.setOutputChannel(tailChannel());
        return tailer;
    }

    @Bean
    public MessageChannel tailChannel() {
        return new DirectChannel();
    }

}
