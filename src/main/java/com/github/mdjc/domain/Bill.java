package com.github.mdjc.domain;

import java.time.LocalDate;

public class Bill {
	private final long id;
	private final String description;
	private final LocalDate dueDate;
	private final double dueAmount;
	private final PaymentStatus paymentStatus;
	private final PaymentMethod paymentMethod;
	private final LocalDate lastUpdateOn;
	private final ProofOfPaymentExtension proofOfPaymentExtension;

	public Bill(long id, String description, LocalDate dueDate, double dueAmount, PaymentStatus paymentStatus,
			LocalDate lastUpdateOn, PaymentMethod paymentMethod, ProofOfPaymentExtension proofOfPaymentExtension) {
		this.id = id;
		this.description = description;
		this.dueDate = dueDate;
		this.dueAmount = dueAmount;
		this.paymentStatus = paymentStatus;
		this.paymentMethod = paymentMethod;
		this.lastUpdateOn = lastUpdateOn;
		this.proofOfPaymentExtension = proofOfPaymentExtension;
	}

	public Bill(long id, String description, LocalDate dueDate, double dueAmount, PaymentStatus paymentStatus,
			LocalDate lastUpdateOn) {
		this(id, description, dueDate, dueAmount, paymentStatus, lastUpdateOn, null, null);
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
	
	public ProofOfPaymentExtension getProofOfPaymentExtension() {
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
