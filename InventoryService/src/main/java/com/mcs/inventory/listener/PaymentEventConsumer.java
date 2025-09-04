package com.mcs.inventory.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.mcs.inventory.model.PaymentEvent;
import com.mcs.inventory.service.InventoryService;

@Component
public class PaymentEventConsumer {

	private final InventoryService inventoryService;

	public PaymentEventConsumer(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	@KafkaListener(topics = "payment-events", groupId = "inventory-service")
	public void consume(PaymentEvent event) {
		inventoryService.processPaymentEvent(event);
	}
}
