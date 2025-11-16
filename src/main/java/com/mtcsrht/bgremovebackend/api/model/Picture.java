package com.mtcsrht.bgremovebackend.api.model;

import jakarta.persistence.*;

@Entity

public class Picture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String path;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Picture(){}

    public Picture(String path){
        this.path = path;
    }
    public Long getId() {
        return id;
    }
    public String getpath() {
        return path;
    }
    public void setpath(String path) {
        this.path = path;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

}
