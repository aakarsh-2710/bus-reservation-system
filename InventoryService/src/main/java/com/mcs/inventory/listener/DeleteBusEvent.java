package com.mcs.inventory.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcs.inventory.model.BusDeleteDTO;
import com.mcs.inventory.service.InventoryService;

@Component
public class DeleteBusEvent {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private InventoryService inventoryService;

	public DeleteBusEvent(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	@KafkaListener(topics = "bus.delete.event", groupId = "inventory-service")
	public void consume(@Payload String jsonValue) {
		try {
			BusDeleteDTO event = convertJsonToObject(jsonValue, BusDeleteDTO.class);
			inventoryService.deleteBus(event.getBusId());
		} catch (RuntimeException e) {
			// Loggers
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
