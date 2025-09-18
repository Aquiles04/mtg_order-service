package com.mtg.orders.adapters.out.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mtg.orders.adapters.out.persistence.OrderRepository;
import com.mtg.orders.domain.model.CardOrder;
import com.mtg.orders.domain.model.OrderStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class LogisticsListener {

    private final OrderRepository orderRepository;
    private final KafkaPublisher publisher;
    private final ObjectMapper mapper = new ObjectMapper();

    public LogisticsListener(OrderRepository orderRepository, KafkaPublisher publisher){
        this.orderRepository = orderRepository;
        this.publisher = publisher;
    }

    @KafkaListener(topics = "payment.processed", groupId = "logistics-service")
    public void onPaymentProcessed(String payload) throws Exception {
        JsonNode n = mapper.readTree(payload);
        String orderId = n.get("orderId").asText();
        Optional<CardOrder> maybe = orderRepository.findById(UUID.fromString(orderId));
        if(maybe.isPresent()){
            CardOrder o = maybe.get();
            o.setStatus(OrderStatus.PAID);
            orderRepository.save(o);
            String packPayload = String.format("{\"orderId\":\"%s\",\"status\":\"FULFILL\"}", orderId);
            publisher.publish("order.fulfill", orderId, packPayload);
        }
    }

    @KafkaListener(topics = "order.shipped", groupId = "logistics-service")
    public void onOrderShipped(String payload) throws Exception {
        JsonNode n = mapper.readTree(payload);
        String orderId = n.get("orderId").asText();
        Optional<CardOrder> maybe = orderRepository.findById(UUID.fromString(orderId));
        if(maybe.isPresent()){
            CardOrder o = maybe.get();
            o.setStatus(OrderStatus.FULFILLED);
            orderRepository.save(o);
        }
    }
}
