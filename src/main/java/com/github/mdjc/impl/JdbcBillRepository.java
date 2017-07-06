package com.github.mdjc.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.github.mdjc.domain.Bill;
import com.github.mdjc.domain.BillRepository;
import com.github.mdjc.domain.BilltStats;
import com.github.mdjc.domain.PaymentMethod;
import com.github.mdjc.domain.PaymentStatus;

public class JdbcBillRepository implements BillRepository {
	NamedParameterJdbcTemplate template;

	public JdbcBillRepository(NamedParameterJdbcTemplate template) {
		this.template = template;
	}

	@Override
	public BilltStats getStatsBy(long condoId, LocalDate from, LocalDate to) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("pending_status", PaymentStatus.PENDING.toString());
		parameters.addValue("paid_confirmed_status", PaymentStatus.PAID_CONFIRMED.toString());
		parameters.addValue("paid_awaiting_confirmation_status", PaymentStatus.PAID_AWAITING_CONFIRMATION.toString());
		parameters.addValue("rejected_status", PaymentStatus.REJECTED.toString());
		parameters.addValue("from", from);
		parameters.addValue("to", to);
		parameters.addValue("condo_id", condoId);

		return template
				.queryForObject("Select count(case when payment_status = :pending_status then 1 end) AS pending_count,"
						+ " count(case when payment_status = :paid_confirmed_status then 1 end) AS paid_confirmed_count,"
						+ " count(case when payment_status = :paid_awaiting_confirmation_status then 1  end) AS paid_awaiting_confirmation_count,"
						+ " count(case when payment_status = :rejected_status then 1  end) AS rejected_count,"
						+ " sum(CASE WHEN payment_status = :paid_confirmed_status then due_amount else 0 end) as paid_confirmed_total,"
						+ " count(distinct b.id) as condo_found" + " from condos b"
						+ " left join apartments a on a.condo = b.id"
						+ " left join bills bi on bi.apartment = a.id and bi.last_update_on >= :from and bi.last_update_on <= :to"
						+ " where b.id = :condo_id", parameters, this::statsMapper);
	}

	@Override
	public List<Bill> getBy(long condoId, String username, List<PaymentStatus> paymentStatusList) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("username", username);
		parameters.addValue("condo_id", condoId);
		parameters.addValue("payment_status_list",
				paymentStatusList.stream().map(e -> e.toString()).collect(Collectors.toList()));

			return template.query("select a.id as apartment, b.* from apartments a"
				+ " left join bills b on b.apartment = a.id and b.payment_status in (:payment_status_list)"
				+ " where a.condo = :condo_id and resident = (select id from users where username=:username)"
				+ " order by b.last_update_on, payment_status asc", parameters, this::mapper);		
	}

	private BilltStats statsMapper(ResultSet rs, int rownum) throws SQLException {
		if (rs.getInt("condo_found") == 0) {
			throw new NoSuchElementException("Unexistent Condo");
		}

		return new BilltStats(rs.getInt("pending_count"), rs.getInt("paid_awaiting_confirmation_count"),
				rs.getInt("paid_confirmed_count"), rs.getInt("rejected_count"), rs.getDouble("paid_confirmed_total"));
	}

	private Bill mapper(ResultSet rs, int rownum) throws SQLException {
		PaymentMethod paymentMethod = rs.getString("payment_method") != null
				? PaymentMethod.valueOf(rs.getString("payment_method")) : null;

		return new Bill(rs.getLong("id"), rs.getString("description"), rs.getDate("due_date").toLocalDate(),
				rs.getDouble("due_amount"), PaymentStatus.valueOf(rs.getString("payment_status")), paymentMethod,
				rs.getDate("last_update_on").toLocalDate());
	}

}
