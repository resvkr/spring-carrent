package com.ebabak.springboot_carrent.service.impl;

import com.ebabak.springboot_carrent.enums.PaymentStatus;
import com.ebabak.springboot_carrent.model.Payment;
import com.ebabak.springboot_carrent.model.Rental;
import com.ebabak.springboot_carrent.repository.PaymentRepository;
import com.ebabak.springboot_carrent.repository.RentalRepository;
import com.ebabak.springboot_carrent.service.PaymentService;
import com.ebabak.springboot_carrent.service.RentalService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.stripe.model.checkout.Session;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final RentalService rentalService;
    private final RentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;

    @Value("${STRIPE_API_KEY}")
    private String apiKey;

    @Value("${WEBHOOK_SECRET}")
    private String webhookSecret;

    @Override
    @Transactional
    public String createCheckoutSession(String rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException("Rental not found with id: " + rentalId));


        Stripe.apiKey = apiKey;

        BigDecimal rentalPrice = calculateRentalPrice(rental);
        long amount = rentalPrice.multiply(BigDecimal.valueOf(100)).longValue();



        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName("Rental " + rentalId)
                        .build();


        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency("pln")
                        .setUnitAmount(amount)
                        .setProductData(productData)
                        .build();


        SessionCreateParams.LineItem lineItem =
                SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(priceData)
                        .build();


        SessionCreateParams params = SessionCreateParams.builder()
                .addLineItem(lineItem)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .putMetadata("rentalId", rentalId)
                .setSuccessUrl("http://localhost:8080/api/payments/success")
                .setCancelUrl("http://localhost:8080/api/payments/cancel")
                .build();

        try {
            Session session = Session.create(params);
            Payment payment = Payment.builder()
                    .id(UUID.randomUUID().toString())
                    .amount(rentalPrice.doubleValue())
                    .createdAt(LocalDateTime.now())
                    .rental(rental)
                    .stripeSessionId(session.getId())
                    .status(PaymentStatus.PENDING)
                    .build();
            paymentRepository.save(payment);
            return session.getUrl();
        } catch (Exception e) {
            throw new RuntimeException("Stripe session creation failed", e);
        }
    }

    @Override
    @Transactional
    public void handleWebhook(String payload, String signature) {
        System.out.println("Stripe-Signature: " + signature);
        System.out.println("Received webhook payload: " + payload);
        System.out.println("Stripe-Signature: " + signature);

        Stripe.apiKey = apiKey;
        Event event;
        try {
            event = Webhook.constructEvent(payload, signature, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new RuntimeException("Invalid signature", e);
        }


        if ("checkout.session.completed".equals(event.getType())) {
            StripeObject stripeObject = event.getDataObjectDeserializer().getObject().orElseThrow();
            String sessionId = ((Session) stripeObject).getId();

            if (sessionId != null) {
                paymentRepository.findByStripeSessionId(sessionId).ifPresent(payment -> {
                    payment.setStatus(PaymentStatus.PAID);
                    payment.setPaidAt(LocalDateTime.now());
                    paymentRepository.save(payment);


                    Rental rental = payment.getRental();
                    rentalService.returnRental(rental.getVehicleId().getId(), rental.getUser().getId());
                });
            }
        }
    }

    public BigDecimal calculateRentalPrice(Rental rental) {
        LocalDate rentDate = LocalDate.parse(rental.getRentDate());

        LocalDate returnDate;
        if (rental.getReturnDate() == null) {
            returnDate = LocalDate.now();
        } else {
            returnDate = LocalDate.parse(rental.getReturnDate());
        }

        long rentalDays = ChronoUnit.DAYS.between(rentDate, returnDate);
        if (rentalDays < 1) {
            rentalDays = 1;
        }

        BigDecimal dailyPrice = BigDecimal.valueOf(rental.getVehicleId().getPrice());
        return dailyPrice.multiply(BigDecimal.valueOf(rentalDays));
    }


}
