package com.github.mdjc.domain;

import com.github.mdjc.commons.args.Arguments;

public class OutlayStats {
	private final double amount;

	public OutlayStats(double amount) {
		this.amount = Arguments.checkPositive(amount);
	}

	public double getAmount() {
		return amount;
	}
}
