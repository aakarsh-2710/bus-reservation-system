package com.mcs.booking.listener;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.mcs.booking.model.InventoryEvent;
import com.mcs.booking.service.BookingService;

@Component
public class InventoryEventConsumer {

    private final BookingService bookingService;

    public InventoryEventConsumer(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @KafkaListener(topics = "inventory-events", groupId = "booking-service")
    public void consume(InventoryEvent event) {
        bookingService.updateBookingStatusFromInventory(event);
    }
}
