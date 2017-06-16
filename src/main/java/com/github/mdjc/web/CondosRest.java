package com.github.mdjc.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CondosRest {
	
	@PostMapping(path="/managerurl")
	public String greetings() {
		return "Hi there: Manager";
	}
	
	@PostMapping(path="/residenturl")
	public String greetingsResident() {
		return "Hi there: Resident";
	}
}
