package com.github.mdjc.domain;

import java.time.LocalDate;

public class Bill {
	private final long id;
	private final Apartment apartment;
	private final String description;
	private final LocalDate dueDate;
	private final double dueAmount;
	private final PaymentStatus paymentStatus;
	private final PaymentMethod paymentMethod;
	private final LocalDate lastUpdateOn;	
	
	public Bill(long id, Apartment apartment, String description, LocalDate dueDate, double dueAmount,
			PaymentStatus paymentStatus, PaymentMethod paymentMethod, LocalDate lastUpdateOn) {
		this.id = id;
		this.apartment = apartment;
		this.description = description;
		this.dueDate = dueDate;
		this.dueAmount = dueAmount;
		this.paymentStatus = paymentStatus;
		this.paymentMethod = paymentMethod;
		this.lastUpdateOn = lastUpdateOn;
	}

	public long getId() {
		return id;
	}

	public Apartment getApartment() {
		return apartment;
	}

	public String getDescription() {
		return description;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public double getDueAmount() {
		return dueAmount;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public LocalDate getLastUpdateOn() {
		return lastUpdateOn;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(id);
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Bill)) {
			return false;
		}
		
		return this.id == ((Bill)other).id;
	}
}
