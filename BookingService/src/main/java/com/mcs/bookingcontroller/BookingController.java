package com.mcs.bookingcontroller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mcs.booking.entity.Booking;
import com.mcs.booking.model.BookingRequest;
import com.mcs.booking.service.BookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/booking")
public class BookingController {

	private final BookingService bookingService;

	public BookingController(BookingService bookingService) {
		this.bookingService = bookingService;
	}

	@PostMapping("/create")
	public ResponseEntity<Map<String, Object>> createBooking(@Valid @RequestBody BookingRequest req) {
		Booking saved = bookingService.createBooking(req);
		return ResponseEntity.status(201)
				.body(Map.of("bookingNumber", saved.getBookingNumber(), "status", saved.getStatus()));
	}

	@GetMapping("/{bookingId}")
	public Booking getBookingById(@PathVariable String bookingId) {
		return bookingService.getBookingDetails(bookingId);
	}

	@PostMapping("/cancel")
	public ResponseEntity<?> cancelBooking(@RequestParam Long bookingId) {
		bookingService.cancelBooking(bookingId);
		return ResponseEntity.status(201).body("Booking Canceled");
	}

}
