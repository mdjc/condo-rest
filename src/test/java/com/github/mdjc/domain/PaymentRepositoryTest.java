package com.github.mdjc.domain;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
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
public class PaymentRepositoryTest {
	@Autowired
	PaymentRepository repository;

	@Test
	public void testFindBy_GivenFrom_ShouldReturnServeralPayments() {
		List<Payment> expected = Arrays.asList(new Payment(2, new Apartment("2", new User("mary", Role.RESIDENT)), 10,
				PaymentMethod.CHECK, LocalDate.of(2017, 5, 15), new User("john", Role.RESIDENT), PaymentStatus.PENDING),

				new Payment(1, new Apartment("1", new User("john", Role.RESIDENT)), 5, PaymentMethod.CHECK,
						LocalDate.of(2017, 6, 15), new User("john", Role.RESIDENT), PaymentStatus.PENDING));

		List<Payment> actual = repository.findby(2L, LocalDate.of(2017, 5, 1), null, new PaginationCriteria(0, 0));
		assertEquals(expected, actual);
	}

	@Test
	public void testFindBy_GivenFromAndTo_ShouldReturnServeralPayments() {
		List<Payment> expected = Arrays
				.asList(new Payment(2, new Apartment("2", new User("mary", Role.RESIDENT)), 10, PaymentMethod.CHECK,
						LocalDate.of(2017, 5, 15), new User("john", Role.RESIDENT), PaymentStatus.PENDING));

		List<Payment> actual = repository.findby(2, LocalDate.of(2017, 5, 1), LocalDate.of(2017, 6, 10),
				new PaginationCriteria(0, 0));
		assertEquals(expected, actual);
	}

	@Test
	public void testFindBy_GivenFromAndToWithSortDesc_ShouldReturnServeralPayments() {
		List<Payment> expected = Arrays.asList(new Payment(1, new Apartment("1", new User("john", Role.RESIDENT)), 5,
				PaymentMethod.CHECK, LocalDate.of(2017, 6, 15), new User("john", Role.RESIDENT), PaymentStatus.PENDING),

				new Payment(2, new Apartment("2", new User("mary", Role.RESIDENT)), 10, PaymentMethod.CHECK,
						LocalDate.of(2017, 5, 15), new User("john", Role.RESIDENT), PaymentStatus.PENDING));

		List<Payment> actual = repository.findby(2, LocalDate.of(2017, 5, 1), LocalDate.of(2017, 7, 10),
				new PaginationCriteria(0, 0, PaginationCriteria.SortingOrder.DESC));
		assertEquals(expected, actual);
	}

	@Test
	public void testFindBy_GivenFromAndToAndOffset_ShouldReturnServeralPayments() {
		List<Payment> expected = Arrays
				.asList(new Payment(1, new Apartment("1", new User("john", Role.RESIDENT)), 5, PaymentMethod.CHECK,
						LocalDate.of(2017, 6, 15), new User("john", Role.RESIDENT), PaymentStatus.PENDING));

		List<Payment> actual = repository.findby(2, LocalDate.of(2017, 5, 1), LocalDate.of(2017, 7, 10),
				new PaginationCriteria(1, 1));
		assertEquals(expected, actual);
	}

	@Test
	public void testFindBy_GivenFromToLimitOffsetWithSortDesc_ShouldReturnServeralPayments() {
		List<Payment> expected = Arrays
				.asList(new Payment(2, new Apartment("2", new User("mary", Role.RESIDENT)), 10, PaymentMethod.CHECK,
						LocalDate.of(2017, 5, 15), new User("john", Role.RESIDENT), PaymentStatus.PENDING));

		List<Payment> actual = repository.findby(2, LocalDate.of(2017, 5, 15), LocalDate.of(2017, 8, 1),
				new PaginationCriteria(1, 1, PaginationCriteria.SortingOrder.DESC));
		assertEquals(expected, actual);
	}

	@Test
	public void testGetStatsBy_givenValidBuildingId() {
		PaymentStats expected = new PaymentStats(2, 1, 0, 10);
		PaymentStats actual = repository.getStatsBy(1, LocalDate.of(2016, 2, 23), LocalDate.of(2017, 10, 23));
		assertEquals(expected, actual);
	}

	@Test(expected = NoSuchElementException.class)
	public void testGetStatsBy_givenInvalidBuildingId() {
		repository.getStatsBy(69, LocalDate.of(2016, 2, 23), LocalDate.of(2017, 10, 23));
	}

}
