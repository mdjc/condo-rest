package com.github.mdjc.domain;

import java.util.List;

public interface BuildingRepository {
	List<Building> getAllByUser(User user);
}
