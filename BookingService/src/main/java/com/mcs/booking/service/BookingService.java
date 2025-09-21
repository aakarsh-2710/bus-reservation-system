package com.mcs.booking.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcs.booking.entity.Booking;
import com.mcs.booking.entity.Passenger;
import com.mcs.booking.exception.SeatsNotAvailableException;
import com.mcs.booking.model.BookingCreatedEvent;
import com.mcs.booking.model.BookingRequest;
import com.mcs.booking.model.PassengerDto;
import com.mcs.booking.producer.CancelBookingProducer;
import com.mcs.booking.repository.BookingRepository;
import com.mcs.booking.repository.PassengerRepository;
import com.mcs.booking.util.MessageConstant;

@Service
@Transactional
public class BookingService {

	private final BookingRepository bookingRepository;
	private final PassengerRepository passengerRepository;
	private final RestTemplate restTemplate;
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;
	private CancelBookingProducer bookingProducer;

	public BookingService(BookingRepository bookingRepository, PassengerRepository passengerRepository,
			RestTemplate restTemplate, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper,
			CancelBookingProducer bookingProducer) {
		this.bookingRepository = bookingRepository;
		this.passengerRepository = passengerRepository;
		this.restTemplate = restTemplate;
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
		this.bookingProducer = bookingProducer;
	}

	public Booking createBooking(BookingRequest bookingRequest) {

		// check for seats availability first
		String url = "http://INVENTORY-SERVICE/inventory/getAvailableSeats/" + bookingRequest.getBusId();
		Integer availableSeats = restTemplate.getForObject(url, Integer.class);

		// Throw Exception if Requested seats are greater than available seats
		if (availableSeats < bookingRequest.getNumSeats()) {
			throw new SeatsNotAvailableException(
					String.format(MessageConstant.SEATS_NOT_AVAILABLE, bookingRequest.getNumSeats(), availableSeats));
		}
		// persist booking
		Booking bookingSaved = persistBooking(bookingRequest);

		// persist All passengers
		persistPassengers(bookingRequest.getPassengers(), bookingSaved);

		// publish booking to kafka
		publishBookingToKafka(bookingSaved);

		return bookingSaved;
	}

	private void publishBookingToKafka(Booking bookingSaved) {

		BookingCreatedEvent event = new BookingCreatedEvent();
		event.setBookingId(bookingSaved.getBookingId());
		event.setBusId(bookingSaved.getBusId());
		event.setNumSeats(bookingSaved.getNumSeats());
		try {
			String payload = objectMapper.writeValueAsString(event);
			kafkaTemplate.send("booking.created", bookingSaved.getBookingId().toString(), payload);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Failed to serialize event", e);
		}

	}

	private void persistPassengers(List<PassengerDto> passengers, Booking bookingSaved) {
		List<Passenger> passengerList = new ArrayList<>();
		for (var p : passengers) {
			Passenger passenger = new Passenger();
			passenger.setName(p.getName());
			passenger.setAge(p.getAge());
			passenger.setPhone(p.getPhone());
			passenger.setBooking(bookingSaved);
			passengerList.add(passenger);
		}
		passengerRepository.saveAll(passengerList);

	}

	private Booking persistBooking(BookingRequest bookingRequest) {
		Booking booking = new Booking();
		booking.setBusId(bookingRequest.getBusId());
		booking.setNumSeats(bookingRequest.getNumSeats());
		booking.setSource(bookingRequest.getSource());
		booking.setDestination(bookingRequest.getDestination());
		booking.setBookingDate(LocalDateTime.now());
		booking.setStatus("PENDING");
		return bookingRepository.save(booking);

	}

//	public Booking getBookingDetails(String bookingNumber) {
//		return bookingRepository.findByBookingNumber(bookingNumber)
//				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
//	}

//	public void updateBookingStatusFromInventory(InventoryEvent event) {
//		Booking booking = bookingRepository.findByBookingNumber(event.getBookingNo())
//				.orElseThrow(() -> new RuntimeException("Booking not found"));
//
//		if ("CONFIRMED".equals(event.getStatus())) {
//			booking.setStatus("CONFIRMED");
//		} else if ("REJECTED".equals(event.getStatus())) {
//			booking.setStatus("CANCELLED");
//		} else if ("CANCELLED".equals(event.getStatus())) {
//			booking.setStatus("CANCELLED");
//		}
//		bookingRepository.save(booking);
//	}

	// BookingService.java
//	public CancelBookingEvent cancelBooking(Long bookingId) {
//		Booking booking = bookingRepository.findById(bookingId)
//				.orElseThrow(() -> new RuntimeException("Booking not found"));
//
//		if (!"CONFIRMED".equals(booking.getStatus())) {
//			throw new IllegalStateException("Only CONFIRMED bookings can be cancelled");
//		}
//
//		booking.setStatus("CANCEL_PENDING");
//		bookingRepository.save(booking);
//
//		CancelBookingEvent event = new CancelBookingEvent();
//		event.setBookingId(booking.getBookingId());
//		event.setBusId(booking.getBusId());
//		event.setNoOfSeats(booking.getNumSeats());
//		event.setStatus("CANCEL_PENDING");
//
//		// Publish event
//		bookingProducer.publish(event);
//
//		return event;
//	}

}
