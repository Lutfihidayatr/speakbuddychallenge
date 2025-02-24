package com.lutfi.spchallenge.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Table(name = "phrases")
@Entity
public class Phrase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "content")
    private String content;

    @Column(name = "filaname")
    private String fileName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

    @Column(name = "deleted_at")
    private String deletedAt;


    @PrePersist
    protected void onCreate() {
        createdAt = String.valueOf(ZonedDateTime.now());
        updatedAt = String.valueOf(ZonedDateTime.now());
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = String.valueOf(ZonedDateTime.now());
    }
}
