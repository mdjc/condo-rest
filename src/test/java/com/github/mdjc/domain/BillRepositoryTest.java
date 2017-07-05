package com.github.mdjc.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
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
public class BillRepositoryTest {
	@Autowired
	BillRepository repository;

	@Test
	public void testGetStatsBy_givenValidCondoId() {
		BilltStats expected = new BilltStats(3, 1, 3, 2, 40);
		BilltStats actual = repository.getStatsBy(1, LocalDate.of(2016, 2, 23), LocalDate.of(2017, 10, 23));
		assertEquals(expected, actual);
	}

	@Test(expected = NoSuchElementException.class)
	public void testGetStatsBy_givenInvalidCondoId() {
		repository.getStatsBy(69, LocalDate.of(2016, 2, 23), LocalDate.of(2017, 10, 23));
	}

	@Test
	public void testGetByGivenValidApartmentIdAndPaymentStatusList_ShouldReturnList() {
		List<Bill> expected = Arrays.asList(
				new Bill(8, "montly share", LocalDate.of(2017, 06, 27), 10, PaymentStatus.REJECTED,
						PaymentMethod.TRANSFER, LocalDate.of(2017, 06, 27)),
				new Bill(10, "monthly share", LocalDate.of(2017, 07, 01), 10, PaymentStatus.PENDING, null,
						LocalDate.of(2017, 07, 01)),
				new Bill(12, "gas bill", LocalDate.of(2017, 07, 01), 200, PaymentStatus.REJECTED, PaymentMethod.DEPOSIT,
						LocalDate.of(2017, 07, 01)));

		List<Bill> actual = repository.getBy(4, Arrays.asList(PaymentStatus.PENDING, PaymentStatus.REJECTED));

		assertEquals(expected, actual);

		for (int i = 0; i < expected.size(); i++) {
			testEqual(expected.get(i), actual.get(i));
		}
	}

	public void testGetByGivenInvalidApartmentId_ShouldReturnEmptyList() {
		List<Bill> actual = repository.getBy(69, Arrays.asList(PaymentStatus.PENDING));
		assertEquals(Collections.EMPTY_LIST, actual);
	}

	public void testGetByGivenValidApartmentWithoutPayments_ShouldReturnEmptyList() {
		List<Bill> actual = repository.getBy(7, Arrays.asList(PaymentStatus.PENDING, PaymentStatus.REJECTED,
				PaymentStatus.PAID_AWAITING_CONFIRMATION, PaymentStatus.PAID_CONFIRMED));
		assertEquals(Collections.EMPTY_LIST, actual);
	}

	private void testEqual(Bill expected, Bill actual) {
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getDescription(), expected.getDescription());
		assertEquals(expected.getDueDate(), expected.getDueDate());
		assertTrue(Math.abs(expected.getDueAmount() - expected.getDueAmount()) == 0);
		assertEquals(expected.getPaymentStatus(), expected.getPaymentStatus());
		assertEquals(expected.getPaymentMethod(), expected.getPaymentMethod());
		assertEquals(expected.getLastUpdateOn(), expected.getLastUpdateOn());
	}

}
