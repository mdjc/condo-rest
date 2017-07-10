package com.github.mdjc.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.github.mdjc.domain.Condo;
import com.github.mdjc.domain.CondoRepository;
import com.github.mdjc.domain.CondoStats;
import com.github.mdjc.domain.User;
import com.google.common.collect.ImmutableMap;

@RestController
public class CondosRest {
	@Autowired
	CondoRepository condoRepo;

	@GetMapping(path = "/condos")
	public Map<String, List<Condo>> userCondos(Authentication auth) {
		return ImmutableMap.of("condos", condoRepo.getAllByUser((User) auth.getPrincipal()));
	}

	@GetMapping(path = "/condos/{condoId}")
	public Map<String, Condo> getCondo(@PathVariable long condoId) {
		return ImmutableMap.of("condo", condoRepo.getBy(condoId));
	}

	@GetMapping(path = "/condos/{condoId}/stats")
	public Map<String, CondoStats> condoStats(@PathVariable long condoId) {
		return ImmutableMap.of("stats", condoRepo.getStatsByCondoId(condoId));
	}
}