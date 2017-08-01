package com.github.mdjc.domain;

import java.time.LocalDate;

import com.github.mdjc.commons.args.Arguments;

public class Bill {
	private final long id;
	private final String description;
	private final LocalDate dueDate;
	private final double dueAmount;
	private final PaymentStatus paymentStatus;
	private final PaymentMethod paymentMethod;
	private final LocalDate lastUpdateOn;
	private final ImageExtension proofOfPaymentExtension;

	public Bill(long id, String description, LocalDate dueDate, double dueAmount, PaymentStatus paymentStatus,
			LocalDate lastUpdateOn, PaymentMethod paymentMethod, ImageExtension proofOfPaymentExtension) {
		this.id = id;
		this.description = Arguments.checkBlank(description, "Description should not be blank");
		this.dueDate = Arguments.checkNull(dueDate, "DueDate should not be null");
		this.dueAmount = Arguments.checkPositive(dueAmount, "DueAmount should be positive");
		this.paymentStatus = paymentStatus;
		this.lastUpdateOn = lastUpdateOn;
		this.paymentMethod = paymentMethod;
		this.proofOfPaymentExtension = proofOfPaymentExtension;
	}

	public Bill(long id, String description, LocalDate dueDate, double dueAmount, PaymentStatus paymentStatus,
			LocalDate lastUpdateOn) {
		this(id, description, dueDate, dueAmount, paymentStatus, lastUpdateOn, null, null);
	}
	
	public Bill(long id, String description, LocalDate dueDate, double dueAmount) {
		this(id, description, dueDate, dueAmount, null, null, null, null);
	}

	public long getId() {
		return id;
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

	public LocalDate getLastUpdateOn() {
		return lastUpdateOn;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}
	
	public ImageExtension getProofOfPaymentExtension() {
		return proofOfPaymentExtension;
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

		return this.id == ((Bill) other).id;
	}
}
