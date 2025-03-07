package com.lutfi.spchallenge.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@Table(name = "phrases")
@Entity
public class Phrase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "phrase", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("phrase")
    private List<UserPhrase> userPhrases;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "example", nullable = false)
    private String example;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @Column(name = "deleted_at")
    private ZonedDateTime deletedAt;
}
