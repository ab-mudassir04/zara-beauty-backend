package com.zara.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zara.backend.dto.NewsletterSubscriberDTO;
import com.zara.backend.entity.NewsletterSubscriber;
import com.zara.backend.service.NewsletterSubscriberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/newsletter")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174"
})
@RequiredArgsConstructor
public class NewsletterSubscriberController {

    private final NewsletterSubscriberService newsletterSubscriberService;

    // =====================================================
    // CUSTOMER SUBSCRIBE
    // =====================================================

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(
            @RequestBody NewsletterSubscriberDTO dto) {

        Map<String, Object> response = new HashMap<>();

        try {

            NewsletterSubscriber subscriber =
                    newsletterSubscriberService.subscribe(dto);

            response.put("success", true);
            response.put(
                    "message",
                    "Thank you for subscribing to Zara Beauty!"
            );
            response.put("id", subscriber.getId());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity
                    .badRequest()
                    .body(response);
        }
    }

    // =====================================================
    // ADMIN - GET ALL SUBSCRIBERS
    // =====================================================

    @GetMapping("/subscribers")
    public ResponseEntity<?> getAllSubscribers() {

        try {

            List<NewsletterSubscriber> subscribers =
                    newsletterSubscriberService
                            .getAllSubscribers();

            return ResponseEntity.ok(subscribers);

        } catch (Exception e) {

            e.printStackTrace();

            Map<String, Object> response = new HashMap<>();

            response.put("success", false);
            response.put(
                    "message",
                    "Unable to load newsletter subscribers"
            );
            response.put(
                    "error",
                    e.getMessage()
            );

            return ResponseEntity
                    .internalServerError()
                    .body(response);
        }
    }

    // =====================================================
    // ADMIN - DELETE SUBSCRIBER
    // =====================================================

    @DeleteMapping("/subscribers/{id}")
    public ResponseEntity<?> deleteSubscriber(
            @PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();

        try {

            newsletterSubscriberService
                    .deleteSubscriber(id);

            response.put("success", true);
            response.put(
                    "message",
                    "Subscriber deleted successfully"
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity
                    .badRequest()
                    .body(response);

        } catch (Exception e) {

            e.printStackTrace();

            response.put("success", false);
            response.put(
                    "message",
                    "Unable to delete subscriber"
            );

            return ResponseEntity
                    .internalServerError()
                    .body(response);
        }
    }
}