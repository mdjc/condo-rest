package com.github.mdjc.domain;

public class PaymentStats {
	private final int pendingPaymentCount;
	private final int validatedPaymentCount;
	private final int rejectedPaymentCount;
	private final double validatedPaymentTotal;
	
	public PaymentStats(int pendingPaymentCount, int validatedPaymentCount, int rejectedPaymentCount,
			double validatePaymentTotal) {
		super();
		this.pendingPaymentCount = pendingPaymentCount;
		this.validatedPaymentCount = validatedPaymentCount;
		this.rejectedPaymentCount = rejectedPaymentCount;
		this.validatedPaymentTotal = validatePaymentTotal;
	}

	public int getPendingPaymentCount() {
		return pendingPaymentCount;
	}

	public int getValidatedPaymentCount() {
		return validatedPaymentCount;
	}

	public int getRejectedPaymentCount() {
		return rejectedPaymentCount;
	}

	public double getValidatedPaymentTotal() {
		return validatedPaymentTotal;
	}	
	
	@Override
	public int hashCode() {
		return Double.hashCode(validatedPaymentTotal);
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof PaymentStats)) return false;
		
		PaymentStats that = (PaymentStats) other;
		return this.pendingPaymentCount == that.pendingPaymentCount
				&& this.validatedPaymentCount == that.validatedPaymentCount
				&& this.rejectedPaymentCount == that.rejectedPaymentCount
				&& this.validatedPaymentTotal == that.validatedPaymentTotal;
	}
}
