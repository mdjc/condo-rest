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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.mdjc.domain.Apartment;
import com.github.mdjc.domain.Condo;
import com.github.mdjc.domain.CondoRepository;
import com.github.mdjc.domain.CondoStats;
import com.github.mdjc.domain.ImageExtension;
import com.github.mdjc.domain.Role;
import com.github.mdjc.domain.User;

@RunWith(SpringRunner.class)
@JdbcTest
public class JdbcCondoRepositoryTest {
	@Autowired
	private NamedParameterJdbcTemplate template;
	private CondoRepository repository;

	@Before
	public void init() {
		repository = new JdbcCondoRepository(template);
	}

	@Test
	public void testGetBy_givenValidId_shouldReturnCondo() {
		User user = new User("luis", Role.MANAGER);
		Condo expected = new Condo(3, "Mira Flores IV", user, "Calle Bartolo #17, Santo Domingo", "maria", "8096169980");
		Condo actual = repository.getBy(3);
		assertCondoEquals(expected, actual);
	}

	@Test(expected = NoSuchElementException.class)
	public void testGetBy_givenInvalidId_shouldThrowException() {
		repository.getBy(69);
	}

	@Test
	public void testGetBy_givenValidIdAndManager_shouldReturnCondo() {
		User user = new User("luis", Role.MANAGER);
		Condo expected = new Condo(3, "Mira Flores IV", user, "Calle Bartolo #17, Santo Domingo", "maria", "8096169980");
		Condo actual = repository.getBy(3, user);
		assertCondoEquals(expected, actual);
	}

	@Test
	public void testGetBy_givenValidIdAndResident_shouldReturnCondo() {
		User resident = new User("mary", Role.RESIDENT);
		Condo expected = new Condo(3, "Mira Flores IV", new User("luis", Role.MANAGER),
				"Calle Bartolo #17, Santo Domingo", "maria", "8096169980");
		Condo actual = repository.getBy(3, resident);
		assertCondoEquals(expected, actual);
	}

	@Test(expected = NoSuchElementException.class)
	public void testGetBy_givenUnrelatedIdAndValidManager_shouldThrowException() {
		repository.getBy(1, new User("luis", Role.MANAGER));
	}

	@Test(expected = NoSuchElementException.class)
	public void testGetBy_givenUnrelatedIdAndValidResident_shouldThrowException() {
		repository.getBy(1, new User("mary", Role.RESIDENT));
	}

	@Test(expected = NoSuchElementException.class)
	public void testGetBy_givenInvalidIdAndValidManager_shouldThrowException() {
		repository.getBy(69, new User("luis", Role.MANAGER));
	}

	@Test(expected = NoSuchElementException.class)
	public void testGetBy_givenInvalidIdAndValidResident_shouldThrowException() {
		repository.getBy(69, new User("mary", Role.RESIDENT));
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
		List<Condo> expected = Arrays.asList(new Condo(4, "Baldwing IV", user, "Calle Bartolo #18, Santo Domingo", "jose", "8096169980"),
				new Condo(3, "Mira Flores IV", user, "Calle Bartolo #17, Santo Domingo", "maria", "8096169980"));
		List<Condo> actual = repository.getAllByUser(user);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetAllByUser_givenResident_shouldReturnOneCondo() {
		User user = new User("virgi", Role.RESIDENT);
		List<Condo> expected = Arrays.asList(new Condo(1, "Shadai I", user, "Calle Bartolo #15, Santo Domingo", "juan", "8096169980", ImageExtension.PNG));
		List<Condo> actual = repository.getAllByUser(user);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetAllByUser_givenManager_shouldReturnSeveralCondos() {
		User user = new User("mirna", Role.MANAGER);
		List<Condo> expected = Arrays.asList(
				new Condo(2, "Loring  Place 2333", user, "Calle Bartolo #16, Santo Domingo", "pedro", "8096169980"),
				new Condo(1, "Shadai I", user, "Calle Bartolo #15, Santo Domingo", "juan", "8096169980", ImageExtension.PNG));
		List<Condo> actual = repository.getAllByUser(user);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetAllByUser_givenResident_shouldReturnSeveralCondos() {
		User user = new User("mary", Role.RESIDENT);
		List<Condo> expected = Arrays.asList(
				new Condo(2, "Loring  Place 2333", new User("mirna", Role.MANAGER), "Calle Bartolo #16, Santo Domingo", "pedro", "8096169980"),
				new Condo(3, "Mira Flores IV", new User("luis", Role.MANAGER), "Calle Bartolo #17, Santo Domingo", "maria", "8096169980"));
		List<Condo> actual = repository.getAllByUser(user);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetAllByUser_givenUnexistentUser_shouldReturnEmptyList() {
		User user = new User("unexistent", Role.MANAGER);
		assertTrue(repository.getAllByUser(user).isEmpty());
	}

	@Test
	public void testGetCondoApartmentsGivenValidCondo_shouldReturnist() {
		List<Apartment> expected = Arrays.asList(new Apartment("1A"), new Apartment("1B"), new Apartment("1C"),
				new Apartment("1D"));
		List<Apartment> actual = repository.getCondoApartments(1);

		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).getName(), actual.get(i).getName());
		}
	}

	@Test
	public void testGetCondoApartmentsGivenValidCondo_shouldReturnEmpyList() {
		assertTrue(repository.getCondoApartments(69).isEmpty());
	}

	@Test(expected = NoSuchElementException.class)
	public void testGetStatsByCondoId_givenInvalidId_shouldThrowException() {
		repository.getStatsByCondoId(69);
	}

	@Test
	public void testFefreshBalanceWithBill_givenValidBillAndPositiveSign_shouldPerformRefresh() {
		int billId = 3;
		double billDueAmount = 20;
		CondoStats stats = repository.getStatsByCondoId(1);
		double currentBalance = stats.getBalance();
		double expectedBalance = currentBalance + billDueAmount;
		repository.refreshBalanceWithBill(billId, 1);
		CondoStats statsAfterRefresh = repository.getStatsByCondoId(1);
		double actualBalance = statsAfterRefresh.getBalance();
		assertTrue(expectedBalance - actualBalance == 0);
	}

	@Test
	public void testFefreshBalanceWithBill_givenValidBillAndNegativeSign_shouldPerformRefresh() {
		int billId = 3;
		double billDueAmount = 20;
		CondoStats stats = repository.getStatsByCondoId(1);
		double currentBalance = stats.getBalance();
		double expectedBalance = currentBalance - billDueAmount;
		repository.refreshBalanceWithBill(billId, -1);
		CondoStats statsAfterRefresh = repository.getStatsByCondoId(1);
		double actualBalance = statsAfterRefresh.getBalance();
		assertTrue(expectedBalance - actualBalance == 0);
	}

	@Test
	public void testFefreshBalanceWithOutlay_givenValidIdAndPositiveSign_shouldPerformRefresh() {
		int condoId = 1, outlayId = 1;
		double outlayAmount = 15;
		CondoStats stats = repository.getStatsByCondoId(condoId);
		double currentBalance = stats.getBalance();
		double expectedBalance = currentBalance + outlayAmount;
		repository.refreshBalanceWithOutlay(outlayId, 1);
		CondoStats statsAfterRefresh = repository.getStatsByCondoId(condoId);
		double actualBalance = statsAfterRefresh.getBalance();
		assertTrue(expectedBalance - actualBalance == 0);
	}

	@Test
	public void testFefreshBalanceWithOutlay_givenValidIdAndNegativeSign_shouldPerformRefresh() {
		int condoId = 1, outlayId = 1;
		double outlayAmount = 15;
		CondoStats stats = repository.getStatsByCondoId(condoId);
		double currentBalance = stats.getBalance();
		double expectedBalance = currentBalance - outlayAmount;
		repository.refreshBalanceWithOutlay(outlayId, -1);
		CondoStats statsAfterRefresh = repository.getStatsByCondoId(condoId);
		double actualBalance = statsAfterRefresh.getBalance();
		assertTrue(expectedBalance - actualBalance == 0);
	}

	private void assertCondoEquals(Condo expected, Condo actual) {
		assertEquals(expected, actual);
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getManager(), actual.getManager());
		assertEquals(expected.getAddress(), actual.getAddress());
		assertEquals(expected.getContactName(), actual.getContactName());
		assertEquals(expected.getContactPhone(), actual.getContactPhone());
		assertEquals(expected.getImageExtension(), actual.getImageExtension());
	}
}
