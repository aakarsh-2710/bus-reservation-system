package com.mcs.booking.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.mcs.booking.model.BusDetail;
import com.mcs.booking.model.CancelBookingEvent;
import com.mcs.booking.model.InventoryEvent;
import com.mcs.booking.model.PassengerDto;
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

	public BookingService(BookingRepository bookingRepository, PassengerRepository passengerRepository,
			RestTemplate restTemplate, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
		this.bookingRepository = bookingRepository;
		this.passengerRepository = passengerRepository;
		this.restTemplate = restTemplate;
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
	}

	public Booking createBooking(BookingRequest bookingRequest) {

		// Fetching bus details from admin service
		String adminUrl = "http://ADMIN-SERVICE/admin/getBus/" + bookingRequest.getBusId();
		ResponseEntity<BusDetail> response = restTemplate.getForEntity(adminUrl, BusDetail.class);

		if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
			throw new IllegalArgumentException("Invalid busId: " + bookingRequest.getBusId());
		}

		BusDetail busDetail = response.getBody();

		// check for seats availability first
		String inventoryUrl = "http://INVENTORY-SERVICE/inventory/getAvailableSeats/" + bookingRequest.getBusId();
		Integer availableSeats = restTemplate.getForObject(inventoryUrl, Integer.class);

		// Throw Exception if Requested seats are greater than available seats
		if (availableSeats < bookingRequest.getNumSeats()) {
			throw new SeatsNotAvailableException(
					String.format(MessageConstant.SEATS_NOT_AVAILABLE, bookingRequest.getNumSeats(), availableSeats));
		}

		// persist booking
		Booking bookingSaved = persistBooking(bookingRequest, busDetail);

		// persist All passengers
		persistPassengers(bookingRequest.getPassengers(), bookingSaved);

		// publish booking to kafka
		publishBookingToKafka(bookingSaved, busDetail);

		return bookingSaved;
	}

	private void publishBookingToKafka(Booking bookingSaved, BusDetail busDetail) {

		BookingCreatedEvent event = new BookingCreatedEvent();
		event.setBookingId(bookingSaved.getBookingId());
		event.setBusId(bookingSaved.getBusId());
		event.setNumSeats(bookingSaved.getNumSeats());
		event.setTotalAmount(bookingSaved.getNumSeats() * busDetail.getPrice());
		try {
			String payload = objectMapper.writeValueAsString(event);
			kafkaTemplate.send("booking.event", bookingSaved.getBookingId().toString(), payload);
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

	private Booking persistBooking(BookingRequest bookingRequest, BusDetail busDetail) {
		Booking booking = new Booking();
		booking.setBusId(bookingRequest.getBusId());
		booking.setNumSeats(bookingRequest.getNumSeats());
		booking.setSource(busDetail.getSource());
		booking.setDestination(busDetail.getDestination());
		booking.setBookingDate(LocalDateTime.now());
		booking.setStatus("PENDING");
		return bookingRepository.save(booking);

	}

	public Booking getBookingDetails(UUID bookingId) {

		return bookingRepository.findById(bookingId).orElseThrow(
				() -> new ResourceNotFoundException(String.format(MessageConstant.BOOKING_NOT_FOUND, bookingId)));
	}

	public void updateBookingStatus(InventoryEvent event) {
		Booking booking = bookingRepository.findById(event.getBookingId())
				.orElseThrow(() -> new RuntimeException("Booking not found"));

		if ("CONFIRMED".equals(event.getStatus())) {
			booking.setStatus("CONFIRMED");
		} else if ("REJECTED".equals(event.getStatus())) {
			booking.setStatus("CANCELLED");
		}
		bookingRepository.save(booking);
	}

	public CancelBookingEvent cancelBooking(UUID bookingId) throws JsonProcessingException {
		Booking booking = bookingRepository.findById(bookingId).orElseThrow(
				() -> new ResourceNotFoundException(String.format(MessageConstant.BOOKING_NOT_FOUND, bookingId)));

		if (!"CONFIRMED".equals(booking.getStatus())) {
			throw new IllegalStateException("Only CONFIRMED bookings can be cancelled");
		}

		booking.setStatus("CANCEL");
		bookingRepository.save(booking);

		CancelBookingEvent event = new CancelBookingEvent();
		event.setBookingId(booking.getBookingId());
		event.setBusId(booking.getBusId());
		event.setNoOfSeats(booking.getNumSeats());
		event.setStatus("CANCEL");

		// Publish event
		String payload = objectMapper.writeValueAsString(event);
		kafkaTemplate.send("cancel.booking.events", event.getBookingId().toString(), payload);

		return event;
	}

}
