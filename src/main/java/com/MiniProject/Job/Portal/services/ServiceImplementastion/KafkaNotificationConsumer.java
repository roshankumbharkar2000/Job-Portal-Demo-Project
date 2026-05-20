    package com.MiniProject.Job.Portal.services.ServiceImplementastion;

    import org.springframework.kafka.annotation.KafkaListener;
    import org.springframework.messaging.simp.SimpMessagingTemplate;
    import org.springframework.stereotype.Component;
    @Component
    public class KafkaNotificationConsumer {

        private final SimpMessagingTemplate messagingTemplate;

        public KafkaNotificationConsumer(SimpMessagingTemplate messagingTemplate) {
            this.messagingTemplate = messagingTemplate;
        }

        /*@KafkaListener(topics = "job-application-notifications", groupId = "job-notification-group")
        public void consume(String message) {
            // Send to WebSocket frontend topic
            messagingTemplate.convertAndSend("/topic/notifications", message);
        }*/

        @KafkaListener(topics = "candidate-applied-topic", groupId = "job-notification-group")
        public void consume(String message) {
            // Send message to all subscribed users
            messagingTemplate.convertAndSend("/topic/job-notifications", message);
        }
    }
