package com.github.mdjc.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.github.mdjc.commons.db.DBUtils;
import com.github.mdjc.domain.Role;
import com.github.mdjc.domain.User;
import com.github.mdjc.domain.UserRepository;

public class JdbcUserRepository implements UserRepository {
	private final NamedParameterJdbcTemplate template;

	public JdbcUserRepository(NamedParameterJdbcTemplate template) {
		this.template = template;
	}

	public User getByUsername(String username) {
		try {
			return template.queryForObject("select * from users where username = :username",
					DBUtils.parametersMap("username", username), this::mapper);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private User mapper(ResultSet rs, int rowNum) throws SQLException {
		return new User(rs.getString("username"), rs.getString("password"), Role.valueOf(rs.getString("role")));
	}
}
