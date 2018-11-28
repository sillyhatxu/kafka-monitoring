package com.sillyhat.kafkamonitoring.repository;

import com.sillyhat.kafkamonitoring.model.KafkaMonitoring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface KafkaMonitoringRepository extends JpaRepository<KafkaMonitoring, Long> {

    List<KafkaMonitoring> findByIsDeleteFalse();

}
