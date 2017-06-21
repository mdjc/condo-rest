package com.github.mdjc.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.github.mdjc.domain.Building;
import com.github.mdjc.domain.BuildingRepository;
import com.github.mdjc.domain.BuildingStats;
import com.github.mdjc.domain.Role;
import com.github.mdjc.domain.User;

public class JdbcBuildingRepository implements BuildingRepository {
	private final JdbcTemplate template;

	public JdbcBuildingRepository(JdbcTemplate template) {
		this.template = template;
	}

	@Override
	public List<Building> getAllByUser(User user) {
		String select = "select b.id as id, b.name as name, u.username as manager from buildings b"
				+ " join users u on u.id = b.manager ";
		String orderby = " order by b.name asc";

		if (user.getRole() == Role.MANAGER) {
			return template.query(select + " where manager = (select id from users where username=?)" + orderby,
					this::mapper, user.getUsername());
		}

		return template.query(
				select + " join apartments a on b.id = a.building"
						+ " where a.resident = (select id from users where username=?)" + orderby,
				this::mapper, user.getUsername());
	}

	private Building mapper(ResultSet rs, int rowNum) throws SQLException {
		return new Building(rs.getLong("id"), rs.getString("name"), new User(rs.getString("manager"), Role.MANAGER));
	}

	private BuildingStats statisticsMapper(ResultSet rs, int rowNum) throws SQLException {
		return new BuildingStats(mapper(rs, rowNum), rs.getInt("apartmentCount"), rs.getInt("residentCount"),
				rs.getDouble("balance"));

	}

	@Override
	public BuildingStats getStatsById(long id) {
		try {
			return template.queryForObject("select b.id as id, b.name as name, b.balance, u.username as manager, "
					+ " (select count(*) from apartments where building = b.id) as apartmentCount, "
					+ " (select count(*) from apartments where building = b.id and resident is null) as residentCount"
					+ " from buildings b join users u on u.id = b.manager " 
					+ " where b.id = ?", this::statisticsMapper, id);
		} catch (EmptyResultDataAccessException e) {
			throw new NoSuchElementException();
		}
	}

}
