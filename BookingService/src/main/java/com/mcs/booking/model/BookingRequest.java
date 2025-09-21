package com.mcs.booking.model;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BookingRequest {

	@NotNull
	private Integer busId;
	@NotNull
	@Min(1)
	private Integer numSeats;
	@NotBlank
	private String source;
	@NotBlank
	private String destination;
	@NotNull
	private List<PassengerDto> passengers;

	public Integer getBusId() {
		return busId;
	}

	public void setBusId(Integer busId) {
		this.busId = busId;
	}

	public Integer getNumSeats() {
		return numSeats;
	}

	public void setNumSeats(Integer numSeats) {
		this.numSeats = numSeats;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public List<PassengerDto> getPassengers() {
		return passengers;
	}

	public void setPassengers(List<PassengerDto> passengers) {
		this.passengers = passengers;
	}

}
