package com.github.mdjc.domain;

import com.github.mdjc.commons.args.Arguments;

public class Building {
	private final long id;
	private final String name;
	private final User manager;
	
	public Building(long id, String name, User manager) {
		this.id = Arguments.checkPositive(id);
		this.name = Arguments.checkBlank(name);
		this.manager = Arguments.checkNull(manager);
	}
	
	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public User getManager() {
		return manager;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Building)) {
			return false;
		}
		
		return this.id == ((Building)other).id;
	}
	
	@Override
	public int hashCode() {
		return this.name.hashCode();
	}
}