package com.mtcsrht.bgremovebackend.api.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity

public class Picture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String objectKey;

    // optional but nice
    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Picture(){}

    public Picture(User user, String objectKey) {
        this.user = user;
        this.objectKey = objectKey;
    }
    public Long getId() {
        return id;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
