package com.mcs.inventory.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcs.inventory.model.BusAddDTO;
import com.mcs.inventory.service.InventoryService;

@Component
public class AddBusEvent {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private InventoryService inventoryService;

	public AddBusEvent(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	@KafkaListener(topics = "bus.add.event", groupId = "inventory-service")
	public void consume(@Payload String jsonValue) {
		try {
			BusAddDTO event = convertJsonToObject(jsonValue, BusAddDTO.class);
			inventoryService.addInventory(event);
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
