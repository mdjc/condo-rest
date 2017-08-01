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
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.mdjc.config.BeansConfig;
import com.github.mdjc.domain.Outlay;
import com.github.mdjc.domain.OutlayCategory;
import com.github.mdjc.domain.OutlayRepository;
import com.github.mdjc.domain.OutlayStats;
import com.github.mdjc.domain.PaginationCriteria;
import com.github.mdjc.domain.PaginationCriteria.SortingOrder;

@RunWith(SpringRunner.class)
@JdbcTest
@Import(BeansConfig.class)
public class JdbcOutlayRepositoryTest {
	@Autowired
	private NamedParameterJdbcTemplate template;	
	private OutlayRepository repository;
	
	@Before
	public void init() {
		repository = new JdbcOutlayRepository(template);
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
		List<Outlay> expected = Arrays.asList(
				new Outlay(1, OutlayCategory.SECURITY, 15, LocalDate.of(2017, 6, 16), "Watchman Dominicana", ""));
		List<Outlay> actual = repository.findBy(1, LocalDate.of(2017, 6, 16), null, new PaginationCriteria(0, 1));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testFindBy_givenFromToLimitAndSortAsc_shouldReturnTwoOutlays() {
		List<Outlay> expected = Arrays.asList(
				new Outlay(1, OutlayCategory.SECURITY, 15, LocalDate.of(2017, 6, 16), "Watchman Dominicana", ""),
				new Outlay(2, OutlayCategory.REPARATION, 10, LocalDate.of(2017, 7, 16), "Edenorte",
						"Reparación Lámpara Pasillo"));
		List<Outlay> actual = repository.findBy(1, LocalDate.of(2017, 6, 16), LocalDate.of(2017, 8, 16),
				new PaginationCriteria(0, 2));
		testEquals(expected, actual);
	}


	@Test
	public void testFindBy_givenFromToOffsetLimitAndSortAsc_shouldReturnOneOutlay() {
		List<Outlay> expected = Arrays.asList(new Outlay(2, OutlayCategory.REPARATION, 10, LocalDate.of(2017, 7, 16),
				"Edenorte", "Reparación Lámpara Pasillo"));
		List<Outlay> actual = repository.findBy(1, LocalDate.of(2017, 6, 16), LocalDate.of(2017, 8, 16),
				new PaginationCriteria(1, 1));
		testEquals(expected, actual);
	}
	
	@Test
	public void testFindBy_givenFromToLimitAndSortDesc_shouldReturnOneOutlaySortedDesc() {
		List<Outlay> expected = Arrays.asList(new Outlay(4, OutlayCategory.SECURITY, 55.36, LocalDate.of(2017, 8, 16),
				"Watchman & Asocs", ""));
		List<Outlay> actual = repository.findBy(2, LocalDate.of(2017, 6, 16), LocalDate.of(2017, 8, 16),
				new PaginationCriteria(0, 1, SortingOrder.DESC));
		testEquals(expected, actual);
	}
	
	@Test
	public void testFindBy_givenFromToOffsetLimitAndSortDesc_shouldReturnOneOutlaySortedDesc() {
		List<Outlay> expected = Arrays.asList(new Outlay(3, OutlayCategory.REPARATION, 10, LocalDate.of(2017, 6, 16),
				"Edenorte", "Reparación Lámpara Principal"));
		List<Outlay> actual = repository.findBy(2, LocalDate.of(2017, 6, 16), LocalDate.of(2017, 8, 16),
				new PaginationCriteria(1, 1, SortingOrder.DESC));
		testEquals(expected, actual);
	}

	@Test
	public void testCountFindBy_givenValidCondoIdFromAndTo_shouldReturnCount() {
		int expected = 2;
		int actual = repository.countFindBy(1, LocalDate.of(2017, 6, 16), LocalDate.of(2017, 7, 16));
		assertEquals(expected, actual);
	}
	
	private void testEquals(List<Outlay> expected, List<Outlay> actual) {
		assertEquals(expected, actual);

		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).getId(), actual.get(i).getId());
			assertEquals(expected.get(i).getCategory(), actual.get(i).getCategory());
			assertEquals(expected.get(i).getCreatedOn(), actual.get(i).getCreatedOn());
			assertEquals(expected.get(i).getSupplier(), actual.get(i).getSupplier());
			assertEquals(expected.get(i).getComment(), actual.get(i).getComment());
			assertTrue(expected.get(i).getAmount() - actual.get(i).getAmount() == 0);
		}
	}
}
