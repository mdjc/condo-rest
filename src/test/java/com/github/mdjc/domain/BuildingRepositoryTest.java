package com.github.mdjc.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
public class BuildingRepositoryTest {
	@Autowired
	BuildingRepository repository;

	@Test
	public void testGetAllByUser_givenManager_shouldReturnOneBuilding() {
		User user = new User("luis", Role.MANAGER);
		List<Building> expected = Arrays.asList(new Building(3, "Mira Flores IV", user));
		List<Building> actual = repository.getAllByUser(user);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetAllByUser_givenResident_shouldReturnOneBuilding() {
		User user = new User("virgi", Role.RESIDENT);
		List<Building> expected = Arrays.asList(new Building(1, "Shadai I", user));
		List<Building> actual = repository.getAllByUser(user);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetAllByUser_givenManager_shouldReturnSeveralBuildings() {
		User user = new User("mirna", Role.MANAGER);
		List<Building> expected = Arrays.asList(new Building(2, "Loring  Place 2333", user),
				new Building(1, "Shadai I", user));
		List<Building> actual = repository.getAllByUser(user);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetAllByUser_givenResident_shouldReturnSeveralBuildings() {
		User user = new User("mary", Role.RESIDENT);
		List<Building> expected = Arrays.asList(new Building(2, "Loring  Place 2333", user),
				new Building(3, "Mira Flores IV", user));
		List<Building> actual = repository.getAllByUser(user);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetAllByUser_givenUnexistentUser_shouldReturnEmptyList() {
		User user = new User("unexistent", Role.MANAGER);
		assertTrue(repository.getAllByUser(user).isEmpty());
	}
	
	@Test
	public void testGetStatsById_givenValidId_shouldReturnStatistics() {
		BuildingStats actual = repository.getStatsById(1);
		Building expectedBuilding = new Building(1, "Shadai I", new User("mirna", Role.MANAGER));
		BuildingStats expected = new BuildingStats(expectedBuilding, 4, 2, 100);
		
		assertEquals(expectedBuilding, actual.getBuilding());
		assertEquals(expectedBuilding.getName(), actual.getBuilding().getName());
		assertEquals(expectedBuilding.getManager().getUsername(), actual.getBuilding().getManager().getUsername());
		assertEquals(expected.getApartmentCount(), actual.getApartmentCount());
		assertEquals(expected.getResidentCount(), actual.getResidentCount());
		assertTrue(Math.abs(expected.getBalance() - actual.getBalance()) == 0);
		
	}
	
	
	@Test(expected=NoSuchElementException.class)
	public void testGetStatsById_givenInvalidId_shouldThrowException() {
		repository.getStatsById(69);		
	}
}
