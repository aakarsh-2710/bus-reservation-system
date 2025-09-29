package com.mcs.admin.model;

import java.time.LocalDateTime;

public class BusAddDTO {

	private Integer busId;
	private Integer availableSeats;
	private LocalDateTime lastUpdated;

	public BusAddDTO(Integer busId, Integer availableSeats, LocalDateTime lastUpdated) {
		this.busId = busId;
		this.availableSeats = availableSeats;
		this.lastUpdated = lastUpdated;
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

	public LocalDateTime getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(LocalDateTime lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

}
