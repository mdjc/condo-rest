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
		List<Building> expected = Arrays.asList(new Building(2, "Loring  Place 2333",  new User("mirna", Role.MANAGER)),
				new Building(3, "Mira Flores IV",  new User("luis", Role.MANAGER)));
		List<Building> actual = repository.getAllByUser(user);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetAllByUser_givenUnexistentUser_shouldReturnEmptyList() {
		User user = new User("unexistent", Role.MANAGER);
		assertTrue(repository.getAllByUser(user).isEmpty());
	}
	
	@Test
	public void testGetStatsByBuildingId_givenValidId_shouldReturnStatistics() {
		BuildingStats actual = repository.getStatsByBuildingId(1);
		BuildingStats expected = new BuildingStats(4, 2, 100);
		
		assertEquals(expected.getApartmentCount(), actual.getApartmentCount());
		assertEquals(expected.getResidentCount(), actual.getResidentCount());
		assertTrue(Math.abs(expected.getBalance() - actual.getBalance()) == 0);
	}
	
	
	@Test(expected=NoSuchElementException.class)
	public void testGetStatsByBuildingId_givenInvalidId_shouldThrowException() {
		repository.getStatsByBuildingId(69);		
	}
	
	@Test
	public void testGetBy_givenValidId_shouldReturnBuilding() {
		User user = new User("luis", Role.MANAGER);
		Building expected = new Building(3, "Mira Flores IV", user);
		Building actual = repository.getBy(3);
		assertEquals(expected, actual);
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getManager(), actual.getManager());
	}
	
	@Test(expected=NoSuchElementException.class)
	public void testGetBy_givenInvalidId_shouldThrowException() {
		repository.getBy(69);		
	}
}
