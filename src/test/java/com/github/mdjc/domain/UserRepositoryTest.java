package com.github.mdjc.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.mdjc.config.BeansConfig;

@RunWith(SpringRunner.class)
@JdbcTest
@Import(BeansConfig.class)
public class UserRepositoryTest {
	@Autowired
	UserRepository repository;
	
	@Test
	public void testGetAllByUsername_givenValidUser_shouldReturnUser() {
		User expected = new User("luis", Role.MANAGER);
		User actual = repository.getByUsername("luis");
		assertEquals(expected, actual);
		assertEquals(expected.getRole(), actual.getRole());
	}
	
	@Test
	public void testGetAllByUsername_givenUnknowUser_shouldReturnNull() {
		User actual = repository.getByUsername("unknown");
		assertEquals(null, actual);
	}
}
