package com.github.mdjc.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.github.mdjc.domain.Apartment;
import com.github.mdjc.domain.Payment;
import com.github.mdjc.domain.PaymentMethod;
import com.github.mdjc.domain.PaymentRepository;
import com.github.mdjc.domain.PaymentStatus;
import com.github.mdjc.domain.Role;
import com.github.mdjc.domain.PaginationCriteria;
import com.github.mdjc.domain.User;

public class JdbcPaymentRepository implements PaymentRepository {
	JdbcTemplate template;

	public JdbcPaymentRepository(JdbcTemplate template) {
		this.template = template;
	}

	@Override
	public List<Payment> findby(long buildingId, LocalDate from, LocalDate to, PaginationCriteria options) {
		if (to == null) {
			to = LocalDate.now();
		}
		
		return template.query(
				"select p.id, p.amount, p.payment_method, p.created_on, p.status, u.username as created_by_name,"
				+ " u.role as created_by_role, a.name as apartment, r.username as resident "
						+ " from payments p" 
						+ " join users u on u.id = p.created_by"
						+ " join apartments a on a.id = p.apartment" 
						+ " join users r on r.id = a.resident"
						+ " where a.building = ? and p.created_on >= ? and p.created_on <= ? "
						+ " order by p.created_on " + options.getSortingOrder()
						+ " limit " +  options.getOffset() + "," + options.getLimit(),
				this::mapper, buildingId, from, to);
	}

	private Payment mapper(ResultSet rs, int rownum) throws SQLException {
		User resident = new User(rs.getString("resident"), Role.RESIDENT);
		Apartment apartment = new Apartment(rs.getString("apartment"), resident);
		User createdBy = new User(rs.getString("created_by_name"), Role.valueOf(rs.getString("created_by_role")));

		return new Payment(rs.getLong("id"), apartment, rs.getDouble("amount"),
				PaymentMethod.valueOf(rs.getString("payment_method")), rs.getDate("created_on").toLocalDate(),
				createdBy, PaymentStatus.valueOf(rs.getString("status")));
	}

}
