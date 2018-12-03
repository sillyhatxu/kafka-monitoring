package com.sillyhat.kafkamonitoring.service.impl;

import com.sillyhat.kafkamonitoring.client.KafkaConsumerClient;
import com.sillyhat.kafkamonitoring.common.Constants;
import com.sillyhat.kafkamonitoring.common.MonitoringProperties;
import com.sillyhat.kafkamonitoring.model.KafkaMonitoring;
import com.sillyhat.kafkamonitoring.repository.KafkaMonitoringRepository;
import com.sillyhat.kafkamonitoring.service.KafkaConsumerService;
import com.sillyhat.kafkamonitoring.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class KafkaConsumerServiceImpl implements KafkaConsumerService{

    private KafkaMonitoringRepository kafkaMonitoringRepository;

    private MonitoringProperties monitoringProperties;

    @Override
    public void consumer() {
        List<KafkaMonitoring> kafkaMonitoringList = querykafkaMonitoringList();
        if(kafkaMonitoringList == null || kafkaMonitoringList.isEmpty()){
            log.info("Kafka monitoring list is zero.");
            return;
        }
        KafkaConsumer<String, String> kafkaConsumer = KafkaConsumerClient.getInstance(monitoringProperties.getBootstrapServers(), monitoringProperties.getGroupId(), monitoringProperties.getConsumerCount()).getKafkaConsumer();
        try {
            Collection<TopicPartition> topicPartitionCollection = new ArrayList<>();
            for (KafkaMonitoring kafkaMonitoring : kafkaMonitoringList) {
                topicPartitionCollection.add(new TopicPartition(kafkaMonitoring.getTopic(),kafkaMonitoring.getPartition()));
            }
            kafkaConsumer.assign(topicPartitionCollection);
            for (KafkaMonitoring kafkaMonitoring : kafkaMonitoringList) {
                kafkaConsumer.seek(new TopicPartition(kafkaMonitoring.getTopic(),kafkaMonitoring.getPartition()), kafkaMonitoring.getOffset());
            }
            while (true) {
                log.info("---------- polling ----------");
                ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(1));
                BulkRequest request = new BulkRequest();
                for (TopicPartition topicPartition : topicPartitionCollection) {
                    long lastestOffset = -1;
                    for (ConsumerRecord<String, String> record : records.records(topicPartition)) {
                        log.debug("offset = {}, key = {},topic = {},partition = {}, value = {}", record.offset(), record.key(), record.topic(), record.partition(), record.value());
                        lastestOffset = record.offset() + 1;
                        Map<String, Object> data = new HashMap<>();
                        data.put("offset", record.offset());
                        data.put("key", record.key());
                        data.put("topic", record.topic());
                        data.put("partition", record.partition());
                        data.put("value", record.value());
                        Date sendTime = new Date(record.timestamp());
                        data.put("timestamp", sendTime);
                        String index = getIndex(sendTime);
                        request.add(new IndexRequest(index, getType(), Utils.getUUID()).source(data));
                    }
                    if (lastestOffset > 0) {
                        Optional<KafkaMonitoring> kafkaMonitoringOptional = kafkaMonitoringList.stream().filter(kafkaMonitoring -> kafkaMonitoring.getTopic().equals(topicPartition.topic()) && kafkaMonitoring.getPartition() == topicPartition.partition()).findFirst();
                        if (kafkaMonitoringOptional.isPresent()) {
                            KafkaMonitoring kafkaMonitoring = kafkaMonitoringOptional.get();
                            kafkaMonitoring.setOffset(lastestOffset);
                        }
                    }
                }
                if (request.numberOfActions() > 0) {
                    RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost(monitoringProperties.getElasticsearchHostname(), monitoringProperties.getElasticsearchPort(), monitoringProperties.getElasticsearchScheme())));
                    try {
                        BulkResponse bulkResponse = client.bulk(request, RequestOptions.DEFAULT);
                        if(bulkResponse.hasFailures()){
                            log.error("Synchronize data to elasticsearch error.");
                        }
                    } catch (IOException e) {
                        log.error("Synchronize data to elasticsearch error[client.bulk()].",e);
                    } finally {
                        try {
                            client.close();
                        } catch (IOException e) {
                            log.error("Close elaseicsearch client error.",e);
                        }
                    }
                    for (KafkaMonitoring kafkaMonitoring : kafkaMonitoringList) {
                        kafkaMonitoringRepository.updateKafkaMonitoringOffset(kafkaMonitoring.getOffset(),kafkaMonitoring.getId());
                    }
                }
            }
        } catch (WakeupException we){
            log.info("This consumer has already been closed.");
            kafkaConsumer.close();
        }
        log.info("KafkaConsumerService.consumer() end.");
    }

    //kafka-monitoring-2018.11.27
    private String getIndex(Date date){
        return Constants.ELASTICSEARCH_INDEX_KAFKA_MONITORING + Utils.formateDate(date);
    }

    private String getType(){
        return "tags";
    }

//    private KafkaConsumer<String, String> getKafkaConsumer(String bootstrapServers,String groupId,int consumerCount){
//        Properties props = new Properties();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
//        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1024 * 1024 * 2);
//        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 1000 * 5);
//        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, consumerCount);
////        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(props);
//        return new KafkaConsumer<>(props);
//    }

    public List<KafkaMonitoring> querykafkaMonitoringList(){
        return kafkaMonitoringRepository.findByIsDeleteFalse();
    }

}
