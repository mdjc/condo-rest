package com.github.mdjc;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class CondosRestApplication implements ApplicationRunner{
	private static Logger LOGGER = LoggerFactory.getLogger(CondosRestApplication.class);
	
	@Autowired
	Environment env;

	public static void main(String[] args) {
		SpringApplication.run(CondosRestApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		LOGGER.info("active profiles: {}", Arrays.toString(env.getActiveProfiles()));
	}
}
