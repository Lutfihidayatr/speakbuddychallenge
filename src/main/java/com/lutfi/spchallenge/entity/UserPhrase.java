package com.lutfi.spchallenge.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Table(name = "user_phrases")
@Entity
public class UserPhrase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"user_phrases", "hibernateLazyInitializer", "handler"})
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phrase_id", nullable = false)
    @JsonIgnoreProperties({"user_phrases", "hibernateLazyInitializer", "handler"})
    private Phrase phrase;

    @Column(name = "content")
    private String content;

    @Column(name = "filename")
    private String fileName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @Column(name = "deleted_at")
    private ZonedDateTime deletedAt;

    public UserPhrase(String filename, String content) {
        this.fileName = filename;
        this.content = content;
    }

    public UserPhrase() {}

    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now();
        updatedAt = ZonedDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = ZonedDateTime.now();
    }

    @Override
    public String toString() {
        return "UserPhrase{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", filename='" + fileName + '\'' +
                ", createdDate=" + createdAt +
                '}';
    }
}
