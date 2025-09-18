package com.mtg.orders.adapters.out.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPublisher {

    private final KafkaTemplate<String, String> kafka;
    public KafkaPublisher(KafkaTemplate<String, String> kafka){
        this.kafka = kafka;
    }

    public void publish(String topic, String key, String payload){
        kafka.send(topic, key, payload);
    }
}
