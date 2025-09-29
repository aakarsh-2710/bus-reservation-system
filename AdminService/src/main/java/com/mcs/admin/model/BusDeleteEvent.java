package com.mcs.admin.model;

public class BusDeleteEvent {
	private Integer busId;
	private String reason;

	public BusDeleteEvent(Integer busId, String reason) {
		this.busId = busId;
		this.reason = reason;
	}

	public Integer getBusId() {
		return busId;
	}

	public void setBusId(Integer busId) {
		this.busId = busId;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

}
