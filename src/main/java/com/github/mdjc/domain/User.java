package com.github.mdjc.domain;

import com.github.mdjc.commons.args.Arguments;

public class User {
	private final String username;
	private final String password;
	private final Role role;

	public User(String username, String password, Role role) {
		this.username = Arguments.checkBlank(username);
		this.password = password;
		this.role = role;
	}
	
	public User(String username, Role role) {
		this(username, "", role);
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
	
	public boolean isManager() {
		return role.equals(Role.MANAGER);
	}
	
	public boolean isResident() {
		return role.equals(Role.RESIDENT);
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof User)) return false;
		
		return this.username.equalsIgnoreCase(((User) other).username);
	}
	
	@Override
	public int hashCode() {
		return this.username.hashCode();
	}
}
