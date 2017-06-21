package com.github.mdjc.domain;

public class BuildingStats {
	private final Building building;
	private final int apartmentCount;
	private final int residentCount;
	private final double balance;
	
	public BuildingStats(Building building, int apartmentCount, int residentCount, double balance) {
		this.building = building;
		this.apartmentCount = apartmentCount;
		this.residentCount = residentCount;
		this.balance = balance;
	}

	public Building getBuilding() {
		return building;
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
