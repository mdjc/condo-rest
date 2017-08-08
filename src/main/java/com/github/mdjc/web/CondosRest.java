package com.github.mdjc.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.github.mdjc.domain.Apartment;
import com.github.mdjc.domain.Condo;
import com.github.mdjc.domain.CondoRepository;
import com.github.mdjc.domain.CondoStats;
import com.github.mdjc.domain.User;

@RestController
public class CondosRest {
	@Autowired
	CondoRepository condoRepo;

	@GetMapping(path = "condos")
	public List<Condo> userCondos(Authentication auth) {
		return condoRepo.getAllByUser((User) auth.getPrincipal());
	}

	@GetMapping(path = "condos/{condoId}")
	public Condo getCondo(@PathVariable long condoId) {
		return condoRepo.getBy(condoId);
	}

	@GetMapping(path = "condos/{condoId}/stats")
	public CondoStats condoStats(@PathVariable long condoId) {
		return condoRepo.getStatsByCondoId(condoId);
	}
	
	@GetMapping(path = "condos/{condoId}/apartments")
	public List<Apartment> condoApartments(@PathVariable long condoId) {
		return condoRepo.getCondoApartments(condoId);
	}
}