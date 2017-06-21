package com.github.mdjc.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.mdjc.domain.Building;
import com.github.mdjc.domain.BuildingRepository;
import com.github.mdjc.domain.BuildingStats;
import com.github.mdjc.domain.User;

@RestController
public class CondosRest {
	@Autowired
	BuildingRepository buildingRepo;

	@GetMapping(path = "/buildings")
	public Map<String, List<Building>> userBuildings(Authentication auth) {
		Map<String, List<Building>> map = new HashMap<>();
		map.put("buildings", buildingRepo.getAllByUser((User) auth.getPrincipal()));
		return map;
	}

	@GetMapping(path = "/buildingStats")
	public Map<String, BuildingStats> buildingStats(@RequestParam long buildingId) {
		Map<String, BuildingStats> map = new HashMap<>();
		map.put("stats", buildingRepo.getStatsById(buildingId));
		return map;
	}
}
