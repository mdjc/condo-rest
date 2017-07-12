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
	public void testGetBy_givenValidId_shouldReturnBill() {
		Bill expected = new Bill(3, "cuota mensual", LocalDate.of(2017, 6, 15), 20, PaymentStatus.PAID_CONFIRMED,
				LocalDate.of(2017, 6, 15), PaymentMethod.CHECK, ProofOfPaymentExtension.JPG);
		Bill actual = repository.getBy(3);
		assertEquals(expected, actual);
	}

	@Test(expected = NoSuchElementException.class)
	public void testGetByGivenInvalidId_shouldThrowException() {
		repository.getBy(69);
	}

	@Test
	public void testGetCondoBillBy_givenValidId_shouldReturnCondoBill() {
		CondoBill expected = new CondoBill(3, "cuota mensual", LocalDate.of(2017, 6, 15), 20,
				PaymentStatus.PAID_CONFIRMED, LocalDate.of(2017, 6, 15), PaymentMethod.CHECK,
				ProofOfPaymentExtension.JPG, new Apartment("1D", new User("aldo", Role.RESIDENT)));
		CondoBill actual = repository.getCondoBilldBy(3);
		assertCondoBillEquals(expected, actual);
	}

	@Test(expected = NoSuchElementException.class)
	public void testGetCondoBillBy_givenInvalidId_shouldThrowException() {
		repository.getCondoBilldBy(69);
	}

	@Test
	public void testGetStatsBy_givenValidCondoId_shouldReturnStats() {
		BilltStats expected = new BilltStats(3, 2, 3, 2, 40);
		BilltStats actual = repository.getStatsBy(1, LocalDate.of(2016, 2, 23), LocalDate.of(2017, 10, 23));
		assertEquals(expected, actual);
	}

	@Test(expected = NoSuchElementException.class)
	public void testGetStatsBy_givenInvalidCondoId() {
		repository.getStatsBy(69, LocalDate.of(2016, 2, 23), LocalDate.of(2017, 10, 23));
	}

	@Test
	public void testFindBy_givenValidCondoIdAndPaymentStatusList_shouldReturnList() {
		List<CondoBill> expected = Arrays.asList(
				new CondoBill(11, "consumo de gas", LocalDate.of(2017, 7, 1), 100,
						PaymentStatus.PAID_AWAITING_CONFIRMATION, LocalDate.of(2017, 7, 1), PaymentMethod.CHECK,
						ProofOfPaymentExtension.JPG, new Apartment("1D", new User("aldo", Role.RESIDENT))),
				new CondoBill(13, "iluminación del pasillo", LocalDate.of(2017, 7, 10), 350,
						PaymentStatus.PAID_AWAITING_CONFIRMATION, LocalDate.of(2017, 7, 10), PaymentMethod.CHECK,
						ProofOfPaymentExtension.PNG, new Apartment("1A", new User("virgi", Role.RESIDENT))));
		List<CondoBill> actual = repository.findBy(1, Arrays.asList(PaymentStatus.PAID_AWAITING_CONFIRMATION));
		assertEquals(expected, actual);
		assertCondoBillListEquals(expected, actual);
	}

	@Test
	public void testFindBy_givenValidCondoIdAndPaymentStatusListForCondoWithoutPayments_shouldReturnEmptyList() {
		List<CondoBill> actual = repository.findBy(3, Arrays.asList(PaymentStatus.PAID_AWAITING_CONFIRMATION));
		assertCondoBillListEquals(Collections.emptyList(), actual);
	}

	@Test
	public void testFindBy_givenInvalidCondoIdAndPaymentStatusList_shouldReturnEmptyList() {
		List<CondoBill> actual = repository.findBy(69, Arrays.asList(PaymentStatus.PAID_AWAITING_CONFIRMATION));
		assertCondoBillListEquals(Collections.emptyList(), actual);
	}

	@Test
	public void testFindBy_givenValidCondoUsernameAndPaymentStatusList_shouldReturnList() {
		List<Bill> expected = Arrays.asList(
				new Bill(8, "cuota mensual", LocalDate.of(2017, 06, 27), 10, PaymentStatus.REJECTED,
						LocalDate.of(2017, 06, 27), PaymentMethod.TRANSFER, ProofOfPaymentExtension.PNG),
				new Bill(10, "cuota mensual", LocalDate.of(2017, 07, 01), 10, PaymentStatus.PENDING,
						LocalDate.of(2017, 07, 01)),
				new Bill(12, "consumo de gas", LocalDate.of(2017, 07, 01), 200, PaymentStatus.REJECTED,
						LocalDate.of(2017, 07, 01), PaymentMethod.DEPOSIT, ProofOfPaymentExtension.JPG));
		List<Bill> actual = repository.findBy(1, "aldo", Arrays.asList(PaymentStatus.PENDING, PaymentStatus.REJECTED));
		assertEquals(expected, actual);
		assertBillListEquals(expected, actual);
	}

	@Test
	public void testFindBy_givenInvalidCondId_shouldReturnEmptyList() {
		List<Bill> actual = repository.findBy(69, "aldo", Arrays.asList(PaymentStatus.PENDING));
		assertEquals(Collections.EMPTY_LIST, actual);
	}

	@Test
	public void testFindBy_givenInvalidUsername_shouldReturnEmptyList() {
		List<Bill> actual = repository.findBy(1, "fakeusername", Arrays.asList(PaymentStatus.PENDING));
		assertEquals(Collections.EMPTY_LIST, actual);
	}

	@Test
	public void testFindBy_givenValidCondoAndUsernameForApartmentWithoutPayments_shouldReturnEmptyList() {
		List<Bill> actual = repository.findBy(3, "mary", Arrays.asList(PaymentStatus.PENDING, PaymentStatus.REJECTED,
				PaymentStatus.PAID_AWAITING_CONFIRMATION, PaymentStatus.PAID_CONFIRMED));
		assertEquals(Collections.EMPTY_LIST, actual);
	}

	@Test
	public void testUpdatePaymentInfo_givenPaymentStatusMethodAndExtension_shouldPerformUpdate() {
		int billId = 10;
		repository.updatePaymentInfo(billId, PaymentStatus.PAID_AWAITING_CONFIRMATION, PaymentMethod.TRANSFER,
				ProofOfPaymentExtension.PNG);
		Bill expected = new Bill(10, "cuota mensual", LocalDate.of(2017, 07, 01), 10,
				PaymentStatus.PAID_AWAITING_CONFIRMATION, LocalDate.now(), PaymentMethod.TRANSFER,
				ProofOfPaymentExtension.PNG);
		Bill actual = repository.getBy(billId);
		assertEquals(expected, actual);
	}

	@Test
	public void testUpdatePaymentInfo_givenPaymentStatusAndMethod_shouldPerformUpdate() {
		int billId = 8;
		Bill expected = new Bill(billId, "cuota mensual", LocalDate.of(2017, 06, 27), 10, PaymentStatus.REJECTED,
				LocalDate.now(), PaymentMethod.CHECK, ProofOfPaymentExtension.PNG);
		repository.updatePaymentInfo(billId, PaymentStatus.PAID_AWAITING_CONFIRMATION, PaymentMethod.CHECK);
		Bill actual = repository.getBy(billId);
		assertEquals(expected, actual);
	}

	private void assertCondoBillEquals(CondoBill expected, CondoBill actual) {
		assertBillEquals(expected, actual);
		assertEquals(expected.getApartment(), expected.getApartment());
		assertEquals(expected.getApartment().getResident(), actual.getApartment().getResident());
	}

	private void assertBillEquals(Bill expected, Bill actual) {
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getDescription(), expected.getDescription());
		assertEquals(expected.getDueDate(), expected.getDueDate());
		assertTrue(Math.abs(expected.getDueAmount() - expected.getDueAmount()) == 0);
		assertEquals(expected.getPaymentStatus(), expected.getPaymentStatus());
		assertEquals(expected.getLastUpdateOn(), expected.getLastUpdateOn());
		assertEquals(expected.getPaymentMethod(), expected.getPaymentMethod());
		assertEquals(expected.getProofOfPaymentExtension(), actual.getProofOfPaymentExtension());
	}

	private void assertBillListEquals(List<Bill> expected, List<Bill> actual) {
		for (int i = 0; i < expected.size(); i++) {
			assertBillEquals(expected.get(i), actual.get(i));
		}
	}

	private void assertCondoBillListEquals(List<CondoBill> expected, List<CondoBill> actual) {
		for (int i = 0; i < expected.size(); i++) {
			assertCondoBillEquals(expected.get(i), actual.get(i));
		}
	}
}
