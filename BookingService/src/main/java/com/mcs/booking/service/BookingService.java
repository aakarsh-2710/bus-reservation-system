package com.mcs.booking.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcs.booking.entity.Booking;
import com.mcs.booking.entity.Passenger;
import com.mcs.booking.model.BookingCreatedEvent;
import com.mcs.booking.model.BookingRequest;
import com.mcs.booking.model.CancelBookingEvent;
import com.mcs.booking.model.InventoryEvent;
import com.mcs.booking.producer.CancelBookingProducer;
import com.mcs.booking.repository.BookingRepository;
import com.mcs.booking.repository.PassengerRepository;

@Service
public class BookingService {

	private final BookingRepository bookingRepository;
	private final PassengerRepository passengerRepository;
	private final RestTemplate restTemplate;
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;
	private final String inventoryUrl; // from config
	private CancelBookingProducer bookingProducer;

	public BookingService(BookingRepository bookingRepository, PassengerRepository passengerRepository,
			RestTemplate restTemplate, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper,
			@Value("${inventory.service.url}") String inventoryUrl, CancelBookingProducer bookingProducer) {
		this.bookingRepository = bookingRepository;
		this.passengerRepository = passengerRepository;
		this.restTemplate = restTemplate;
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
		this.inventoryUrl = inventoryUrl;
		this.bookingProducer = bookingProducer;
	}

	public Booking createBooking(BookingRequest req) {

		ResponseEntity<Integer> response;
		try {

			// Build the URL with the query parameter
			UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(inventoryUrl).queryParam("busId",
					req.getBusId());

			// Make the GET request and get the full response entity
			response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, null, Integer.class);
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Inventory service unavailable");
		}
		Integer available = response.getBody();
		if (available < req.getNumSeats()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Not enough seats");
		}

		// 2) persist booking and passengers
		Booking booking = new Booking();
		booking.setBusId(req.getBusId());
		booking.setNumSeats(req.getNumSeats());
		booking.setSource(req.getSource());
		booking.setDestination(req.getDestination());
		booking.setStatus("PENDING");
		Booking saved = bookingRepository.save(booking);

		if (req.getPassengers() != null) {
			for (var p : req.getPassengers()) {
				Passenger passenger = new Passenger();
				passenger.setName(p.getName());
				passenger.setAge(p.getAge());
				passenger.setPhone(p.getPhone());
				passenger.setBooking(saved);
				passengerRepository.save(passenger);
			}
		}

		// 3) publish event to Kafka
		BookingCreatedEvent event = new BookingCreatedEvent();
		event.setBookingNumber(saved.getBookingNumber());
		event.setBusId(saved.getBusId());
		event.setNumSeats(saved.getNumSeats());
		try {
			String payload = objectMapper.writeValueAsString(event);
			kafkaTemplate.send("booking.created", saved.getBookingNumber(), payload);
		} catch (JsonProcessingException e) {
			// log and continue - event publishing can be retried later by an outbox pattern
			throw new RuntimeException("Failed to serialize event", e);
		}

		return saved;
	}

	public Booking getBookingDetails(String bookingNumber) {
		return bookingRepository.findByBookingNumber(bookingNumber)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
	}

	public void updateBookingStatusFromInventory(InventoryEvent event) {
		Booking booking = bookingRepository.findByBookingNumber(event.getBookingNo())
				.orElseThrow(() -> new RuntimeException("Booking not found"));

		if ("CONFIRMED".equals(event.getStatus())) {
			booking.setStatus("CONFIRMED");
		} else if ("REJECTED".equals(event.getStatus())) {
			booking.setStatus("CANCELLED");
		} else if ("CANCELLED".equals(event.getStatus())) {
			booking.setStatus("CANCELLED");
		}
		bookingRepository.save(booking);
	}

	// BookingService.java
	public CancelBookingEvent cancelBooking(Long bookingId) {
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new RuntimeException("Booking not found"));

		if (!"CONFIRMED".equals(booking.getStatus())) {
			throw new IllegalStateException("Only CONFIRMED bookings can be cancelled");
		}

		booking.setStatus("CANCEL_PENDING");
		bookingRepository.save(booking);

		CancelBookingEvent event = new CancelBookingEvent();
		event.setBookingNo(booking.getBookingNumber());
		event.setBusId(booking.getBusId());
		event.setNoOfSeats(booking.getNumSeats());
		event.setStatus("CANCEL_PENDING");

		// Publish event
		bookingProducer.publish(event);

		return event;
	}

}
