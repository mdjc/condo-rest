package com.github.mdjc.domain;

import com.github.mdjc.commons.args.Arguments;

public class Apartment {
	private final String name;
	private final User resident;
		
	public Apartment(String name, User resident) {
		this.name = Arguments.checkBlank(name);
		this.resident = resident;
	}
	
	public Apartment(String name) {
		this(name, null);
	}	
	
	public String getName() {
		return name;
	}
	
	public User getResident() {
		return resident;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Apartment)) {
			return false;
		}
		
		return this.name.equals(((Apartment)other).getName());
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
