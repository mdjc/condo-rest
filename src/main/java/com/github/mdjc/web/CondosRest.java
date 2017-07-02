package com.github.mdjc.web;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.mdjc.domain.BillRepository;
import com.github.mdjc.domain.Building;
import com.github.mdjc.domain.BuildingRepository;
import com.github.mdjc.domain.BuildingStats;
import com.github.mdjc.domain.Outlay;
import com.github.mdjc.domain.OutlayRepository;
import com.github.mdjc.domain.OutlayStats;
import com.github.mdjc.domain.PaginationCriteria;
import com.github.mdjc.domain.BilltStats;
import com.github.mdjc.domain.User;

@RestController
public class CondosRest {
	@Autowired
	BuildingRepository buildingRepo;

	@Autowired
	BillRepository billRepo;
	
	@Autowired
	OutlayRepository outlayRepo;

	@GetMapping(path = "/buildings")
	public Map<String, List<Building>> userBuildings(Authentication auth) {
		Map<String, List<Building>> map = new HashMap<>();
		map.put("buildings", buildingRepo.getAllByUser((User) auth.getPrincipal()));
		return map;
	}

	@GetMapping(path = "/buildings/{buildingId}")
	public Map<String, Building> getBuilding(@PathVariable long buildingId) {
		Map<String, Building> map = new HashMap<>();
		map.put("building", buildingRepo.getBy(buildingId));
		return map;
	}

	@GetMapping(path = "/buildings/{buildingId}/stats")
	public Map<String, BuildingStats> buildingStats(@PathVariable long buildingId) {
		Map<String, BuildingStats> map = new HashMap<>();
		map.put("stats", buildingRepo.getStatsByBuildingId(buildingId));
		return map;
	}

	@GetMapping(path = "/buildings/{buildingId}/bills/stats")
	public Map<String, BilltStats> buildingBillStats(@PathVariable long buildingId, 
			@RequestParam("from")
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from, 
			@RequestParam("to") 
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
		
		Map<String, BilltStats> map = new HashMap<>();
		map.put("stats", billRepo.getStatsBy(buildingId, from, to));
		return map;
	}

	@GetMapping(path = "/buildings/{buildingId}/outlays")
	public Map<String, List<Outlay>> buildingOutlays(@PathVariable long buildingId,
			@RequestParam("from") 
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam("to") 
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
			@RequestParam(defaultValue = "0") int offset, 
			@RequestParam(defaultValue = "0") int limit,
			@RequestParam(defaultValue = "ASC") String order) {
		Map<String, List<Outlay>> map = new HashMap<>();
		PaginationCriteria pagCriteria = new PaginationCriteria(offset, limit,
				PaginationCriteria.SortingOrder.valueOf(order.toUpperCase()));
		map.put("outlays", outlayRepo.findBy(buildingId, from, to, pagCriteria));
		return map;
	}
	
	@GetMapping(path = "/buildings/{buildingId}/outlays/stats")
	public Map<String, OutlayStats> buildingOutlayStats(@PathVariable long buildingId, 
			@RequestParam("from")
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from, 
			@RequestParam("to") 
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
		
		Map<String, OutlayStats> map = new HashMap<>();
		map.put("stats", outlayRepo.getStatsBy(buildingId, from, to));
		return map;
	}
}