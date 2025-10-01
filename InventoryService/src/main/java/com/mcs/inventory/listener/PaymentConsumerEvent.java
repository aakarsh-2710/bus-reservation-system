package com.mcs.inventory.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcs.inventory.model.PaymentEvent;
import com.mcs.inventory.service.InventoryService;

@Component
public class PaymentConsumerEvent {

	private InventoryService inventoryService;
	private ObjectMapper objectMapper = new ObjectMapper();

	public PaymentConsumerEvent(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	@KafkaListener(topics = "payment.events", groupId = "inventory-service")
	public void consume(@Payload String jsonValue) {
		try {
			PaymentEvent paymentEvent = convertJsonToObject(jsonValue, PaymentEvent.class);
			inventoryService.processPaymentEvent(paymentEvent);
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
