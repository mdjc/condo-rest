package com.github.mdjc.domain;

import java.time.LocalDate;

public class CondoBill extends Bill {
	private final Apartment apartment;

	public CondoBill(long id, String description, LocalDate dueDate, double dueAmount, PaymentStatus paymentStatus,
			LocalDate lastUpdateOn, PaymentMethod paymentMethod, ProofOfPaymentExtension proofOfPaymentExtension, Apartment apartment) {
		super(id, description, dueDate, dueAmount, paymentStatus, lastUpdateOn, paymentMethod, proofOfPaymentExtension);
		this.apartment = apartment;
	}

	public Apartment getApartment() {
		return apartment;
	}
}
