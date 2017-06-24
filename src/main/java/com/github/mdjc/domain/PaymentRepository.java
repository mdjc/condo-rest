package com.github.mdjc.domain;

import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository {
	public List<Payment> findby(long buildingId, LocalDate from, LocalDate to, PaginationCriteria options);
}
