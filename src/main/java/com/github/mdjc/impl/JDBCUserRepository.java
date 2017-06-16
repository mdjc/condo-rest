package com.github.mdjc.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.github.mdjc.domain.Role;
import com.github.mdjc.domain.User;
import com.github.mdjc.domain.UserRepository;

public class JDBCUserRepository implements UserRepository {
	private final JdbcTemplate template;

	public JDBCUserRepository(JdbcTemplate template) {
		this.template = template;
	}

	public User getByUsername(String username) {
		RowMapper<User> mapper = (rs, rownum) -> {
			return new User(rs.getString("username"), 
					rs.getString("password"), 
					Role.valueOf(rs.getString("role")),
					rs.getString("apartment"));
		};

		try {
			return template.queryForObject("select * from users where username = ?", mapper, username);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
}
