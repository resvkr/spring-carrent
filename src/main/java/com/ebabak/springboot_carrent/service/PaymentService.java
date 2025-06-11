package com.ebabak.springboot_carrent.service;

public interface PaymentService {
    public String createCheckoutSession(String rentalId);
    public void handleWebhook(String payload, String signature);
}
