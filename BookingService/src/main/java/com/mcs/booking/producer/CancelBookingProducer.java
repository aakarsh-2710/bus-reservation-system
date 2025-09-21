package com.mcs.booking.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.mcs.booking.model.CancelBookingEvent;

@Component
public class CancelBookingProducer {

	private KafkaTemplate<String, CancelBookingEvent> kafkaTemplate;

	public void publish(CancelBookingEvent event) {
		kafkaTemplate.send("cancel-booking-events", event.getBookingId().toString(), event);
	}
}
