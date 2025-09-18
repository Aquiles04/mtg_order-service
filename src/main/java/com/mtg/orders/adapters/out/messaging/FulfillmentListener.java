package com.mtg.orders.adapters.out.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class FulfillmentListener {

    private final KafkaPublisher publisher;
    private final ObjectMapper mapper = new ObjectMapper();

    public FulfillmentListener(KafkaPublisher publisher){
        this.publisher = publisher;
    }

    @KafkaListener(topics = "order.fulfill", groupId = "fulfillment-service")
    public void onOrderFulfill(String payload) throws Exception {
        JsonNode n = mapper.readTree(payload);
        String orderId = n.get("orderId").asText();
        // simulate shipping
        String shipped = String.format("{\"orderId\":\"%s\",\"status\":\"SHIPPED\"}", orderId);
        publisher.publish("order.shipped", orderId, shipped);
    }
}
