package com.github.mdjc.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.github.mdjc.commons.db.DBUtils;
import com.github.mdjc.domain.Apartment;
import com.github.mdjc.domain.Condo;
import com.github.mdjc.domain.CondoRepository;
import com.github.mdjc.domain.CondoStats;
import com.github.mdjc.domain.ImageExtension;
import com.github.mdjc.domain.Role;
import com.github.mdjc.domain.User;

public class JdbcCondoRepository implements CondoRepository {
	private static final String SELECT_CONDO_AND_MANAGER = "select c.*, u.username as manager_name from condos c"
			+ " join users u on u.id = c.manager";
	private final NamedParameterJdbcTemplate template;

	public JdbcCondoRepository(NamedParameterJdbcTemplate template) {
		this.template = template;
	}

	@Override
	public Condo getBy(long id) {
		try {
			return template.queryForObject(SELECT_CONDO_AND_MANAGER + " where c.id= :id ",
					DBUtils.parametersMap("id", id), this::mapper);

		} catch (EmptyResultDataAccessException e) {
			throw new NoSuchElementException("Unexistent Condo");
		}
	}

	@Override
	public Condo getBy(long id, User user) {
		try {
			if (user.isManager()) {
				return template.queryForObject(
						SELECT_CONDO_AND_MANAGER
								+ " where c.id = :condoId and manager = (select id from users where username = :username)",
						DBUtils.parametersMap("condoId", id, "username", user.getUsername()), this::mapper);
			}

			return template.queryForObject(
					SELECT_CONDO_AND_MANAGER + " join apartments a on c.id = a.condo"
							+ " where c.id = :condoId and a.resident = (select id from users where username = :username)",
					DBUtils.parametersMap("condoId", id, "username", user.getUsername()), this::mapper);
		} catch (EmptyResultDataAccessException e) {
			throw new NoSuchElementException("Unexistent Condo for User");
		}
	}

	@Override
	public CondoStats getStatsByCondoId(long condoId) {
		try {
			return template.queryForObject(
					"select c.id as id, c.name as name, c.balance, u.username as manager, "
							+ " (select count(*) from apartments where condo = c.id) as apartmentCount, "
							+ " (select count(*) from apartments where condo = c.id and resident is not null) as residentCount"
							+ " from condos c join users u on u.id = c.manager " + " where c.id = :condo_id",
					DBUtils.parametersMap("condo_id", condoId), this::statsMapper);
		} catch (EmptyResultDataAccessException e) {
			throw new NoSuchElementException("Unexistent Condo");
		}
	}

	@Override
	public List<Condo> getAllByUser(User user) {
		String orderby = " order by c.name asc";

		if (user.isManager()) {
			return template.query(SELECT_CONDO_AND_MANAGER
					+ " where manager = (select id from users where username = :username)" + orderby,
					DBUtils.parametersMap("username", user.getUsername()), this::mapper);
		}

		return template.query(
				SELECT_CONDO_AND_MANAGER + " join apartments a on c.id = a.condo"
						+ " where a.resident = (select id from users where username = :username)" + orderby,
				DBUtils.parametersMap("username", user.getUsername()), this::mapper);
	}

	@Override
	public List<Apartment> getCondoApartments(long condoId) {
		return template.query(
				"select a.*, u.username as resident_name from apartments a"
						+ " left join users u on u.id = a.resident where condo = :condo_id ",
				DBUtils.parametersMap("condo_id", condoId), this::apartmentMapper);
	}

	@Override
	public void refreshBalanceWithBill(long billId, int sign) {
		template.update(
				"update condos set balance = balance + (:sign * (select b.due_amount from bills b where b.id = :bill_id))"
						+ " where id = (select a.condo from bills b join apartments a on a.id = b.apartment where b.id = :bill_id)",
				DBUtils.parametersMap("bill_id", billId, "sign", sign));
	}

	@Override
	public void refreshBalanceWithOutlay(long outlayId, int sign) {
		template.update(
				"update condos set balance = balance + (:sign * (select o.amount from outlays o where o.id = :outlay_id))"
						+ " where id = (select condo from outlays where id = :outlay_id)",
				DBUtils.parametersMap("outlay_id", outlayId, "sign", sign));

	}

	private Condo mapper(ResultSet rs, int rowNum) throws SQLException {
		ImageExtension imgExtension = rs.getString("img_extension") != null
				? ImageExtension.valueOf(rs.getString("img_extension")) : null;
		return new Condo(rs.getLong("id"), rs.getString("name"), new User(rs.getString("manager_name"), Role.MANAGER),
				rs.getString("address"), rs.getString("contact_name"), rs.getString("contact_phone"),
				rs.getInt("billing_day_of_month"), imgExtension);
	}

	private CondoStats statsMapper(ResultSet rs, int rowNum) throws SQLException {
		return new CondoStats(rs.getInt("apartmentCount"), rs.getInt("residentCount"), rs.getDouble("balance"));
	}

	private Apartment apartmentMapper(ResultSet rs, int rowNum) throws SQLException {
		User resident = rs.getString("resident_name") == null ? null
				: new User(rs.getString("resident_name"), Role.RESIDENT);
		return new Apartment(rs.getString("name"), resident);
	}

}
