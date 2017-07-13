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
	public Condo getBy(long id) {
		try {
			return template.queryForObject("select c.id as id, c.name as name, u.username as manager from condos c"
					+ " join users u on u.id = c.manager where c.id=? ", this::mapper, id);

		} catch (EmptyResultDataAccessException e) {
			throw new NoSuchElementException("Unexistent Condo");
		}
	}

	@Override
	public CondoStats getStatsByCondoId(long condoId) {
		try {
			return template.queryForObject(
					"select c.id as id, c.name as name, c.balance, u.username as manager, "
							+ " (select count(*) from apartments where condo = c.id) as apartmentCount, "
							+ " (select count(*) from apartments where condo = c.id and resident is null) as residentCount"
							+ " from condos c join users u on u.id = c.manager " + " where c.id = ?",
					this::statsMapper, condoId);
		} catch (EmptyResultDataAccessException e) {
			throw new NoSuchElementException("Unexistent Condo");
		}
	}

	@Override
	public List<Condo> getAllByUser(User user) {
		String select = "select c.id as id, c.name as name, u.username as manager from condos c"
				+ " join users u on u.id = c.manager ";
		String orderby = " order by c.name asc";

		if (user.getRole() == Role.MANAGER) {
			return template.query(select + " where manager = (select id from users where username=?)" + orderby,
					this::mapper, user.getUsername());
		}

		return template.query(
				select + " join apartments a on c.id = a.condo"
						+ " where a.resident = (select id from users where username=?)" + orderby,
				this::mapper, user.getUsername());
	}

	@Override
	public void refreshBalanceWithBill(long billId, int sign) {
		template.update(
				"update condos set balance = balance + (? * (select b.due_amount from bills b where b.id = ?))"
						+ " where id = (select a.condo from bills b join apartments a on a.id = b.apartment where b.id = ?)",
				sign, billId, billId);
	}

	private Condo mapper(ResultSet rs, int rowNum) throws SQLException {
		return new Condo(rs.getLong("id"), rs.getString("name"), new User(rs.getString("manager"), Role.MANAGER));
	}

	private CondoStats statsMapper(ResultSet rs, int rowNum) throws SQLException {
		return new CondoStats(rs.getInt("apartmentCount"), rs.getInt("residentCount"), rs.getDouble("balance"));
	}
}
