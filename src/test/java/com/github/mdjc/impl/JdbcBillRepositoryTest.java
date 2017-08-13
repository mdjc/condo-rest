package com.github.mdjc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.mdjc.config.BeansConfig;
import com.github.mdjc.domain.Apartment;
import com.github.mdjc.domain.Bill;
import com.github.mdjc.domain.BillRepository;
import com.github.mdjc.domain.BilltStats;
import com.github.mdjc.domain.CondoBill;
import com.github.mdjc.domain.ImageExtension;
import com.github.mdjc.domain.PaginationCriteria;
import com.github.mdjc.domain.PaymentMethod;
import com.github.mdjc.domain.PaymentStatus;
import com.github.mdjc.domain.Role;
import com.github.mdjc.domain.User;

@RunWith(SpringRunner.class)
@JdbcTest
@Import(BeansConfig.class)
public class JdbcBillRepositoryTest {

	@Value("${myproperty}")
	String myp;

	@Autowired
	private NamedParameterJdbcTemplate template;
	private BillRepository repository;

	@Before
	public void init() {
		System.out.println(myp);
		repository = new JdbcBillRepository(template);
	}

	@Test
	public void testGetBy_givenValidId_shouldReturnBill() {
		Bill expected = new Bill(3, "cuota mensual", LocalDate.of(2017, 6, 15), 20, PaymentStatus.PAID_CONFIRMED,
				LocalDate.of(2017, 6, 15), PaymentMethod.CHECK, ImageExtension.JPG);
		Bill actual = repository.getBy(3);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetBy_givenValidIdAndManager_shouldReturnBill() {
		Bill expected = new Bill(3, "cuota mensual", LocalDate.of(2017, 6, 15), 20, PaymentStatus.PAID_CONFIRMED,
				LocalDate.of(2017, 6, 15), PaymentMethod.CHECK, ImageExtension.JPG);
		Bill actual = repository.getBy(3, new User("mirna", Role.MANAGER));
		assertEquals(expected, actual);
	}

	@Test
	public void testGetBy_givenValidIdAndResident_shouldReturnBill() {
		Bill expected = new Bill(3, "cuota mensual", LocalDate.of(2017, 6, 15), 20, PaymentStatus.PAID_CONFIRMED,
				LocalDate.of(2017, 6, 15), PaymentMethod.CHECK, ImageExtension.JPG);
		Bill actual = repository.getBy(3, new User("aldo", Role.RESIDENT));
		assertEquals(expected, actual);
	}

	@Test(expected = NoSuchElementException.class)
	public void testGetBy_givenInvalidIdWithManager_shouldThrowException() {
		repository.getBy(69, new User("mirna", Role.MANAGER));
	}

	@Test(expected = NoSuchElementException.class)
	public void testGetBy_givenInvalidIdWithResident_shouldThrowException() {
		repository.getBy(69, new User("aldo", Role.RESIDENT));
	}

	@Test(expected = NoSuchElementException.class)
	public void testGetByGivenInvalidId_shouldThrowException() {
		repository.getBy(69);
	}

	@Test
	public void testGetCondoBillBy_givenValidId_shouldReturnCondoBill() {
		CondoBill expected = new CondoBill(3, "cuota mensual", LocalDate.of(2017, 6, 15), 20,
				PaymentStatus.PAID_CONFIRMED, LocalDate.of(2017, 6, 15), PaymentMethod.CHECK, ImageExtension.JPG,
				new Apartment("1D"));
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
						ImageExtension.JPG, new Apartment("1D", new User("aldo", Role.RESIDENT))),
				new CondoBill(13, "iluminación del pasillo", LocalDate.of(2017, 7, 10), 350,
						PaymentStatus.PAID_AWAITING_CONFIRMATION, LocalDate.of(2017, 7, 10), PaymentMethod.CHECK,
						ImageExtension.PNG, new Apartment("1A", new User("virgi", Role.RESIDENT))));
		PaginationCriteria criteria = new PaginationCriteria(0, 200);
		List<CondoBill> actual = repository.findBy(1, Arrays.asList(PaymentStatus.PAID_AWAITING_CONFIRMATION), null,
				null, criteria);
		assertEquals(expected, actual);
		assertCondoBillListEquals(expected, actual);
	}

	@Test
	public void testFindBy_givenValidCondoIdAndPaymentStatusListForCondoWithoutPayments_shouldReturnEmptyList() {
		PaginationCriteria criteria = new PaginationCriteria(0, 200);
		List<CondoBill> actual = repository.findBy(3, Arrays.asList(PaymentStatus.PAID_AWAITING_CONFIRMATION), null,
				null, criteria);
		assertCondoBillListEquals(Collections.emptyList(), actual);
	}

	@Test
	public void testFindBy_givenInvalidCondoIdAndPaymentStatusList_shouldReturnEmptyList() {
		PaginationCriteria criteria = new PaginationCriteria(0, 200);
		List<CondoBill> actual = repository.findBy(69, Arrays.asList(PaymentStatus.PAID_AWAITING_CONFIRMATION), null,
				null, criteria);
		assertCondoBillListEquals(Collections.emptyList(), actual);
	}

	@Test
	public void testFindBy_givenValidCondoIdPaymentStatusListAndFrom_shouldReturnList() {
		List<CondoBill> expected = Arrays.asList(new CondoBill(13, "iluminación del pasillo", LocalDate.of(2017, 7, 10),
				350, PaymentStatus.PAID_AWAITING_CONFIRMATION, LocalDate.of(2017, 7, 10), PaymentMethod.CHECK,
				ImageExtension.PNG, new Apartment("1A", new User("virgi", Role.RESIDENT))));
		PaginationCriteria criteria = new PaginationCriteria(0, 200);
		List<CondoBill> actual = repository.findBy(1, Arrays.asList(PaymentStatus.PAID_AWAITING_CONFIRMATION),
				LocalDate.of(2017, 7, 10), null, criteria);
		assertEquals(expected, actual);
		assertCondoBillListEquals(expected, actual);
	}

	@Test
	public void testFindBy_givenValidCondoIdPaymentStatusListAndTo_shouldReturnList() {
		List<CondoBill> expected = Arrays.asList(
				new CondoBill(11, "consumo de gas", LocalDate.of(2017, 7, 1), 100,
						PaymentStatus.PAID_AWAITING_CONFIRMATION, LocalDate.of(2017, 7, 1), PaymentMethod.CHECK,
						ImageExtension.JPG, new Apartment("1D", new User("aldo", Role.RESIDENT))),
				new CondoBill(13, "iluminación del pasillo", LocalDate.of(2017, 7, 10), 350,
						PaymentStatus.PAID_AWAITING_CONFIRMATION, LocalDate.of(2017, 7, 10), PaymentMethod.CHECK,
						ImageExtension.PNG, new Apartment("1A", new User("virgi", Role.RESIDENT))));
		PaginationCriteria criteria = new PaginationCriteria(0, 10);
		List<CondoBill> actual = repository.findBy(1, Arrays.asList(PaymentStatus.PAID_AWAITING_CONFIRMATION), null,
				LocalDate.of(2017, 7, 10), criteria);
		assertEquals(expected, actual);
		assertCondoBillListEquals(expected, actual);
	}

	@Test
	public void testFindBy_givenValidCondoIdPaymentStatusListAndToWithOffset_shouldReturnList() {
		List<CondoBill> expected = Arrays.asList(new CondoBill(13, "iluminación del pasillo", LocalDate.of(2017, 7, 10),
				350, PaymentStatus.PAID_AWAITING_CONFIRMATION, LocalDate.of(2017, 7, 10), PaymentMethod.CHECK,
				ImageExtension.PNG, new Apartment("1A", new User("virgi", Role.RESIDENT))));
		PaginationCriteria criteria = new PaginationCriteria(1, 10);
		List<CondoBill> actual = repository.findBy(1, Arrays.asList(PaymentStatus.PAID_AWAITING_CONFIRMATION), null,
				LocalDate.of(2017, 7, 10), criteria);
		assertEquals(expected, actual);
		assertCondoBillListEquals(expected, actual);
	}

	@Test
	public void testFindBy_givenValidCondoIdPaymentStatusListFromAndTo_shouldReturnList() {
		List<CondoBill> expected = Arrays.asList(
				new CondoBill(11, "consumo de gas", LocalDate.of(2017, 7, 1), 100,
						PaymentStatus.PAID_AWAITING_CONFIRMATION, LocalDate.of(2017, 7, 1), PaymentMethod.CHECK,
						ImageExtension.JPG, new Apartment("1D", new User("aldo", Role.RESIDENT))),
				new CondoBill(9, "cuota mensual", LocalDate.of(2017, 7, 1), 10, PaymentStatus.PAID_CONFIRMED,
						LocalDate.of(2017, 7, 1), PaymentMethod.TRANSFER, ImageExtension.PNG,
						new Apartment("1A", new User("virgi", Role.RESIDENT))),
				new CondoBill(13, "iluminación del pasillo", LocalDate.of(2017, 7, 10), 350,
						PaymentStatus.PAID_AWAITING_CONFIRMATION, LocalDate.of(2017, 7, 10), PaymentMethod.CHECK,
						ImageExtension.PNG, new Apartment("1A", new User("virgi", Role.RESIDENT))));
		PaginationCriteria criteria = new PaginationCriteria(0, 10);
		List<CondoBill> actual = repository.findBy(1,
				Arrays.asList(PaymentStatus.PAID_AWAITING_CONFIRMATION, PaymentStatus.PAID_CONFIRMED),
				LocalDate.of(2017, 7, 1), LocalDate.of(2017, 7, 10), criteria);
		assertEquals(expected, actual);
		assertCondoBillListEquals(expected, actual);
	}

	@Test
	public void testCountFindBy_givenValidCondoIdPaymentStatusListFromAndTo_shouldReturnCount() {
		int expected = 3;
		int actual = repository.countFindBy(1,
				Arrays.asList(PaymentStatus.PAID_AWAITING_CONFIRMATION, PaymentStatus.PAID_CONFIRMED),
				LocalDate.of(2017, 7, 1), LocalDate.of(2017, 7, 10));
		assertEquals(expected, actual);
	}

	@Test
	public void testFindBy_givenValidCondoUsernameAndPaymentStatusList_shouldReturnList() {
		List<Bill> expected = Arrays.asList(
				new Bill(8, "cuota mensual", LocalDate.of(2017, 06, 27), 10, PaymentStatus.REJECTED,
						LocalDate.of(2017, 06, 27), PaymentMethod.TRANSFER, ImageExtension.PNG),
				new Bill(10, "cuota mensual", LocalDate.of(2017, 07, 01), 10, PaymentStatus.PENDING,
						LocalDate.of(2017, 07, 01)),
				new Bill(12, "consumo de gas", LocalDate.of(2017, 07, 01), 200, PaymentStatus.REJECTED,
						LocalDate.of(2017, 07, 01), PaymentMethod.DEPOSIT, ImageExtension.JPG));
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
	public void testAddBill_givenValidApartmentDueDateAmountAndDescription_shouldAddBill() {
		CondoBill bill = new CondoBill("something", LocalDate.now(), 100, PaymentStatus.PENDING, LocalDate.now(),
				new Apartment("1D"));
		repository.add(1, bill);
		CondoBill expected = new CondoBill(14, "something", LocalDate.now(), 100, PaymentStatus.PENDING,
				LocalDate.now(), new Apartment("1D"));
		assertCondoBillEquals(expected, repository.getCondoBilldBy(14));
	}

	@Test
	public void testDeleteBill_givenValidId_shouldDeleteBill() {
		long billId = 5;
		Bill bill = repository.getBy(billId);
		assertTrue(bill != null);
		repository.delete(billId);

		try {
			repository.getBy(billId);
		} catch (NoSuchElementException e) {
			assertTrue(true);
		}
	}

	@Test(expected = NoSuchElementException.class)
	public void testDeleteBill_givenInvalidId_shouldThrowException() {
		repository.delete(69);
	}

	@Test
	public void testUpdatePaymentInfo_givenPaymentStatusMethodAndExtension_shouldPerformUpdate() {
		int billId = 10;
		repository.updatePaymentInfo(billId, PaymentStatus.PAID_AWAITING_CONFIRMATION, PaymentMethod.TRANSFER,
				ImageExtension.PNG);
		Bill expected = new Bill(10, "cuota mensual", LocalDate.of(2017, 07, 01), 10,
				PaymentStatus.PAID_AWAITING_CONFIRMATION, LocalDate.now(), PaymentMethod.TRANSFER, ImageExtension.PNG);
		Bill actual = repository.getBy(billId);
		assertEquals(expected, actual);
	}

	@Test
	public void testUpdatePaymentInfo_givenPaymentStatusAndMethod_shouldPerformUpdate() {
		int billId = 8;
		Bill expected = new Bill(billId, "cuota mensual", LocalDate.of(2017, 06, 27), 10, PaymentStatus.REJECTED,
				LocalDate.now(), PaymentMethod.CHECK, ImageExtension.PNG);
		repository.updatePaymentInfo(billId, PaymentStatus.PAID_AWAITING_CONFIRMATION, PaymentMethod.CHECK);
		Bill actual = repository.getBy(billId);
		assertEquals(expected, actual);
	}

	@Test
	public void testUpdatePaymentInfo_givenPaymentStatus_shouldPerformUpdate() {
		int billId = 13;
		Bill expected = new Bill(billId, "iluminación del pasillo", LocalDate.of(2017, 07, 10), 10,
				PaymentStatus.PAID_CONFIRMED, LocalDate.now(), PaymentMethod.CHECK, ImageExtension.PNG);
		repository.updatePaymentInfo(billId, PaymentStatus.PAID_CONFIRMED);
		Bill actual = repository.getBy(billId);
		assertEquals(expected, actual);
	}

	private void assertCondoBillEquals(CondoBill expected, CondoBill actual) {
		assertBillEquals(expected, actual);
		assertEquals(expected.getApartment(), expected.getApartment());
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
