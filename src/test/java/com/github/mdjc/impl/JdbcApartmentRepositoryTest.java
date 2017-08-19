package com.github.mdjc.impl;

import static org.junit.Assert.assertEquals;

import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.mdjc.domain.Apartment;
import com.github.mdjc.domain.ApartmentRepository;

@RunWith(SpringRunner.class)
@JdbcTest
public class JdbcApartmentRepositoryTest {
	@Autowired
	private NamedParameterJdbcTemplate template;
	private ApartmentRepository repository;

	@Before
	public void init() {
		repository = new JdbcApartmentRepository(template);
	}

	@Test
	public void testGetByGivenValidResident_shouldReturnApartment() {
		Apartment expected = new Apartment("1A");
		Apartment actual = repository.getBy("virgi");
		assertEquals(expected, actual);
	}

	@Test(expected = NoSuchElementException.class)
	public void testGetByGivenInvalidResident_shoulThrowException() {
		repository.getBy("unexsistent-user");
	}
}
