package com.github.mdjc.domain;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.NoSuchElementException;

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
public class BillRepositoryTest {
	@Autowired
	BillRepository repository;

	@Test
	public void testGetStatsBy_givenValidBuildingId() {
		BilltStats expected = new BilltStats(3, 1, 3, 2, 40);
		BilltStats actual = repository.getStatsBy(1, LocalDate.of(2016, 2, 23), LocalDate.of(2017, 10, 23));
		assertEquals(expected, actual);
	}

	@Test(expected = NoSuchElementException.class)
	public void testGetStatsBy_givenInvalidBuildingId() {
		repository.getStatsBy(69, LocalDate.of(2016, 2, 23), LocalDate.of(2017, 10, 23));
	}
}
