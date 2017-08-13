package com.github.mdjc.domain;

import java.time.LocalDate;
import java.util.List;

public interface BillRepository {
	Bill getBy(long billId);
	Bill getBy(long billId, User user);
	CondoBill getCondoBilldBy(long billId);
	BilltStats getStatsBy(long condoId, LocalDate from, LocalDate to);
	
	List<CondoBill> findBy(long condoId, List<PaymentStatus> paymentStatusList, LocalDate from, LocalDate to, 
			PaginationCriteria pagCriteria);
	int countFindBy(long condoId, List<PaymentStatus> paymentStatusList, LocalDate from, LocalDate to);
	List<Bill> findBy(long condoId, String username, List<PaymentStatus> paymentStatusList);
	
	void add(long condoId, CondoBill bill);
	void delete(long billId);
	
	void updatePaymentInfo(long billId, PaymentStatus paymentStatus, PaymentMethod paymentMethod,
			ImageExtension prooOfPaymentExt);
	void updatePaymentInfo(long billId, PaymentStatus paymentStatus, PaymentMethod paymentMethod);
	void updatePaymentInfo(long billId, PaymentStatus paymentStatus);
}
