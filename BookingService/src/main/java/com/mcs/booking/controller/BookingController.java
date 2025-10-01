package com.mcs.booking.controller;

import java.util.UUID;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mcs.booking.entity.Booking;
import com.mcs.booking.exception.SeatsNotAvailableException;
import com.mcs.booking.model.BookingRequest;
import com.mcs.booking.service.BookingService;
import com.mcs.booking.util.MessageConstant;
import com.mcs.booking.util.ResponseTemplate;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/booking")
public class BookingController {

	private final BookingService bookingService;

	public BookingController(BookingService bookingService) {
		this.bookingService = bookingService;
	}

	@PostMapping("/create")
	public ResponseEntity<?> createBooking(@Valid @RequestBody BookingRequest bookingRequest) {
		Booking booking = null;
		try {
			booking = bookingService.createBooking(bookingRequest);
		} catch (SeatsNotAvailableException e) {
			return ResponseTemplate.errorMsg(e.getMessage(), HttpStatus.CONFLICT);
		} catch (Exception e) {
			return ResponseTemplate.errorMsg(MessageConstant.TECHNICAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return ResponseTemplate.successMsg(
				String.format(MessageConstant.BOOKING_PENDING, booking.getBookingId(), booking.getStatus()));

	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<?> getBookingById(@PathVariable UUID bookingId) {
		Booking bookingDetails = null;
		try {
			bookingDetails = bookingService.getBookingDetails(bookingId);

		} catch (ResourceNotFoundException e) {
			return ResponseTemplate.errorMsg(e.getMessage(), HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return ResponseTemplate.errorMsg(MessageConstant.TECHNICAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return ResponseTemplate.successMsg(bookingDetails);
	}

	@PostMapping("/cancel/{bookingId}")
	public ResponseEntity<?> cancelBooking(@PathVariable UUID bookingId) {

		try {
			bookingService.cancelBooking(bookingId);

		} catch (JsonProcessingException | IllegalStateException e) {
			return ResponseTemplate.errorMsg(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception e) {
			return ResponseTemplate.errorMsg(MessageConstant.TECHNICAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return ResponseEntity.status(201).body("Booking Canceled");
	}

}
