package com.github.mdjc.domain;

import com.github.mdjc.commons.args.Arguments;

public class Condo {
	private final long id;
	private final String name;
	private final User manager;
	
	public Condo(long id, String name, User manager) {
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
		if (!(other instanceof Condo)) {
			return false;
		}
		
		return this.id == ((Condo)other).id;
	}
	
	@Override
	public int hashCode() {
		return this.name.hashCode();
	}
}