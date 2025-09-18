package com.mtg.orders.adapters.out.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BillingListener {

    private final KafkaPublisher publisher;
    private final ObjectMapper mapper = new ObjectMapper();

    public BillingListener(KafkaPublisher publisher){
        this.publisher = publisher;
    }

    @KafkaListener(topics = "order.created", groupId = "billing-service")
    public void onOrderCreated(String payload) throws Exception {
        JsonNode n = mapper.readTree(payload);
        String orderId = n.get("orderId").asText();
        // Here we'd charge; for demo always success
        String paymentPayload = String.format("{\"orderId\":\"%s\",\"status\":\"PAID\"}", orderId);
        publisher.publish("payment.processed", orderId, paymentPayload);
    }
}
