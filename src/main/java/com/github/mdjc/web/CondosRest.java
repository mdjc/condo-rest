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
import com.github.mdjc.domain.Condo;
import com.github.mdjc.domain.CondoRepository;
import com.github.mdjc.domain.CondoStats;
import com.github.mdjc.domain.Outlay;
import com.github.mdjc.domain.OutlayRepository;
import com.github.mdjc.domain.OutlayStats;
import com.github.mdjc.domain.PaginationCriteria;
import com.github.mdjc.domain.BilltStats;
import com.github.mdjc.domain.User;

@RestController
public class CondosRest {
	@Autowired
	CondoRepository condoRepo;

	@Autowired
	BillRepository billRepo;
	
	@Autowired
	OutlayRepository outlayRepo;

	@GetMapping(path = "/condos")
	public Map<String, List<Condo>> userCondos(Authentication auth) {
		Map<String, List<Condo>> map = new HashMap<>();
		map.put("condos", condoRepo.getAllByUser((User) auth.getPrincipal()));
		return map;
	}

	@GetMapping(path = "/condos/{condoId}")
	public Map<String, Condo> getCondo(@PathVariable long condoId) {
		Map<String, Condo> map = new HashMap<>();
		map.put("condo", condoRepo.getBy(condoId));
		return map;
	}

	@GetMapping(path = "/condos/{condoId}/stats")
	public Map<String, CondoStats> condoStats(@PathVariable long condoId) {
		Map<String, CondoStats> map = new HashMap<>();
		map.put("stats", condoRepo.getStatsByCondoId(condoId));
		return map;
	}

	@GetMapping(path = "/condos/{condoId}/bills/stats")
	public Map<String, BilltStats> condoBillStats(@PathVariable long condoId, 
			@RequestParam("from")
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from, 
			@RequestParam("to") 
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
		
		Map<String, BilltStats> map = new HashMap<>();
		map.put("stats", billRepo.getStatsBy(condoId, from, to));
		return map;
	}

	@GetMapping(path = "/condos/{condoId}/outlays")
	public Map<String, List<Outlay>> condoOutlays(@PathVariable long condoId,
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
		map.put("outlays", outlayRepo.findBy(condoId, from, to, pagCriteria));
		return map;
	}
	
	@GetMapping(path = "/condos/{condoId}/outlays/stats")
	public Map<String, OutlayStats> condoOutlayStats(@PathVariable long condoId, 
			@RequestParam("from")
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from, 
			@RequestParam("to") 
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
		
		Map<String, OutlayStats> map = new HashMap<>();
		map.put("stats", outlayRepo.getStatsBy(condoId, from, to));
		return map;
	}
}