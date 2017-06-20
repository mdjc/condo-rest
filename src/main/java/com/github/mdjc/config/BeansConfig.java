package com.github.mdjc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.github.mdjc.domain.BuildingRepository;
import com.github.mdjc.domain.UserRepository;
import com.github.mdjc.impl.JdbcBuildingRepository;
import com.github.mdjc.impl.JdbcUserRepository;

@Configuration
public class BeansConfig {	
	@Bean
	public UserRepository userRepository(JdbcTemplate template){
		return new JdbcUserRepository(template);
	}
	
	@Bean
	public BuildingRepository buildingRepository(JdbcTemplate template) {
		return new JdbcBuildingRepository(template);
	}
}
