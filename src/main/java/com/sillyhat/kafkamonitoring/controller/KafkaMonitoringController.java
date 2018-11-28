package com.sillyhat.kafkamonitoring.controller;


import com.sillyhat.kafkamonitoring.client.KafkaConsumerClient;
import com.sillyhat.kafkamonitoring.common.ApiResponse;
import com.sillyhat.kafkamonitoring.common.ApiStatus;
import com.sillyhat.kafkamonitoring.common.MonitoringProperties;
import com.sillyhat.kafkamonitoring.domain.KafkaMonitoringDTO;
import com.sillyhat.kafkamonitoring.model.KafkaMonitoring;
import com.sillyhat.kafkamonitoring.repository.KafkaMonitoringRepository;
import com.sillyhat.kafkamonitoring.service.KafkaConsumerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class KafkaMonitoringController {

    private KafkaMonitoringRepository kafkaMonitoringRepository;

    private MonitoringProperties monitoringProperties;

    private KafkaConsumerService kafkaConsumerService;

    @PostMapping(value = "/kafka-monitoring/restart")
    public ResponseEntity restartKafkaMonitoring() {
        try {
            KafkaConsumerClient kafkaConsumerClient = KafkaConsumerClient.getInstance(monitoringProperties.getBootstrapServers(), monitoringProperties.getGroupId(), monitoringProperties.getConsumerCount());
            kafkaConsumerClient.close();
            log.info("Create data-synchronize kafka consumer start.");
            new Thread(() -> kafkaConsumerService.consumer()).start();
            log.info("Create data-synchronize kafka consumer end.");
            return ApiResponse.builder().ok().msg("Operator Success.").build();
        } catch (Exception e){
            log.error("Operator error.",e);
            return ApiResponse.builder().ret(ApiStatus.OPERATOR_ERROR).msg(e.getMessage()).build();
        }
    }

    @GetMapping(value = "/kafka-monitoring/list")
    public ResponseEntity queryKafkaMonitoringList() {
        try {
            List<KafkaMonitoring> kafkaConfigList =  kafkaMonitoringRepository.findAll();
            return ApiResponse.builder().data(kafkaConfigList).msg("Operator Success.").build();
        } catch (Exception e){
            log.error("Operator error.",e);
            return ApiResponse.builder().ret(ApiStatus.OPERATOR_ERROR).msg(e.getMessage()).build();
        }
    }

    @PostMapping(value = "/kafka-monitoring")
    public ResponseEntity addKafkaMonitoring(@RequestBody @NotNull KafkaMonitoringDTO kafkaMonitoringDTO) {
        try {
            if(kafkaMonitoringDTO.validate()){
                return ApiResponse.builder().ok().msg("Operator Success.").build();
            }else{
                return ApiResponse.builder().ret(ApiStatus.OPERATOR_ERROR).msg("Data check error.").build();
            }
        } catch (Exception e){
            log.error("Operator error.",e);
            return ApiResponse.builder().ret(ApiStatus.OPERATOR_ERROR).msg(e.getMessage()).build();
        }
    }

    @PutMapping(value = "/kafka-monitoring/{id}")
    public ResponseEntity updateKafkaMonitoringById(@PathVariable(value = "id") long id,@RequestBody @NotNull KafkaMonitoringDTO kafkaMonitoringDTO) {
        try {
            KafkaMonitoring kafkaMonitoring = kafkaMonitoringRepository.getOne(id);
            kafkaMonitoring.setTopic(kafkaMonitoringDTO.getTopic());
            kafkaMonitoring.setPartition(kafkaMonitoringDTO.getPartition());
            kafkaMonitoring.setOffset(kafkaMonitoringDTO.getOffset());
            kafkaMonitoring.setDelete(false);
            kafkaMonitoringRepository.save(kafkaMonitoring);
            return ApiResponse.builder().msg("Operator Success.").build();
        } catch (Exception e){
            log.error("Operator error.",e);
            return ApiResponse.builder().ret(ApiStatus.OPERATOR_ERROR).msg(e.getMessage()).build();
        }
    }

    @DeleteMapping(value = "/kafka-monitoring/{id}")
    public ResponseEntity deleteKafkaMonitoringById(@PathVariable(value = "id") long id) {
        try {
            KafkaMonitoring kafkaMonitoring = kafkaMonitoringRepository.getOne(id);
            kafkaMonitoring.setDelete(true);
            kafkaMonitoringRepository.save(kafkaMonitoring);
            return ApiResponse.builder().msg("Operator Success.").build();
        } catch (Exception e){
            log.error("Operator error.",e);
            return ApiResponse.builder().ret(ApiStatus.OPERATOR_ERROR).msg(e.getMessage()).build();
        }
    }
}
