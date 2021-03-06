package com.github.mdjc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
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

import com.github.mdjc.domain.ImageExtension;
import com.github.mdjc.domain.Outlay;
import com.github.mdjc.domain.OutlayCategory;
import com.github.mdjc.domain.OutlayRepository;
import com.github.mdjc.domain.OutlayStats;
import com.github.mdjc.domain.PaginationCriteria;
import com.github.mdjc.domain.PaginationCriteria.SortingOrder;
import com.github.mdjc.domain.Role;
import com.github.mdjc.domain.User;

@RunWith(SpringRunner.class)
@JdbcTest
public class JdbcOutlayRepositoryTest {
	@Autowired
	private NamedParameterJdbcTemplate template;
	private OutlayRepository repository;

	@Before
	public void init() {
		repository = new JdbcOutlayRepository(template);
	}

	@Test
	public void testGetBy_givenValidId_shouldReturnOutlay() {
		long id = 4;
		Outlay expected = new Outlay(id, OutlayCategory.SECURITY, 55.36, "Watchman & Asocs",
				"", ImageExtension.JPG, LocalDate.of(2017, 8, 16));
		Outlay actual = repository.getBy(id);
		assertOutlayEquals(expected, actual);
	}

	@Test(expected = NoSuchElementException.class)
	public void testGetBy_givenInvalidId_shouldThrowException() {
		repository.getBy(69);
	}
	
	@Test
	public void testGetBy_givenValidIdAndManager_shouldReturnOutlay() {
		long id = 4;
		Outlay expected = new Outlay(id, OutlayCategory.SECURITY, 55.36, "Watchman & Asocs",
				"", ImageExtension.JPG, LocalDate.of(2017, 8, 16));
		Outlay actual = repository.getBy(id, new User("mirna", Role.MANAGER));
		assertOutlayEquals(expected, actual);
	}
	
	@Test
	public void testGetBy_givenValidIdAndResident_shouldReturnOutlay() {
		long id = 4;
		Outlay expected = new Outlay(id, OutlayCategory.SECURITY, 55.36, "Watchman & Asocs",
				"", ImageExtension.JPG, LocalDate.of(2017, 8, 16));
		Outlay actual = repository.getBy(id, new User("john", Role.RESIDENT));
		assertOutlayEquals(expected, actual);
	}
	
	@Test(expected = NoSuchElementException.class)
	public void testGetBy_givenInvalidIdForManager_shouldThrowException() {
		repository.getBy(69, new User("mirna", Role.MANAGER));
	}
	
	@Test(expected = NoSuchElementException.class)
	public void testGetBy_givenInvalidIdForResident_shouldThrowException() {
		repository.getBy(69, new User("virgi", Role.RESIDENT));
	}
	

	@Test
	public void testGetStatsBy_givenValidCondoFromAndTo_shouldReturnOutlay() {
		OutlayStats expected = new OutlayStats(25);
		OutlayStats actual = repository.getStatsBy(1, LocalDate.of(2017, 6, 16), LocalDate.of(2017, 7, 16));
		assertTrue(expected.getAmount() - actual.getAmount() == 0);
	}

	@Test(expected = NoSuchElementException.class)
	public void testGetStatsBy_givenInvalidCondoFromAndTo_shouldThrowException() {
		repository.getStatsBy(69, LocalDate.of(2017, 6, 16), LocalDate.of(2017, 7, 16));
	}

	@Test
	public void testFindBy_givenFromLimitAndSortAsc_shouldReturnOneOutlay() {
		List<Outlay> expected = Arrays.asList(new Outlay(1, OutlayCategory.SECURITY, 15, "Watchman Dominicana", "",
				ImageExtension.JPG, LocalDate.of(2017, 6, 16)));
		List<Outlay> actual = repository.findBy(1, LocalDate.of(2017, 6, 16), null, new PaginationCriteria(0, 1));
		assertEquals(expected, actual);
	}

	@Test
	public void testFindBy_givenFromToLimitAndSortAsc_shouldReturnTwoOutlays() {
		List<Outlay> expected = Arrays.asList(
				new Outlay(1, OutlayCategory.SECURITY, 15, "Watchman Dominicana", "", ImageExtension.JPG,
						LocalDate.of(2017, 6, 16)),
				new Outlay(2, OutlayCategory.REPARATION, 10, "Edenorte", "Reparación Lámpara Pasillo",
						ImageExtension.JPG, LocalDate.of(2017, 7, 16)));
		List<Outlay> actual = repository.findBy(1, LocalDate.of(2017, 6, 16), LocalDate.of(2017, 8, 16),
				new PaginationCriteria(0, 2));
		assertOutlayListEquals(expected, actual);
	}

	@Test
	public void testFindBy_givenFromToOffsetLimitAndSortAsc_shouldReturnOneOutlay() {
		List<Outlay> expected = Arrays.asList(new Outlay(2, OutlayCategory.REPARATION, 10, 
				"Edenorte", "Reparación Lámpara Pasillo", ImageExtension.JPG, LocalDate.of(2017, 7, 16)));
		List<Outlay> actual = repository.findBy(1, LocalDate.of(2017, 6, 16), LocalDate.of(2017, 8, 16),
				new PaginationCriteria(1, 1));
		assertOutlayListEquals(expected, actual);
	}

	@Test
	public void testFindBy_givenFromToLimitAndSortDesc_shouldReturnOneOutlaySortedDesc() {
		List<Outlay> expected = Arrays.asList(new Outlay(4, OutlayCategory.SECURITY, 55.36, "Watchman & Asocs", "",
				ImageExtension.JPG, LocalDate.of(2017, 8, 16)));
		List<Outlay> actual = repository.findBy(2, LocalDate.of(2017, 6, 16), LocalDate.of(2017, 8, 16),
				new PaginationCriteria(0, 1, SortingOrder.DESC));
		assertOutlayListEquals(expected, actual);
	}

	@Test
	public void testFindBy_givenFromToOffsetLimitAndSortDesc_shouldReturnOneOutlaySortedDesc() {
		List<Outlay> expected = Arrays.asList(new Outlay(3, OutlayCategory.REPARATION, 10, "Edenorte",
				"Reparación Lámpara Principal", ImageExtension.PNG, LocalDate.of(2017, 6, 16)));
		List<Outlay> actual = repository.findBy(2, LocalDate.of(2017, 6, 16), LocalDate.of(2017, 8, 16),
				new PaginationCriteria(1, 1, SortingOrder.DESC));
		assertOutlayListEquals(expected, actual);
	}

	@Test
	public void testCountFindBy_givenValidCondoIdFromAndTo_shouldReturnCount() {
		int expected = 2;
		int actual = repository.countFindBy(1, LocalDate.of(2017, 6, 16), LocalDate.of(2017, 7, 16));
		assertEquals(expected, actual);
	}

	@Test
	public void testAddGivenValidCondoIdAndOutlay_shouldAddOutlay() {
		repository.add(2, new Outlay(OutlayCategory.CLEANING, 400, "Casa Limpia Asoc",
				"Limpieza del Tinaco", ImageExtension.PNG, LocalDate.now()));

		Outlay expected = new Outlay(5, OutlayCategory.CLEANING, 400, "Casa Limpia Asoc",
				"Limpieza del Tinaco", ImageExtension.PNG, LocalDate.now());
		Outlay actual = repository.getBy(expected.getId());
		assertOutlayEquals(expected, actual);
	}
	
	@Test
	public void testDeleteGivenValidOutlayId_ShouldDelete() {
		int outlayId = 1;
		Outlay outlay = repository.getBy(outlayId);
		assertTrue(outlay != null);
		
		repository.delete(outlayId);
		try {
			repository.getBy(outlayId);
		} catch(NoSuchElementException e) {
			assertTrue(true);
		}
	}

	@Test(expected = NoSuchElementException.class)
	public void testDeleteGivenInvalidOutlayId_shouldThrowException() {
		repository.delete(69);
	}
	
	private void assertOutlayListEquals(List<Outlay> expected, List<Outlay> actual) {
		assertEquals(expected, actual);

		for (int i = 0; i < expected.size(); i++) {
			assertOutlayEquals(expected.get(i), actual.get(i));
		}
	}

	private void assertOutlayEquals(Outlay expected, Outlay actual) {
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getCategory(), actual.getCategory());
		assertEquals(expected.getCreatedOn(), actual.getCreatedOn());
		assertEquals(expected.getSupplier(), actual.getSupplier());
		assertEquals(expected.getComment(), actual.getComment());
		assertTrue(expected.getAmount() - actual.getAmount() == 0);
		assertEquals(expected.getReceiptImageExtension(), actual.getReceiptImageExtension());
	}
}
