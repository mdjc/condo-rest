package com.github.mdjc.domain;

import java.util.List;

public interface CondoRepository {
	Condo getBy(long id);
	CondoStats getStatsByCondoId(long id);
	
	List<Condo> getAllByUser(User user);
	
	void refreshBalanceWithBill(long billId, int sign);
}
