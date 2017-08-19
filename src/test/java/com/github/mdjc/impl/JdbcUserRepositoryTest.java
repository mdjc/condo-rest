package com.github.mdjc.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.mdjc.domain.Role;
import com.github.mdjc.domain.User;
import com.github.mdjc.domain.UserRepository;

@RunWith(SpringRunner.class)
@JdbcTest
public class JdbcUserRepositoryTest {
	@Autowired
	private NamedParameterJdbcTemplate template;
	private UserRepository repository;
	
	@Before
	public void init() {
		repository = new JdbcUserRepository(template);
	}
	
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
