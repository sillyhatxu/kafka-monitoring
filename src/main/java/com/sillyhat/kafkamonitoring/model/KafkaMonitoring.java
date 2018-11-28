package com.sillyhat.kafkamonitoring.model;

import lombok.*;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KafkaMonitoring extends BaseModel implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="kafka_topic")
    private String topic;

    @Column(name="kafka_partition")
    private int partition;

    @Column(name="kafka_offset")
    private long offset;

    private boolean isDelete;

    public boolean validate(){
        return !StringUtils.isEmpty(topic) && partition > 0 && offset > 0L;
    }


}