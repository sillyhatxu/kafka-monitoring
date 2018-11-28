package com.sillyhat.kafkamonitoring.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KafkaMonitoringDTO {

    private String topic;

    private int partition;

    private long offset;

    public boolean validate(){
        return !StringUtils.isEmpty(topic) && partition > 0 && offset > 0L;
    }

}
