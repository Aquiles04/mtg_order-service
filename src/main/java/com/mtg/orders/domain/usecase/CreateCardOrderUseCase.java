package com.mtg.orders.domain.usecase;

import com.mtg.orders.adapters.out.persistence.OrderRepository;
import com.mtg.orders.domain.model.CardOrder;
import com.mtg.orders.domain.model.OrderStatus;
import org.springframework.stereotype.Service;

@Service
public class CreateCardOrderUseCase {

    private final OrderRepository repository;

    public CreateCardOrderUseCase(OrderRepository repository) {
        this.repository = repository;
    }

    public CardOrder execute(CardOrder order) {
        if (order.getQuantity() <= 0) {
            throw new IllegalArgumentException("Order must have quantity greater than 0");
        }
        if (order.getCardName() == null || order.getCardName().isBlank()) {
            throw new IllegalArgumentException("Card name is required");
        }

        order.setStatus(OrderStatus.PENDING);
        return repository.save(order);
    }
}
