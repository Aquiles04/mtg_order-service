package com.mtg.orders.adapters.in.web;

import com.mtg.orders.adapters.out.messaging.KafkaPublisher;
import com.mtg.orders.adapters.out.persistence.OrderRepository;
import com.mtg.orders.domain.model.CardOrder;
import com.mtg.orders.domain.model.CreateOrderRequest;
import com.mtg.orders.domain.model.CreateOrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final KafkaPublisher publisher;

    public OrderController(OrderRepository orderRepository, KafkaPublisher publisher){
        this.orderRepository = orderRepository;
        this.publisher = publisher;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateOrderRequest req){
        CardOrder o = new CardOrder(req.buyerId(), req.cardName(), req.quantity());
        CardOrder saved = orderRepository.save(o);
        String payload = String.format("{\"orderId\":\"%s\",\"buyerId\":\"%s\",\"cardName\":\"%s\",\"quantity\":%d}", saved.getId(), saved.getBuyerId(), saved.getCardName(), saved.getQuantity());        publisher.publish("order.created", saved.getId().toString(), payload);
        return ResponseEntity.accepted().body(new CreateOrderResponse(saved.getId().toString(), saved.getStatus().name()));
    }

    @GetMapping
    public ResponseEntity<List<CardOrder>> list(){ return ResponseEntity.ok(orderRepository.findAll()); }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable UUID id){
        return orderRepository.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}


