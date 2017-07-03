package com.github.mdjc.domain;

import java.time.LocalDate;

public interface BillRepository {
	public BilltStats getStatsBy(long condoId, LocalDate from, LocalDate to);
}
