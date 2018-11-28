package com.sillyhat.kafkamonitoring.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "kafka-monitoring-properties")
public class MonitoringProperties {

    private String bootstrapServers;

    private String groupId;

    private int consumerCount;

    private String elasticsearchHostname;

    private int elasticsearchPort;

    private String elasticsearchScheme;
}
