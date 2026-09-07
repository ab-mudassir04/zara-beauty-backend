package com.zara.backend.service;

import java.nio.charset.StandardCharsets;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.razorpay.RazorpayClient;

@Service
public class RazorpayService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    // ==========================================
    // GET KEY
    // ==========================================

    public String getKeyId() {
        return keyId;
    }

    // ==========================================
    // CREATE RAZORPAY ORDER
    // ==========================================

    public com.razorpay.Order createPaymentOrder(
            Double amount,
            String receipt
    ) throws Exception {

        if (amount == null || amount <= 0) {
            throw new RuntimeException(
                    "Payment amount must be greater than zero"
            );
        }

        if (receipt == null || receipt.trim().isEmpty()) {
            throw new RuntimeException(
                    "Payment receipt is required"
            );
        }

        RazorpayClient razorpayClient =
                new RazorpayClient(
                        keyId,
                        keySecret
                );

        // INR -> Paise
        int amountInPaise =
                (int) Math.round(amount * 100);

        JSONObject options =
                new JSONObject();

        options.put(
                "amount",
                amountInPaise
        );

        options.put(
                "currency",
                "INR"
        );

        options.put(
                "receipt",
                receipt
        );

        return razorpayClient.orders.create(
                options
        );
    }

    // ==========================================
    // VERIFY PAYMENT
    // ==========================================

    public boolean verifyPayment(
            String razorpayOrderId,
            String razorpayPaymentId,
            String razorpaySignature
    ) {

        try {

            if (razorpayOrderId == null ||
                    razorpayPaymentId == null ||
                    razorpaySignature == null) {

                return false;
            }

            String payload =
                    razorpayOrderId +
                    "|" +
                    razorpayPaymentId;

            String generatedSignature =
                    hmacSha256(
                            payload,
                            keySecret
                    );

            return generatedSignature.equals(
                    razorpaySignature
            );

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }

    // ==========================================
    // REFUND
    // ==========================================

    public com.razorpay.Refund refundPayment(
            String paymentId,
            Double amount
    ) throws Exception {

        if (paymentId == null ||
                paymentId.trim().isEmpty()) {

            throw new RuntimeException(
                    "Razorpay payment ID is required"
            );
        }

        if (amount == null || amount <= 0) {

            throw new RuntimeException(
                    "Refund amount must be greater than zero"
            );
        }

        RazorpayClient razorpayClient =
                new RazorpayClient(
                        keyId,
                        keySecret
                );

        int amountInPaise =
                (int) Math.round(amount * 100);

        JSONObject options =
                new JSONObject();

        options.put(
                "amount",
                amountInPaise
        );

        return razorpayClient.payments.refund(
                paymentId,
                options
        );
    }

    // ==========================================
    // HMAC SHA256
    // ==========================================

    private String hmacSha256(
            String data,
            String secret
    ) throws Exception {

        Mac mac =
                Mac.getInstance("HmacSHA256");

        SecretKeySpec secretKeySpec =
                new SecretKeySpec(
                        secret.getBytes(
                                StandardCharsets.UTF_8
                        ),
                        "HmacSHA256"
                );

        mac.init(secretKeySpec);

        byte[] hash =
                mac.doFinal(
                        data.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        StringBuilder hex =
                new StringBuilder();

        for (byte b : hash) {

            String hexValue =
                    Integer.toHexString(
                            0xff & b
                    );

            if (hexValue.length() == 1) {
                hex.append('0');
            }

            hex.append(hexValue);
        }

        return hex.toString();
    }
}