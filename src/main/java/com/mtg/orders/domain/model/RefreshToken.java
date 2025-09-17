package com.mtg.orders.domain.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false)
    private String token;

    private Instant expiresAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserAccount user;

    public RefreshToken(){}

    public RefreshToken(String token, Instant expiresAt, UserAccount user){
        this.token = token; this.expiresAt = expiresAt; this.user = user;
    }

    // getters and setters
    public UUID getId(){ return id; }
    public void setId(UUID id){ this.id = id; }
    public String getToken(){ return token; }
    public void setToken(String token){ this.token = token; }
    public Instant getExpiresAt(){ return expiresAt; }
    public void setExpiresAt(Instant expiresAt){ this.expiresAt = expiresAt; }
    public UserAccount getUser(){ return user; }
    public void setUser(UserAccount user){ this.user = user; }
}
