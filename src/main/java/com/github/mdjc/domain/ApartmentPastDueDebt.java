package com.github.mdjc.domain;

public class ApartmentPastDueDebt {
	private final Apartment apartment;
	private final double debt;
	
	public ApartmentPastDueDebt(Apartment apartment, double debt) {
		this.apartment = apartment;
		this.debt = debt;
	}

	public Apartment getApartment() {
		return apartment;
	}

	public double getDebt() {
		return debt;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ApartmentPastDueDebt)) {
			return false;
		}
		
		ApartmentPastDueDebt other = (ApartmentPastDueDebt) obj;
		return this.apartment.equals(other.getApartment()) && this.debt == other.debt;
	}
	
	@Override
	public int hashCode() {
		return this.apartment.hashCode();
	}
}
