package com.sillyhat.kafkamonitoring.client;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Properties;

public class KafkaConsumerClient {

    private static volatile KafkaConsumerClient instance;

    private KafkaConsumer<String, String> kafkaConsumer;

    private KafkaConsumerClient(String bootstrapServers,String groupId,int consumerCount){
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1024 * 1024 * 2);
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 1000 * 5);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, consumerCount);
        kafkaConsumer = new KafkaConsumer<>(props);
    }

    public static KafkaConsumerClient getInstance(String bootstrapServers,String groupId,int consumerCount) {
        if (instance == null) {
            synchronized (KafkaConsumerClient.class) {
                if (instance == null) {
                    instance = new KafkaConsumerClient(bootstrapServers,groupId,consumerCount);
                }
            }
        }
        return instance;
    }

    public KafkaConsumer<String, String> getKafkaConsumer() {
        return kafkaConsumer;
    }

    public void close(){
        kafkaConsumer.wakeup();
        instance = null;
    }


}
