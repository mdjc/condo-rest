package com.github.mdjc.domain;

public class BilltStats {
	private final int pendingBillCount;
	private final int awaitingValidationBillCount;
	private final int rejectedBillCount;
	private final int paidBillCount;
	private final double paidBillTotal;
	
	public BilltStats(int pendingBillCount, int awaitingValidationBillCount, int rejectedBillCount, int paidBillCount,
			double paidBillTotal) {
		super();
		this.pendingBillCount = pendingBillCount;
		this.awaitingValidationBillCount = awaitingValidationBillCount;
		this.rejectedBillCount = rejectedBillCount;
		this.paidBillCount = paidBillCount;
		this.paidBillTotal = paidBillTotal;
	}

	public int getPendingBillCount() {
		return pendingBillCount;
	}

	public int getAwaitingValidationBillCount() {
		return awaitingValidationBillCount;
	}

	public int getRejectedBillCount() {
		return rejectedBillCount;
	}

	public int getPaidBillCount() {
		return paidBillCount;
	}

	public double getPaidBillTotal() {
		return paidBillTotal;
	}

	@Override
	public int hashCode() {
		return Double.hashCode(paidBillTotal);
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof BilltStats)) return false;
		
		BilltStats that = (BilltStats) other;
		return this.pendingBillCount == that.pendingBillCount
				&& this.awaitingValidationBillCount == that.awaitingValidationBillCount
				&& this.rejectedBillCount == that.rejectedBillCount
				&& this.paidBillCount == that.paidBillCount
				&& this.paidBillTotal == that.paidBillTotal;
	}
}
