package com.github.mdjc.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.github.mdjc.domain.Building;
import com.github.mdjc.domain.BuildingRepository;
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
}
