package com.mtg.orders.domain.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "card_orders")
public class CardOrder {
    @Id
    @GeneratedValue
    private UUID id;

    private String buyerId;
    private String cardName;
    private int quantity;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public CardOrder(){}

    public CardOrder(String buyerId, String cardName, int quantity){
        this.buyerId = buyerId;
        this.cardName = cardName;
        this.quantity = quantity;
        this.status = OrderStatus.PENDING;
    }

    public UUID getId(){ return id; }
    public void setId(UUID id){ this.id = id; }
    public String getBuyerId(){ return buyerId; }
    public void setBuyerId(String buyerId){ this.buyerId = buyerId; }
    public String getCardName(){ return cardName; }
    public void setCardName(String cardName){ this.cardName = cardName; }
    public int getQuantity(){ return quantity; }
    public void setQuantity(int quantity){ this.quantity = quantity; }
    public OrderStatus getStatus(){ return status; }
    public void setStatus(OrderStatus status){ this.status = status; }
}

