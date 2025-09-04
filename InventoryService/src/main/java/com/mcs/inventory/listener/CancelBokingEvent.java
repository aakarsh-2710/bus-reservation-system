package com.mcs.inventory.listener;

import org.springframework.kafka.annotation.KafkaListener;

import com.mcs.inventory.model.CancelBookingEvent;
import com.mcs.inventory.service.InventoryService;

public class CancelBokingEvent {

	private InventoryService inventoryService;

	public CancelBokingEvent(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	@KafkaListener(topics = "cancel-booking-events", groupId = "inventory-service")
	public void consumeCancel(CancelBookingEvent event) {
		inventoryService.processCancelBooking(event);
	}
}
