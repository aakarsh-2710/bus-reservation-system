package com.mcs.payment.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Payment {

	@Id
	@UuidGenerator
	private UUID paymentId;

	@Column(nullable = false)
	private UUID bookingId;

	@Column(nullable = false)
	private Integer busId;

	@Column(nullable = false)
	private double amount;

	@Column(nullable = false)
	private LocalDateTime dateOfPayment;

	@Column(nullable = false)
	private String status; // SUCCESS / FAILED

	public UUID getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(UUID paymentId) {
		this.paymentId = paymentId;
	}

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

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public LocalDateTime getDateOfPayment() {
		return dateOfPayment;
	}

	public void setDateOfPayment(LocalDateTime dateOfPayment) {
		this.dateOfPayment = dateOfPayment;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
