package com.ebabak.springboot_carrent.repository;

import com.ebabak.springboot_carrent.model.Payment;
import com.ebabak.springboot_carrent.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String>{
    Optional<Payment> findByStripeSessionId(String sessionId);
}
