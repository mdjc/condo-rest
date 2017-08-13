package com.github.mdjc.domain;

import java.time.LocalDate;
import java.util.List;

public interface OutlayRepository {
	Outlay getBy(long outlayId);
	Outlay getBy(long outlayId, User user);
	OutlayStats getStatsBy(long condoId, LocalDate from, LocalDate to);
	
	List<Outlay> findBy(long condoId, LocalDate from, LocalDate to, PaginationCriteria options);
	int countFindBy(long condoId, LocalDate from, LocalDate to);
	
	long add(long condoId, Outlay outlay);
	void delete(long outlayId);
}
