package com.github.mdjc.web;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.mdjc.domain.User;

@RestController
public class LoginRest {
	@PostMapping("login")
	User login(Authentication auth) {
		return (User) auth.getPrincipal();
	}
}
