package com.mcs.inventory.model;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public class BusInventoryDTO {

	@NotNull
	private Integer busId;
	@NotNull
	private Integer availableSeats;
	@NotNull
	private LocalDateTime lastUpdated;

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
