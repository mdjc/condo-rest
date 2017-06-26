package com.github.mdjc.domain;

import java.time.LocalDate;
import java.util.List;

public interface OutlayRepository {
	List<Outlay> findBy(long buildingId, LocalDate from, LocalDate to, PaginationCriteria options);
}
