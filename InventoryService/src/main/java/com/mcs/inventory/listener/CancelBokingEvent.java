package com.mcs.inventory.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcs.inventory.model.CancelBookingEvent;
import com.mcs.inventory.service.InventoryService;

public class CancelBokingEvent {

	private InventoryService inventoryService;
	private ObjectMapper objectMapper = new ObjectMapper();
	
	public CancelBokingEvent(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	@KafkaListener(topics = "cancel-booking-events", groupId = "inventory-service")
	public void consumeCancel(@Payload String jsonValue) {
		try {
			CancelBookingEvent cancelBookingEvent = convertJsonToObject(jsonValue, CancelBookingEvent.class);
			inventoryService.processCancelBooking(cancelBookingEvent);
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
