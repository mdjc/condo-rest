package com.github.mdjc.domain;

import com.github.mdjc.commons.args.Arguments;

public class Condo {
	private final long id;
	private final String name;
	private final User manager;
	private final String address;
	private final String contactName;
	private final String contactPhone;
	private final ImageExtension imageExtension;
	
	public Condo(long id, String name, User manager, String address) {
		this(id, name, manager, address, null, null, null);
	}
	
	public Condo(long id, String name, User manager, String address, String contactName, String contactPhone) {
		this(id, name, manager, address, contactName, contactPhone, null);
	}
	
	public Condo(long id, String name, User manager, String address, String contactName, String contactPhone, ImageExtension imageExtension) {
		this.id = Arguments.checkPositive(id);
		this.name = Arguments.checkBlank(name);
		this.manager = Arguments.checkNull(manager);
		this.address = Arguments.checkBlank(address);
		this.contactName = contactName;
		this.contactPhone = contactPhone;
		this.imageExtension = imageExtension;
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

	public String getAddress() {
		return address;
	}

	public String getContactName() {
		return contactName;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public ImageExtension getImageExtension() {
		return imageExtension;
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