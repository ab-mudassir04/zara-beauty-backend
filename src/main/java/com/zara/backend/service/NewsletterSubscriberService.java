package com.zara.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zara.backend.dto.NewsletterSubscriberDTO;
import com.zara.backend.entity.NewsletterSubscriber;
import com.zara.backend.repository.NewsletterSubscriberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NewsletterSubscriberService {

    private final NewsletterSubscriberRepository repository;

    // =====================================================
    // CUSTOMER SUBSCRIBE
    // =====================================================

    public NewsletterSubscriber subscribe(
            NewsletterSubscriberDTO dto) {

        if (dto == null) {
            throw new RuntimeException(
                    "Invalid subscriber data."
            );
        }

        // =================================================
        // NAME
        // =================================================

        if (dto.getName() == null ||
                dto.getName().trim().isEmpty()) {

            throw new RuntimeException(
                    "Please enter your name."
            );
        }

        String name = dto.getName().trim();

        // =================================================
        // EMAIL
        // =================================================

        String email = null;

        if (dto.getEmail() != null &&
                !dto.getEmail().trim().isEmpty()) {

            email = dto.getEmail()
                    .trim()
                    .toLowerCase();

            if (repository.existsByEmail(email)) {

                throw new RuntimeException(
                        "This email is already subscribed."
                );
            }
        }

        // =================================================
        // PHONE
        // =================================================

        String phone = null;

        if (dto.getPhone() != null &&
                !dto.getPhone().trim().isEmpty()) {

            phone = dto.getPhone().trim();

            if (repository.existsByPhone(phone)) {

                throw new RuntimeException(
                        "This phone number is already subscribed."
                );
            }
        }

        // =================================================
        // EMAIL OR PHONE REQUIRED
        // =================================================

        if (email == null && phone == null) {

            throw new RuntimeException(
                    "Please enter either your email address or phone number."
            );
        }

        // =================================================
        // CREATE
        // =================================================

        NewsletterSubscriber subscriber =
                new NewsletterSubscriber();

        subscriber.setName(name);
        subscriber.setEmail(email);
        subscriber.setPhone(phone);

        return repository.save(subscriber);
    }

    // =====================================================
    // GET ALL
    // =====================================================

    @Transactional(readOnly = true)
    public List<NewsletterSubscriber> getAllSubscribers() {

        return repository.findAll();
    }

    // =====================================================
    // DELETE
    // =====================================================

    public void deleteSubscriber(Long id) {

        if (id == null) {

            throw new RuntimeException(
                    "Subscriber ID is required."
            );
        }

        if (!repository.existsById(id)) {

            throw new RuntimeException(
                    "Subscriber not found."
            );
        }

        repository.deleteById(id);
    }
}