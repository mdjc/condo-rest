package com.github.mdjc.domain;

import java.time.LocalDate;
import java.util.List;

public interface BillRepository {
	public BilltStats getStatsBy(long condoId, LocalDate from, LocalDate to);
	public List<Bill> getBy(long condoId, String username,  List<PaymentStatus> paymentStatusList);
}
