package com.sillyhat.kafkamonitoring.repository;

import com.sillyhat.kafkamonitoring.model.KafkaMonitoring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface KafkaMonitoringRepository extends JpaRepository<KafkaMonitoring, Long> {

    List<KafkaMonitoring> findByIsDeleteFalse();

    @Modifying
    @Transactional(rollbackFor = {Exception.class})
    @Query(value = "update kafka_monitoring t set t.kafka_offset = ?1,t.last_modified_date = now() where id = ?2",nativeQuery = true)
    void updateKafkaMonitoringOffset(long offset,long id);

}
