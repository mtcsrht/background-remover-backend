package com.mtcsrht.bgremovebackend.api.model.Dto;

import java.time.Instant;

public class PictureDto {
    private Long id;
    private String url;
    private Instant createdAt;

    public PictureDto(){}
    public PictureDto(Long id, String url, Instant createdAt) {
        this.id = id;
        this.url = url;
        this.createdAt = createdAt;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}