package com.github.mdjc.domain;

import java.util.List;

public interface CondoRepository {
	Condo getBy(long id);
	Condo getBy(long id, User user);
	
	CondoStats getStatsByCondoId(long id);
	
	List<Condo> getAllByUser(User user);
	List<Apartment> getCondoApartments(long condoId);
	
	void refreshBalanceWithBill(long billId, int sign);
	void refreshBalanceWithOutlay(long outlayId, int sign);
}
