package com.github.mdjc.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.NoSuchElementException;

import org.springframework.jdbc.core.JdbcTemplate;

import com.github.mdjc.domain.BillRepository;
import com.github.mdjc.domain.BilltStats;
import com.github.mdjc.domain.PaymentStatus;

public class JdbcBillRepository implements BillRepository {
	JdbcTemplate template;

	public JdbcBillRepository(JdbcTemplate template) {
		this.template = template;
	}

	@Override
	public BilltStats getStatsBy(long buildingId, LocalDate from, LocalDate to) {
		return template.queryForObject(
				"Select count(case when payment_status = ? then 1 end) AS pending_count,"
						+ " count(case when payment_status = ? then 1 end) AS paid_confirmed_count,"
						+ " count(case when payment_status = ? then 1  end) AS paid_awaiting_confirmation_count,"
						+ " count(case when payment_status = ? then 1  end) AS rejected_count,"
						+ " sum(CASE WHEN payment_status = ? then due_amount else 0 end) as paid_confirmed_total,"
						+ " count(distinct b.id) as building_found" 
						+ " from buildings b"
						+ " left join apartments a on a.building = b.id"
						+ " left join bills bi on bi.apartment = a.id and bi.last_update_on >= ? and bi.last_update_on <= ?"
						+ " where b.id = ?",
				this::statsMapper, PaymentStatus.PENDING.toString(), PaymentStatus.PAID_CONFIRMED.toString(),
				PaymentStatus.PAID_AWAITING_CONFIRMATION.toString(), PaymentStatus.REJECTED.toString(),
				PaymentStatus.PAID_CONFIRMED.toString(), from, to, buildingId);

	}

	private BilltStats statsMapper(ResultSet rs, int rownum) throws SQLException {
		if (rs.getInt("building_found") == 0) {
			throw new NoSuchElementException("Unexistent Building");
		}

		return new BilltStats(rs.getInt("pending_count"), rs.getInt("paid_awaiting_confirmation_count"),
				rs.getInt("paid_confirmed_count"), rs.getInt("rejected_count"), rs.getDouble("paid_confirmed_total"));
	}
}
