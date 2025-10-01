package com.mcs.payment.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.mcs.payment.entity.Payment;
import com.mcs.payment.model.BookingEvent;
import com.mcs.payment.model.PaymentEvent;
import com.mcs.payment.repository.PaymentRepository;

@Service
public class PaymentService {

	private final PaymentRepository repo;
	private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

	public PaymentService(PaymentRepository repo, KafkaTemplate<String, PaymentEvent> kafkaTemplate) {
		this.repo = repo;
		this.kafkaTemplate = kafkaTemplate;
	}

	public void processPayment(BookingEvent event) {
		Payment payment = new Payment();
		UUID paymentId =  UUID.randomUUID();
		payment.setPaymentId(paymentId);
		payment.setBookingId(event.getBookingId());
		payment.setBusId(event.getBusId());
		payment.setDateOfPayment(LocalDateTime.now());

		// 💡 Here we can integrate with real payment gateway later
		payment.setStatus("SUCCESS");
		payment.setAmount(event.getTotalAmount());
		repo.save(payment);

		PaymentEvent paymentEvent = new PaymentEvent();
		paymentEvent.setPaymentId(paymentId);
		paymentEvent.setBookingId(event.getBookingId());
		paymentEvent.setBusId(event.getBusId());
		paymentEvent.setStatus(payment.getStatus());
		paymentEvent.setAmount(payment.getAmount());

		kafkaTemplate.send("payment.events", paymentId.toString(), paymentEvent);
	}
}
