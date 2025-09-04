package com.mcs.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bus_detail")
public class BusDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long busId;

	@Column(unique = true, nullable = false)
	private String busNumber;

	@Column(nullable = false)
	private String source;

	@Column(nullable = false)
	private String destination;

	@Column(nullable = false)
	private Integer totalSeats;

	@Column(nullable = false)
	private Double price;

	public BusDetail() {
	}

	public BusDetail(String busNumber, String source, String destination, Integer totalSeats, Double price) {
		this.busNumber = busNumber;
		this.source = source;
		this.destination = destination;
		this.totalSeats = totalSeats;
		this.price = price;
	}

	public Long getBusId() {
		return busId;
	}

	public void setBusId(Long busId) {
		this.busId = busId;
	}

	public String getBusNumber() {
		return busNumber;
	}

	public void setBusNumber(String busNumber) {
		this.busNumber = busNumber;
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

	public Integer getTotalSeats() {
		return totalSeats;
	}

	public void setTotalSeats(Integer totalSeats) {
		this.totalSeats = totalSeats;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

}
