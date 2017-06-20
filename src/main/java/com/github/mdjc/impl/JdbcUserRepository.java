package com.github.mdjc.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.github.mdjc.domain.Role;
import com.github.mdjc.domain.User;
import com.github.mdjc.domain.UserRepository;

public class JdbcUserRepository implements UserRepository {
	private final JdbcTemplate template;

	public JdbcUserRepository(JdbcTemplate template) {
		this.template = template;
	}

	public User getByUsername(String username) {
		try {
			return template.queryForObject("select * from users where username = ?", this::mapper, username);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private User mapper(ResultSet rs, int rowNum) throws SQLException {
		return new User(rs.getString("username"), rs.getString("password"), Role.valueOf(rs.getString("role")));
	}
}
