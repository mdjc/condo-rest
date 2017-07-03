package com.github.mdjc.domain;

import java.util.List;

public interface CondoRepository {
	List<Condo> getAllByUser(User user);
	CondoStats getStatsByCondoId(long id);
	Condo getBy(long id);
}
