package com.mcs.booking.model;

import java.util.UUID;

public class InventoryEvent {

	private UUID bookingId;
	private Integer busId;
	private String status; // CONFIRMED / REJECTED

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
