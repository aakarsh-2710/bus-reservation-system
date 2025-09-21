package com.mcs.booking.model;

import java.util.UUID;

public class CancelBookingEvent {
	private UUID bookingId;
	private Integer busId;
	private int noOfSeats;
	private String status; // CANCEL_PENDING / CANCELLED

	public UUID getBookingId() {
		return bookingId;
	}

	public void setBookingId(UUID bookingId) {
		this.bookingId = bookingId;
	}

	public Integer getBusId() {
		return busId;
	}

	public void setBusId(Integer busId) {
		this.busId = busId;
	}

	public int getNoOfSeats() {
		return noOfSeats;
	}

	public void setNoOfSeats(int noOfSeats) {
		this.noOfSeats = noOfSeats;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
