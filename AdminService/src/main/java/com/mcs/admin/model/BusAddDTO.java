package com.mcs.admin.model;

public class BusAddDTO {

	private Integer busId;
	private Integer availableSeats;

	public BusAddDTO(Integer busId, Integer availableSeats) {
		this.busId = busId;
		this.availableSeats = availableSeats;
	}

	public Integer getBusId() {
		return busId;
	}

	public void setBusId(Integer busId) {
		this.busId = busId;
	}

	public Integer getAvailableSeats() {
		return availableSeats;
	}

	public void setAvailableSeats(Integer availableSeats) {
		this.availableSeats = availableSeats;
	}

}
