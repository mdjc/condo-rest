package com.github.mdjc.impl;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.github.mdjc.domain.Apartment;
import com.github.mdjc.domain.Bill;
import com.github.mdjc.domain.BillRepository;
import com.github.mdjc.domain.BilltStats;
import com.github.mdjc.domain.CondoBill;
import com.github.mdjc.domain.PaymentMethod;
import com.github.mdjc.domain.PaymentStatus;
import com.github.mdjc.domain.ProofOfPaymentExtension;
import com.github.mdjc.domain.Role;
import com.github.mdjc.domain.User;

public class JdbcBillRepository implements BillRepository {
	private final NamedParameterJdbcTemplate template;
	
	public JdbcBillRepository(NamedParameterJdbcTemplate template) {
		this.template = template;
	}

	@Override
	public Bill getBy(long billId) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("bill_id", billId);

		try {
			return template.queryForObject("select * from bills where id = :bill_id", parameters, this::mapper);
		} catch (EmptyResultDataAccessException e) {
			throw new NoSuchElementException("Unexistent Bill");
		}
	}
	
	@Override
	public CondoBill getCondoBilldBy(long billId) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("billId", billId);

		try {
			return template.queryForObject("select b.*, a.name as apartment_name, u.username from apartments a"
				+ " join bills b on b.apartment = a.id"
				+ " join users u on u.id = a.resident"
				+ " where b.id = :billId ", parameters, this::condoBillMapper);
		} catch (EmptyResultDataAccessException e) {
			throw new NoSuchElementException("Unexistent Bill");
		}
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
	
	public List<CondoBill> findBy(long condoId, List<PaymentStatus> paymentStatusList) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("condo_id", condoId);
		parameters.addValue("payment_status_list", statusStrList(paymentStatusList));

		return template.query("select b.*, a.name as apartment_name, u.username from apartments a"
				+ " join bills b on b.apartment = a.id and b.payment_status in (:payment_status_list)"
				+ " join users u on u.id = a.resident"
				+ " where a.condo = :condo_id "
				+ " order by b.last_update_on, payment_status asc", parameters, this::condoBillMapper);
	}
	
	@Override
	public List<Bill> findBy(long condoId, String username, List<PaymentStatus> paymentStatusList) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("username", username);
		parameters.addValue("condo_id", condoId);
		parameters.addValue("payment_status_list", statusStrList(paymentStatusList));

		return template.query("select b.* from apartments a"
				+ " join bills b on b.apartment = a.id and b.payment_status in (:payment_status_list)"
				+ " where a.condo = :condo_id "
				+ " and resident = (select id from users where username=:username)"
				+ " order by b.last_update_on, payment_status asc", parameters, this::mapper);
	}

	@Override
	public void updatePaymentInfo(long billId, PaymentStatus paymentStatus, PaymentMethod paymentMethod,
			ProofOfPaymentExtension prooOfPaymentExt) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("bill_id", billId);
		parameters.addValue("payment_status", paymentStatus.toString());
		parameters.addValue("payment_method", paymentMethod.toString());
		parameters.addValue("last_update_on", Date.valueOf(LocalDate.now()));
		parameters.addValue("proof_of_payment_extension", prooOfPaymentExt.toString());

		template.update(
				"update bills set payment_status = :payment_status, payment_method = :payment_method, "
						+ " last_update_on = :last_update_on, proof_of_payment_extension = :proof_of_payment_extension where id = :bill_id  ",
				parameters);
	}
	
	@Override
	public void updatePaymentInfo(long billId, PaymentStatus paymentStatus, PaymentMethod paymentMethod) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("bill_id", billId);
		parameters.addValue("payment_status", paymentStatus.toString());
		parameters.addValue("payment_method", paymentMethod.toString());
		parameters.addValue("last_update_on", Date.valueOf(LocalDate.now()));

		template.update(
				"update bills set payment_status = :payment_status, payment_method = :payment_method, "
						+ " last_update_on = :last_update_on where id = :bill_id  ",
				parameters);
	}

	private Bill mapper(ResultSet rs, int rownum) throws SQLException {
		PaymentMethod paymentMethod = rs.getString("payment_method") != null
				? PaymentMethod.valueOf(rs.getString("payment_method")) : null;

		PaymentStatus paymentStatus = rs.getString("payment_status") != null
				? PaymentStatus.valueOf(rs.getString("payment_status")) : null;

		ProofOfPaymentExtension proofOfPaymentExt = rs.getString("proof_of_payment_extension") != null
				? ProofOfPaymentExtension.valueOf(rs.getString("proof_of_payment_extension")) : null;

		return new Bill(rs.getLong("id"), rs.getString("description"), rs.getDate("due_date").toLocalDate(),
				rs.getDouble("due_amount"), paymentStatus,  rs.getDate("last_update_on").toLocalDate(), paymentMethod,
				proofOfPaymentExt);
	}
	
	private CondoBill condoBillMapper(ResultSet rs, int rownume) throws SQLException {
		PaymentMethod paymentMethod = rs.getString("payment_method") != null
				? PaymentMethod.valueOf(rs.getString("payment_method")) : null;

		PaymentStatus paymentStatus = rs.getString("payment_status") != null
				? PaymentStatus.valueOf(rs.getString("payment_status")) : null;

		ProofOfPaymentExtension proofOfPaymentExt = rs.getString("proof_of_payment_extension") != null
				? ProofOfPaymentExtension.valueOf(rs.getString("proof_of_payment_extension")) : null;

		Apartment apartment = new Apartment(rs.getString("apartment_name"), new User(rs.getString("username"), Role.RESIDENT));
		return new CondoBill(rs.getLong("id"), rs.getString("description"), rs.getDate("due_date").toLocalDate(),
				rs.getDouble("due_amount"), paymentStatus,  rs.getDate("last_update_on").toLocalDate(), paymentMethod,
				proofOfPaymentExt, apartment );
	}
	
	private BilltStats statsMapper(ResultSet rs, int rownum) throws SQLException {
		if (rs.getInt("condo_found") == 0) {
			throw new NoSuchElementException("Unexistent Condo");
		}

		return new BilltStats(rs.getInt("pending_count"), rs.getInt("paid_awaiting_confirmation_count"),
				rs.getInt("paid_confirmed_count"), rs.getInt("rejected_count"), rs.getDouble("paid_confirmed_total"));
	}
	
	private List<String> statusStrList(List<PaymentStatus> paymentStatusList) {
		return paymentStatusList.stream().map(e -> e.toString()).collect(Collectors.toList());
	}
}
