package com.github.mdjc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.github.mdjc.domain.UserRepository;
import com.github.mdjc.impl.JDBCUserRepository;

@Configuration
public class BeansConfig {	
	@Bean
	public UserRepository userRepository(JdbcTemplate template){
		return new JDBCUserRepository(template);
	}
}
