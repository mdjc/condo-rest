package com.github.mdjc.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.github.mdjc.commons.db.DBUtils;
import com.github.mdjc.domain.Apartment;
import com.github.mdjc.domain.ApartmentRepository;

public class JdbcApartmentRepository implements ApartmentRepository {
	private final NamedParameterJdbcTemplate template;

	public JdbcApartmentRepository(NamedParameterJdbcTemplate template) {
		this.template = template;
	}

	@Override
	public Apartment getBy(String username) {
		try {
		return template.queryForObject(
				"select * from apartments where resident = (select id from users where username = :username)",
				DBUtils.parametersMap("username", username), this::mapper);
		} catch(EmptyResultDataAccessException e) {
			throw new NoSuchElementException("Unexistent Apartment");
		}
	}

	Apartment mapper(ResultSet rs, int row) throws SQLException {
		return new Apartment(rs.getString("name"));
	}

}
