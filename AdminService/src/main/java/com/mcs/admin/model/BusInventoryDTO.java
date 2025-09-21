package com.mcs.admin.model;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public class BusInventoryDTO {

	@NotNull
	private Integer busId;
	@NotNull
	private Integer availableSeats;
	@NotNull
	private LocalDateTime lastUpdated;

	public BusInventoryDTO(@NotNull Integer busId, @NotNull Integer availableSeats,
			@NotNull LocalDateTime lastUpdated) {
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
