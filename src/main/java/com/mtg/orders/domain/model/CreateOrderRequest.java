package com.mtg.orders.domain.model;

public record CreateOrderRequest(String buyerId, String cardName, int quantity) {
}
