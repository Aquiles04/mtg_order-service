package com.mtg.orders.adapters.out.persistence;

import com.mtg.orders.domain.model.CardOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<CardOrder, UUID> {
}
