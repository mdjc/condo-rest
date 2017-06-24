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

import com.github.mdjc.domain.Building;
import com.github.mdjc.domain.BuildingRepository;
import com.github.mdjc.domain.BuildingStats;
import com.github.mdjc.domain.PaginationCriteria;
import com.github.mdjc.domain.Payment;
import com.github.mdjc.domain.PaymentRepository;
import com.github.mdjc.domain.User;

@RestController
public class CondosRest {
	@Autowired
	BuildingRepository buildingRepo;

	@Autowired
	PaymentRepository paymentRepo;

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

	@GetMapping(path = "/buildings/{buildingId}/payments")
	public Map<String, List<Payment>> userBuildings(@PathVariable long buildingId, 
			@RequestParam("from")
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from, 
			@RequestParam("to") 
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
			@RequestParam(defaultValue="0") int offset,
			@RequestParam(defaultValue = "0") int limit,
			@RequestParam(defaultValue="ASC") String order) {
		PaginationCriteria pagCriteria = new PaginationCriteria(offset, limit, PaginationCriteria.SortingOrder.valueOf(order.toUpperCase()));
		
		Map<String, List<Payment>> map = new HashMap<>();
		map.put("payments", paymentRepo.findby(buildingId, from, to, pagCriteria));
		return map;
	}
}
