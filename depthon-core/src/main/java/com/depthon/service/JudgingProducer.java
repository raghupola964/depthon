package com.depthon.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class JudgingProducer {

    private static final String TOPIC = "posts-to-judge";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public JudgingProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPostForJudging(Long postId) {
        // Convert the post ID to a String and send it to the topic
        kafkaTemplate.send(TOPIC, String.valueOf(postId));
        System.out.println("Sent post " + postId + " to the judging queue");
    }
}