package com.github.mdjc.domain;

public class Building {
	private final String name;
	private final String address;
	
	public Building(String name, String address) {
		this.name = name;
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null) return false;
		if (!(other instanceof Building)) return false;
		
		return this.name.equals(((Building)other).name);
	}
	
	@Override
	public int hashCode() {
		return this.name.hashCode();
	}
}
