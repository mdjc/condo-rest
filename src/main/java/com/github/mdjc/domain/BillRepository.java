package com.github.mdjc.domain;

import java.time.LocalDate;
import java.util.List;

public interface BillRepository {
	Bill getBy(long billId);
	BilltStats getStatsBy(long condoId, LocalDate from, LocalDate to);
	
	List<Bill> findBy(long condoId, List<PaymentStatus> paymentStatusList);
	List<Bill> findBy(long condoId, String username, List<PaymentStatus> paymentStatusList);
	
	void updatePaymentInfo(long billId, PaymentStatus paymentStatus, PaymentMethod paymentMethod,
			ProofOfPaymentExtension prooOfPaymentExt);
}
