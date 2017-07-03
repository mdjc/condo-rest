package com.github.mdjc.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
public class OutlaysRepositoryTest {
	@Autowired
	OutlayRepository repository;

	@Test
	public void testFindBy_GivenFromToAndLimit_ShouldReturnTwoOutlays() {
		List<Outlay> expected = Arrays.asList(
				new Outlay(1, OutlayCategory.SECURITY, 15, LocalDate.of(2017, 6, 16), "Watchman Dominicana", ""),
				new Outlay(2, OutlayCategory.REPARATION, 10, LocalDate.of(2017, 7, 16), "Edenorte",
						"Reparación Lámpara Pasillo"));
		List<Outlay> actual = repository.findBy(1, LocalDate.of(2017, 6, 16), LocalDate.of(2017, 8, 16),
				new PaginationCriteria(0, 2));
		testEquals(expected, actual);
	}

	@Test
	public void testFindBy_GivenFromAndLimit_ShouldReturnOneOutlay() {
		List<Outlay> expected = Arrays.asList(
				new Outlay(1, OutlayCategory.SECURITY, 15, LocalDate.of(2017, 6, 16), "Watchman Dominicana", ""));
		List<Outlay> actual = repository.findBy(1, LocalDate.of(2017, 6, 16), null, new PaginationCriteria(0, 1));
		assertEquals(expected, actual);
	}

	@Test
	public void testFindBy_GivenFromToAndLimitSortDesc_ShouldReturnTwoOutlaysSortedDesc() {
		List<Outlay> expected = Arrays.asList(
				new Outlay(1, OutlayCategory.SECURITY, 15, LocalDate.of(2017, 6, 16), "Watchman Dominicana", ""),
				new Outlay(2, OutlayCategory.REPARATION, 10, LocalDate.of(2017, 7, 16), "Edenorte",
						"Reparación Lámpara Pasillo"));
		List<Outlay> actual = repository.findBy(1, LocalDate.of(2017, 6, 16), LocalDate.of(2017, 8, 16),
				new PaginationCriteria(0, 2));
		testEquals(expected, actual);
	}

	@Test
	public void testFindBy_GivenFromToOffsetAndLimitSortDesc_ShouldReturnOneOutlaySortedDesc() {
		List<Outlay> expected = Arrays.asList(new Outlay(2, OutlayCategory.REPARATION, 10, LocalDate.of(2017, 7, 16),
				"Edenorte", "Reparación Lámpara Pasillo"));
		List<Outlay> actual = repository.findBy(1, LocalDate.of(2017, 6, 16), LocalDate.of(2017, 8, 16),
				new PaginationCriteria(1, 1));
		testEquals(expected, actual);
	}

	@Test
	public void testGetStatsBy_GivenValidCondoFromAndTo_ShouldReturnOUtlay() {
		OutlayStats expected = new OutlayStats(25);
		OutlayStats actual = repository.getStatsBy(1, LocalDate.of(2017, 6, 16), LocalDate.of(2017, 7, 16));
		assertTrue(expected.getAmount() - actual.getAmount() == 0);
	}

	@Test(expected = NoSuchElementException.class)
	public void testGetStatsBy_GivenInvalidCondoFromAndTo_ShouldThrowException() {
		repository.getStatsBy(69, LocalDate.of(2017, 6, 16), LocalDate.of(2017, 7, 16));
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
