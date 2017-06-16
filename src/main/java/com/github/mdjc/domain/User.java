package com.github.mdjc.domain;

public class User {
	private final String username;
	private final String password;
	private final Role role;
	private final String apartment;
	
	public User(String username, String password, Role role, String apartment) {
		this.username = username;
		this.password = password;
		this.role = role;
		this.apartment = apartment;
	}
	
	public User(String username, Role role, String apartment) {
		this(username, "", role, apartment);
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public Role getRole() {
		return role;
	}

	public String getApartment() {
		return apartment;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null) return false;
		if (!(other instanceof User)) return false;
		
		return this.username.equalsIgnoreCase(((User) other).username);
	}
	
	@Override
	public int hashCode() {
		return this.username.hashCode();
	}
}
