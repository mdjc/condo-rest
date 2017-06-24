package com.github.mdjc.domain;

import java.time.LocalDate;

public class Payment {
	private final long id;
	private final Apartment apartment;
	private final double amount;
	private final PaymentMethod method;
	private final LocalDate createdOn;
	private final User createdBy;
	private PaymentStatus status;
	
	public Payment(long id, Apartment apartment, double amount, PaymentMethod method, LocalDate createdOn,
			User createdBy, PaymentStatus status) {
		this.id = id;
		this.apartment = apartment;
		this.amount = amount;
		this.method = method;
		this.createdOn = createdOn;
		this.createdBy = createdBy;
		this.status = status;
	}

	public long getId() {
		return id;
	}

	public Apartment getApartment() {
		return apartment;
	}

	public double getAmount() {
		return amount;
	}

	public PaymentMethod getMethod() {
		return method;
	}
	
	public LocalDate getCreatedOn() {
		return createdOn;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public PaymentStatus getStatus() {
		return status;
	}

	public void setStatus(PaymentStatus status) {
		this.status = status;
	}
	
	@Override
	public int hashCode() {
		return Long.hashCode(id);
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Payment)) {
			return false;
		}
		
		return this.id == ((Payment)other).id;
	}
}
