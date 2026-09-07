package com.zara.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zara.backend.entity.NewsletterSubscriber;

public interface NewsletterSubscriberRepository
        extends JpaRepository<NewsletterSubscriber, Long> {

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
}