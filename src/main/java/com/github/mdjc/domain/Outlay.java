package com.github.mdjc.domain;

import java.time.LocalDate;

import com.github.mdjc.commons.args.Arguments;

public class Outlay implements Comparable<Outlay> {
	private final long id;
	private final OutlayCategory category;
	private final double amount;
	private final String supplier;
	private final String comment;
	private final ImageExtension receiptImageExtension;
	private final LocalDate createdOn;

	public Outlay(long id, OutlayCategory category, double amount, String supplier, String comment,
			ImageExtension receiptImageExtension, LocalDate createdOn) {
		this.id = id;
		this.category = category;
		this.amount = Arguments.checkPositive(amount);
		this.supplier = supplier;
		this.comment = comment;
		this.receiptImageExtension = receiptImageExtension;
		this.createdOn = Arguments.checkNull(createdOn);
	}

	public Outlay(OutlayCategory category, double amount, String supplier, String comment,
			ImageExtension receiptImageExtension, LocalDate createdOn) {
		this(0, category, amount, supplier, comment, receiptImageExtension, createdOn);
	}

	public long getId() {
		return id;
	}

	public OutlayCategory getCategory() {
		return category;
	}

	public double getAmount() {
		return amount;
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
	
	public LocalDate getCreatedOn() {
		return createdOn;
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
