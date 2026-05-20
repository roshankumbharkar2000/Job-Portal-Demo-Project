package com.MiniProject.Job.Portal.services.ServiceImplementastion;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "job-application-notifications";

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

/*    public void sendCandidateAppliedEvent(String message) {
        kafkaTemplate.send(TOPIC, message);
    }*/

    public void sendCandidateAppliedEvent(String message) {
        kafkaTemplate.send("candidate-applied-topic", message);
    }
}

