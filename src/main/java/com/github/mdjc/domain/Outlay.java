package com.github.mdjc.domain;

import java.time.LocalDate;

import com.github.mdjc.commons.args.Arguments;

public class Outlay implements Comparable<Outlay>{
	private final long id;
	private OutlayCategory category;
	private final double amount;
	private final LocalDate createdOn;
	private final String supplier;
	private final String comment;
	private final ImageExtension receiptImageExtension; 
	
	public Outlay(long id, OutlayCategory category, double amount, LocalDate createdOn, String supplier,
			String comment, ImageExtension receiptImageExtension) {
		this.id = id;
		this.category = category;
		this.amount = Arguments.checkPositive(amount);
		this.createdOn = Arguments.checkNull(createdOn);
		this.supplier = supplier;
		this.comment = comment;
		this.receiptImageExtension = receiptImageExtension;
	}

	public long getId() {
		return id;
	}

	public OutlayCategory getCategory() {
		return category;
	}

	public void setCategory(OutlayCategory category) {
		this.category = category;
	}

	public double getAmount() {
		return amount;
	}

	public LocalDate getCreatedOn() {
		return createdOn;
	}

	public String getSupplier() {
		return supplier;
	}

	public String getComment() {
		return comment;
	}
	
	public ImageExtension getReceiptImageExtension() {
		return receiptImageExtension;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof Outlay && compareTo((Outlay) other) == 0;
	}
	
	@Override
	public int compareTo(Outlay o) {
		if (this.createdOn.equals(o.createdOn)) {
			return Long.compare(this.id, o.id);
		}
		
		return this.createdOn.compareTo(o.createdOn);
	}
	
	@Override
	public int hashCode() {
		return Long.hashCode(id);
	}
}
