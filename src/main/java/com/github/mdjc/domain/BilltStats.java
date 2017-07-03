package com.github.mdjc.domain;

public class BilltStats {
	private final int pendingBillCount;
	private final int paidAwaitingConfirmationBillCount;
	private final int paidConfirmedBillCount;
	private final int rejectedBillCount;
	private final double paidConfirmedBillTotal;
	
	public BilltStats(int pendingBillCount, int paidAwaitingConfirmationBillCount, int paidConfirmedBillCount,
			int rejectedBillCount, double paidBillTotal) {
		this.pendingBillCount = pendingBillCount;
		this.paidAwaitingConfirmationBillCount = paidAwaitingConfirmationBillCount;
		this.paidConfirmedBillCount = paidConfirmedBillCount;
		this.rejectedBillCount = rejectedBillCount;
		this.paidConfirmedBillTotal = paidBillTotal;
	}
	
	public int getPendingBillCount() {
		return pendingBillCount;
	}

	public int getPaidAwaitingConfirmationBillCount() {
		return paidAwaitingConfirmationBillCount;
	}

	public int getPaidConfirmedBillCount() {
		return paidConfirmedBillCount;
	}


	public int getRejectedBillCount() {
		return rejectedBillCount;
	}

	public double getPaidConfirmedBillTotal() {
		return paidConfirmedBillTotal;
	}

	@Override
	public int hashCode() {
		return Double.hashCode(paidConfirmedBillTotal);
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof BilltStats)) return false;
		
		BilltStats that = (BilltStats) other;
		return this.pendingBillCount == that.pendingBillCount
				&& this.paidAwaitingConfirmationBillCount == that.paidAwaitingConfirmationBillCount
				&& this.paidConfirmedBillCount == that.paidConfirmedBillCount
				&& this.rejectedBillCount == that.rejectedBillCount
				&& this.paidConfirmedBillTotal == that.paidConfirmedBillTotal;
	}
}
