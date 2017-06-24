package com.github.mdjc.domain;

public class BuildingStats {
	private final int apartmentCount;
	private final int residentCount;
	private final double balance;
	
	public BuildingStats(int apartmentCount, int residentCount, double balance) {
		this.apartmentCount = apartmentCount;
		this.residentCount = residentCount;
		this.balance = balance;
	}

	public int getApartmentCount() {
		return apartmentCount;
	}

	public int getResidentCount() {
		return residentCount;
	}

	public double getBalance() {
		return balance;
	}
}
