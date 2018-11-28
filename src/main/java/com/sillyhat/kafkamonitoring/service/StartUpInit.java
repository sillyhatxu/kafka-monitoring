package com.sillyhat.kafkamonitoring.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Slf4j
@Component
@AllArgsConstructor
public class StartUpInit {

    private KafkaConsumerService kafkaConsumerService;

    @PostConstruct
    public void init(){
        log.info("Create data-synchronize kafka consumer start.");
        new Thread(() -> kafkaConsumerService.consumer()).start();
        log.info("Create data-synchronize kafka consumer end.");
    }
}
