package com.mcs.payment.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.mcs.payment.model.BookingEvent;
import com.mcs.payment.service.PaymentService;

@Component
public class BookingEventConsumer {

	private final PaymentService service;

	public BookingEventConsumer(PaymentService service) {
		this.service = service;
	}

	@KafkaListener(topics = "booking-events", groupId = "payment-service")
	public void consume(BookingEvent event) {
		service.processPayment(event);
	}
}
