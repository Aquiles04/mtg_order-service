package com.mtg.orders.domain.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserAccount {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    private String roles; // comma separated roles, e.g. ROLE_USER,ROLE_ADMIN

    public UserAccount() {}

    public UserAccount(String username, String passwordHash, String roles) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.roles = roles;
    }

    // getters and setters
    public UUID getId(){ return id; }
    public void setId(UUID id){ this.id = id; }
    public String getUsername(){ return username; }
    public void setUsername(String username){ this.username = username; }
    public String getPasswordHash(){ return passwordHash; }
    public void setPasswordHash(String passwordHash){ this.passwordHash = passwordHash; }
    public String getRoles(){ return roles; }
    public void setRoles(String roles){ this.roles = roles; }
}
