package com.github.mdjc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.mdjc.config.BeansConfig;
import com.github.mdjc.domain.Condo;
import com.github.mdjc.domain.CondoRepository;
import com.github.mdjc.domain.CondoStats;
import com.github.mdjc.domain.Role;
import com.github.mdjc.domain.User;

@RunWith(SpringRunner.class)
@JdbcTest
@Import(BeansConfig.class)
public class JdbcCondoRepositoryTest {
	@Autowired
	private JdbcTemplate template;
	private CondoRepository repository;
	
	@Before
	public void init() {
		repository = new JdbcCondoRepository(template);
	}	
	
	@Test
	public void testGetBy_givenValidId_shouldReturnCondo() {
		User user = new User("luis", Role.MANAGER);
		Condo expected = new Condo(3, "Mira Flores IV", user);
		Condo actual = repository.getBy(3);
		assertEquals(expected, actual);
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getManager(), actual.getManager());
	}
	
	@Test(expected=NoSuchElementException.class)
	public void testGetBy_givenInvalidId_shouldThrowException() {
		repository.getBy(69);		
	}
	
	@Test
	public void testGetStatsByCondoId_givenValidId_shouldReturnStatistics() {
		CondoStats actual = repository.getStatsByCondoId(1);
		CondoStats expected = new CondoStats(4, 2, 100);
		
		assertEquals(expected.getApartmentCount(), actual.getApartmentCount());
		assertEquals(expected.getResidentCount(), actual.getResidentCount());
		assertTrue(Math.abs(expected.getBalance() - actual.getBalance()) == 0);
	}
	
	@Test
	public void testGetAllByUser_givenManager_shouldReturnTwoCondo() {
		User user = new User("luis", Role.MANAGER);
		List<Condo> expected = Arrays.asList(
				new Condo(4, "Baldwing IV", user),
				new Condo(3, "Mira Flores IV", user));
		List<Condo> actual = repository.getAllByUser(user);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetAllByUser_givenResident_shouldReturnOneCondo() {
		User user = new User("virgi", Role.RESIDENT);
		List<Condo> expected = Arrays.asList(new Condo(1, "Shadai I", user));
		List<Condo> actual = repository.getAllByUser(user);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetAllByUser_givenManager_shouldReturnSeveralCondos() {
		User user = new User("mirna", Role.MANAGER);
		List<Condo> expected = Arrays.asList(new Condo(2, "Loring  Place 2333", user),
				new Condo(1, "Shadai I", user));
		List<Condo> actual = repository.getAllByUser(user);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetAllByUser_givenResident_shouldReturnSeveralCondos() {
		User user = new User("mary", Role.RESIDENT);
		List<Condo> expected = Arrays.asList(new Condo(2, "Loring  Place 2333",  new User("mirna", Role.MANAGER)),
				new Condo(3, "Mira Flores IV",  new User("luis", Role.MANAGER)));
		List<Condo> actual = repository.getAllByUser(user);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetAllByUser_givenUnexistentUser_shouldReturnEmptyList() {
		User user = new User("unexistent", Role.MANAGER);
		assertTrue(repository.getAllByUser(user).isEmpty());
	}	
	
	@Test(expected=NoSuchElementException.class)
	public void testGetStatsByCondoId_givenInvalidId_shouldThrowException() {
		repository.getStatsByCondoId(69);		
	}
	
	@Test
	public void testFefreshBalanceWithBill_givenValidBillAndPositiveSign_shouldPerformRefresh() {
		CondoStats stats = repository.getStatsByCondoId(1);
		double currentBalance = stats.getBalance();
		double expectedBalance = currentBalance + 20;
		repository.refreshBalanceWithBill(3, 1);
		CondoStats statsAfterRefresh = repository.getStatsByCondoId(1);
		double actualBalance = statsAfterRefresh.getBalance();
		assertTrue(expectedBalance - actualBalance == 0);
	}
	
	@Test
	public void testFefreshBalanceWithBill_givenValidBillAndNegativeSign_shouldPerformRefresh() {
		CondoStats stats = repository.getStatsByCondoId(1);
		double currentBalance = stats.getBalance();
		double expectedBalance = currentBalance - 20;
		repository.refreshBalanceWithBill(3, -1);
		CondoStats statsAfterRefresh = repository.getStatsByCondoId(1);
		double actualBalance = statsAfterRefresh.getBalance();
		assertTrue(expectedBalance - actualBalance == 0);
	}
}