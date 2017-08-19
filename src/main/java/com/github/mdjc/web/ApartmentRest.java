package com.github.mdjc.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.github.mdjc.domain.Apartment;
import com.github.mdjc.domain.ApartmentRepository;

@RestController
public class ApartmentRest {
	@Autowired
	ApartmentRepository repository;
	
	@GetMapping(path = "/apartments/{username}")
	public Apartment residentApartment(@PathVariable String username) {
		return repository.getBy(username);
	}
}
