package com.mcs.payment.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcs.payment.model.BookingEvent;
import com.mcs.payment.service.PaymentService;

@Component
public class BookingEventConsumer {

	private final PaymentService service;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public BookingEventConsumer(PaymentService service) {
		this.service = service;
	}

	@KafkaListener(topics = "booking.event", groupId = "payment-service")
	public void consume(@Payload String jsonValue) {
		try {
			BookingEvent bookingEvent = convertJsonToObject(jsonValue, BookingEvent.class);
			service.processPayment(bookingEvent);
		} catch (Exception e) {
// Loggers
		}
	}

	// Generic method to convert JSON string to any object type
	private <T> T convertJsonToObject(String jsonString, Class<T> targetClass) {
		try {
			return objectMapper.readValue(jsonString, targetClass);
		} catch (Exception e) {
			throw new RuntimeException("JSON conversion failed", e);
		}
	}
}
