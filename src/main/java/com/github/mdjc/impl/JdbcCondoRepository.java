package com.github.mdjc.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.github.mdjc.domain.Condo;
import com.github.mdjc.domain.CondoRepository;
import com.github.mdjc.domain.CondoStats;
import com.github.mdjc.domain.Role;
import com.github.mdjc.domain.User;

public class JdbcCondoRepository implements CondoRepository {
	private final JdbcTemplate template;

	public JdbcCondoRepository(JdbcTemplate template) {
		this.template = template;
	}

	@Override
	public List<Condo> getAllByUser(User user) {
		String select = "select b.id as id, b.name as name, u.username as manager from condos b"
				+ " join users u on u.id = b.manager ";
		String orderby = " order by b.name asc";

		if (user.getRole() == Role.MANAGER) {
			return template.query(select + " where manager = (select id from users where username=?)" + orderby,
					this::mapper, user.getUsername());
		}

		return template.query(
				select + " join apartments a on b.id = a.condo"
						+ " where a.resident = (select id from users where username=?)" + orderby,
				this::mapper, user.getUsername());
	}

	@Override
	public CondoStats getStatsByCondoId(long condoId) {
		try {
			return template.queryForObject(
					"select b.id as id, b.name as name, b.balance, u.username as manager, "
							+ " (select count(*) from apartments where condo = b.id) as apartmentCount, "
							+ " (select count(*) from apartments where condo = b.id and resident is null) as residentCount"
							+ " from condos b join users u on u.id = b.manager " + " where b.id = ?",
					this::statsMapper, condoId);
		} catch (EmptyResultDataAccessException e) {
			throw new NoSuchElementException("Unexistent Condo");
		}
	}

	private Condo mapper(ResultSet rs, int rowNum) throws SQLException {
		return new Condo(rs.getLong("id"), rs.getString("name"), new User(rs.getString("manager"), Role.MANAGER));
	}

	private CondoStats statsMapper(ResultSet rs, int rowNum) throws SQLException {
		return new CondoStats(rs.getInt("apartmentCount"), rs.getInt("residentCount"), rs.getDouble("balance"));

	}

	@Override
	public Condo getBy(long id) {
		try {
			return template.queryForObject( 
				"select b.id as id, b.name as name, u.username as manager from condos b"
				+ " join users u on u.id = b.manager where b.id=? ", this::mapper, id);

		} catch(EmptyResultDataAccessException e) {
			throw new NoSuchElementException("Unexistent Condo");
		}
	}
}
