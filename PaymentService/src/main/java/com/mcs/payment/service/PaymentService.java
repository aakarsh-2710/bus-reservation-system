package com.mcs.payment.service;



import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.mcs.payment.entity.Payment;
import com.mcs.payment.model.BookingEvent;
import com.mcs.payment.model.PaymentEvent;
import com.mcs.payment.producer.PaymentEventProducer;
import com.mcs.payment.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository repo;
    private final PaymentEventProducer producer;

    public PaymentService(PaymentRepository repo, PaymentEventProducer producer) {
        this.repo = repo;
        this.producer = producer;
    }

    public void processPayment(BookingEvent event) {
        Payment payment = new Payment();
        payment.setPaymentNo(UUID.randomUUID().toString());
        payment.setBookingNo(event.getBookingNo());
        payment.setBusNo(event.getBusNo());
        payment.setDateOfPayment(LocalDateTime.now());

        // 💡 Here we can integrate with real payment gateway later
        payment.setStatus("SUCCESS");
        payment.setAmount(event.getNoOfSeats() * event.getPrice());

        repo.save(payment);

        PaymentEvent paymentEvent = new PaymentEvent();
        paymentEvent.setBookingNo(event.getBookingNo());
        paymentEvent.setBusNo(event.getBusNo());
        paymentEvent.setStatus(payment.getStatus());
        paymentEvent.setAmount(payment.getAmount());

        producer.publish(paymentEvent);
    }
}
