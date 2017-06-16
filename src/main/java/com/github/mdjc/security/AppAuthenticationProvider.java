package com.github.mdjc.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.github.mdjc.domain.User;
import com.github.mdjc.domain.UserRepository;

public class AppAuthenticationProvider implements AuthenticationProvider {
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	private final UserRepository repository;
	
	public AppAuthenticationProvider(UserRepository repository) {
		this.repository = repository;
	}
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		User user = repository.getByUsername(username);
		String password =  authentication.getCredentials().toString();
		
		if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
			throw new BadCredentialsException("Invalid username/password");
		}
		
		Collection<? extends GrantedAuthority> authorities = Collections
				.singleton(new SimpleGrantedAuthority(user.getRole().toString()));
		return new UsernamePasswordAuthenticationToken(user, password, authorities);		
	}
	
	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
