package com.github.mdjc.web;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.mdjc.domain.Outlay;
import com.github.mdjc.domain.OutlayRepository;
import com.github.mdjc.domain.OutlayStats;
import com.github.mdjc.domain.PaginationCriteria;
import com.google.common.collect.ImmutableMap;

@RestController
public class OutlayRest {
	@Autowired
	OutlayRepository outlayRepo;
	
	@GetMapping(path = "/condos/{condoId}/outlays")
	public Map<String, List<Outlay>> condoOutlays(@PathVariable long condoId,
			@RequestParam("from") 
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam("to") 
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
			@RequestParam(defaultValue = "0") int offset, 
			@RequestParam(defaultValue = "0") int limit,
			@RequestParam(defaultValue = "ASC") String order) {
		PaginationCriteria pagCriteria = new PaginationCriteria(offset, limit,
				PaginationCriteria.SortingOrder.valueOf(order.toUpperCase()));
		return ImmutableMap.of("outlays", outlayRepo.findBy(condoId, from, to, pagCriteria));
	}
	
	@GetMapping(path = "/condos/{condoId}/outlays/stats")
	public Map<String, OutlayStats> condoOutlayStats(@PathVariable long condoId, 
			@RequestParam("from")
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from, 
			@RequestParam("to") 
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
		return ImmutableMap.of("stats", outlayRepo.getStatsBy(condoId, from, to));
	}
}
