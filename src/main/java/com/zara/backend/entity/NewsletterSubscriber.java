package com.zara.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "newsletter_subscribers",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "phone")
    }
)
@Getter
@Setter
public class NewsletterSubscriber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    @Column(nullable = false)
    private LocalDateTime subscribedAt;

    @PrePersist
    protected void onCreate() {

        if (subscribedAt == null) {
            subscribedAt = LocalDateTime.now();
        }
    }
}