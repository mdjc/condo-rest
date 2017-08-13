package com.github.mdjc.impl;

import static com.github.mdjc.commons.db.DBUtils.parametersMap;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.github.mdjc.domain.Apartment;
import com.github.mdjc.domain.Bill;
import com.github.mdjc.domain.BillRepository;
import com.github.mdjc.domain.BilltStats;
import com.github.mdjc.domain.CondoBill;
import com.github.mdjc.domain.PaginationCriteria;
import com.github.mdjc.domain.PaymentMethod;
import com.github.mdjc.domain.PaymentStatus;
import com.github.mdjc.domain.ImageExtension;
import com.github.mdjc.domain.Role;
import com.github.mdjc.domain.User;

public class JdbcBillRepository implements BillRepository {
	private final NamedParameterJdbcTemplate template;

	public JdbcBillRepository(NamedParameterJdbcTemplate template) {
		this.template = template;
	}

	@Override
	public Bill getBy(long billId) {
		try {
			return template.queryForObject("select * from bills where id = :bill_id", parametersMap("bill_id", billId),
					this::mapper);
		} catch (EmptyResultDataAccessException e) {
			throw new NoSuchElementException("Unexistent Bill");
		}
	}

	@Override
	public Bill getBy(long billId, User user) {
		try {
			if (user.isManager()) {
				return template.queryForObject(
						"select b.* from bills b join apartments a on a.id = b.apartment join condos c on c.id = a.condo"
								+ " where b.id = :bill_id and c.manager = (select id from users where username = :username)",
						parametersMap("bill_id", billId, "username", user.getUsername()), this::mapper);
			}
	
			return template.queryForObject(
					"select b.* from bills b join apartments a on a.id = b.apartment"
							+ " where b.id = :bill_id and a.condo = "
							+ " (select c.id from condos c join apartments a on a.condo = c.id where a.resident = (select id from users where username = :username))",
					parametersMap("bill_id", billId, "username", user.getUsername()), this::mapper);
		} catch(EmptyResultDataAccessException e) {
			throw new NoSuchElementException("Unexistent Bill for User");
		}
	}

	@Override
	public CondoBill getCondoBilldBy(long billId) {
		try {
			return template.queryForObject("select b.*, a.name as apartment_name, u.username from apartments a"
					+ " join bills b on b.apartment = a.id" + " join users u on u.id = a.resident"
					+ " where b.id = :bill_id ", parametersMap("bill_id", billId), this::condoBillMapper);
		} catch (EmptyResultDataAccessException e) {
			throw new NoSuchElementException("Unexistent Bill");
		}
	}

	@Override
	public BilltStats getStatsBy(long condoId, LocalDate from, LocalDate to) {
		MapSqlParameterSource parameters = parametersMap("pending_status", PaymentStatus.PENDING.toString(),
				"paid_confirmed_status", PaymentStatus.PAID_CONFIRMED.toString(), "paid_awaiting_confirmation_status",
				PaymentStatus.PAID_AWAITING_CONFIRMATION.toString(), "rejected_status",
				PaymentStatus.REJECTED.toString(), "from", from, "to", to, "condo_id", condoId);

		return template
				.queryForObject("select count(case when payment_status = :pending_status then 1 end) AS pending_count,"
						+ " count(case when payment_status = :paid_confirmed_status then 1 end) AS paid_confirmed_count,"
						+ " count(case when payment_status = :paid_awaiting_confirmation_status then 1  end) AS paid_awaiting_confirmation_count,"
						+ " count(case when payment_status = :rejected_status then 1  end) AS rejected_count,"
						+ " sum(CASE WHEN payment_status = :paid_confirmed_status then due_amount else 0 end) as paid_confirmed_total,"
						+ " count(distinct b.id) as condo_found" + " from condos b"
						+ " left join apartments a on a.condo = b.id"
						+ " left join bills bi on bi.apartment = a.id and bi.last_update_on >= :from and bi.last_update_on <= :to"
						+ " where b.id = :condo_id", parameters, this::statsMapper);
	}

	public List<CondoBill> findBy(long condoId, List<PaymentStatus> paymentStatusList, LocalDate from, LocalDate to,
			PaginationCriteria pagCriteria) {
		MapSqlParameterSource parameters = parametersMap("condo_id", condoId, "offset", pagCriteria.getOffset(),
				"limit", pagCriteria.getLimit());
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("select b.*, a.name as apartment_name, u.username from apartments a");
		sqlBuilder.append(" join bills b on b.apartment = a.id");
		addPaymentListFilter(paymentStatusList, parameters, sqlBuilder);
		addFromFilter(from, parameters, sqlBuilder);
		addToFilter(to, parameters, sqlBuilder);
		sqlBuilder.append(" join users u on u.id = a.resident");
		sqlBuilder.append(" where a.condo = :condo_id ");
		sqlBuilder.append(
				String.format(" order by b.last_update_on %s, payment_status asc", pagCriteria.getSortingOrder()));
		sqlBuilder.append(" limit :offset,:limit");
		return template.query(sqlBuilder.toString(), parameters, this::condoBillMapper);
	}

	public int countFindBy(long condoId, List<PaymentStatus> paymentStatusList, LocalDate from, LocalDate to) {
		MapSqlParameterSource parameters = parametersMap("condo_id", condoId);
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("select count(*) from apartments a");
		sqlBuilder.append(" join bills b on b.apartment = a.id");
		addPaymentListFilter(paymentStatusList, parameters, sqlBuilder);
		addFromFilter(from, parameters, sqlBuilder);
		addToFilter(to, parameters, sqlBuilder);
		sqlBuilder.append(" join users u on u.id = a.resident");
		sqlBuilder.append(" where a.condo = :condo_id ");
		return template.queryForObject(sqlBuilder.toString(), parameters, Integer.class);
	}

	@Override
	public List<Bill> findBy(long condoId, String username, List<PaymentStatus> paymentStatusList) {
		MapSqlParameterSource parameters = parametersMap("username", username, "condo_id", condoId,
				"payment_status_list", statusStrList(paymentStatusList));
		return template.query("select b.* from apartments a"
				+ " join bills b on b.apartment = a.id and b.payment_status in (:payment_status_list)"
				+ " where a.condo = :condo_id " + " and resident = (select id from users where username=:username)"
				+ " order by b.last_update_on, payment_status asc", parameters, this::mapper);
	}

	@Override
	public void add(long condoId, CondoBill bill) {
		SqlParameterSource parameters = parametersMap("condo_id", condoId, "apartment_name",
				bill.getApartment().getName(), "due_date", Date.valueOf(bill.getDueDate()), "due_amount",
				bill.getDueAmount(), "description", bill.getDescription(), "last_update_on",
				Date.valueOf(LocalDate.now()), "payment_status", PaymentStatus.PENDING.toString());

		try {
			template.update(
					"insert into bills (apartment, due_date, due_amount, description, payment_status, last_update_on)"
							+ " values((select id from apartments where name = :apartment_name and condo = :condo_id),"
							+ " :due_date, :due_amount, :description, :payment_status, :last_update_on)",
					parameters);
		} catch (DataIntegrityViolationException e) {
			throw new IllegalArgumentException("invalid bill", e);
		}
	}

	@Override
	public void delete(long billId) {
		int affectedRows = template.update("delete from bills where id = :bill_id", parametersMap("bill_id", billId));

		if (affectedRows == 0) {
			throw new NoSuchElementException("Unexistent Bill");
		}
	}

	@Override
	public void updatePaymentInfo(long billId, PaymentStatus paymentStatus, PaymentMethod paymentMethod,
			ImageExtension prooOfPaymentExt) {
		MapSqlParameterSource parameters = parametersMap("bill_id", billId, "payment_status", paymentStatus.toString(),
				"payment_method", paymentMethod.toString(), "last_update_on", Date.valueOf(LocalDate.now()),
				"proof_of_payment_extension", prooOfPaymentExt.toString());

		template.update("update bills set payment_status = :payment_status, payment_method = :payment_method, "
				+ " last_update_on = :last_update_on, proof_of_payment_extension = :proof_of_payment_extension"
				+ " where id = :bill_id  ", parameters);
	}

	@Override
	public void updatePaymentInfo(long billId, PaymentStatus paymentStatus, PaymentMethod paymentMethod) {
		MapSqlParameterSource parameters = parametersMap("bill_id", billId, "payment_status", paymentStatus.toString(),
				"payment_method", paymentMethod.toString(), "last_update_on", Date.valueOf(LocalDate.now()));

		template.update("update bills set payment_status = :payment_status, payment_method = :payment_method, "
				+ " last_update_on = :last_update_on where id = :bill_id  ", parameters);
	}

	@Override
	public void updatePaymentInfo(long billId, PaymentStatus paymentStatus) {
		MapSqlParameterSource parameters = parametersMap("bill_id", billId, "payment_status", paymentStatus.toString(),
				"last_update_on", Date.valueOf(LocalDate.now()));

		template.update("update bills set payment_status = :payment_status, "
				+ " last_update_on = :last_update_on where id = :bill_id  ", parameters);
	}

	private Bill mapper(ResultSet rs, int rownum) throws SQLException {
		PaymentMethod paymentMethod = paymentMethodOrNull(rs);
		PaymentStatus paymentStatus = paymentStatusOrNull(rs);
		ImageExtension proofOfPaymentExt = proofOfPaymentExtensionOrNull(rs);

		return new Bill(rs.getLong("id"), rs.getString("description"), rs.getDate("due_date").toLocalDate(),
				rs.getDouble("due_amount"), paymentStatus, rs.getDate("last_update_on").toLocalDate(), paymentMethod,
				proofOfPaymentExt);
	}

	private BilltStats statsMapper(ResultSet rs, int rownum) throws SQLException {
		if (rs.getInt("condo_found") == 0) {
			throw new NoSuchElementException("Unexistent Condo");
		}

		return new BilltStats(rs.getInt("pending_count"), rs.getInt("paid_awaiting_confirmation_count"),
				rs.getInt("paid_confirmed_count"), rs.getInt("rejected_count"), rs.getDouble("paid_confirmed_total"));
	}

	private CondoBill condoBillMapper(ResultSet rs, int rownume) throws SQLException {
		PaymentMethod paymentMethod = paymentMethodOrNull(rs);
		PaymentStatus paymentStatus = paymentStatusOrNull(rs);
		ImageExtension proofOfPaymentExt = proofOfPaymentExtensionOrNull(rs);
		Apartment apartment = new Apartment(rs.getString("apartment_name"),
				new User(rs.getString("username"), Role.RESIDENT));

		return new CondoBill(rs.getLong("id"), rs.getString("description"), rs.getDate("due_date").toLocalDate(),
				rs.getDouble("due_amount"), paymentStatus, rs.getDate("last_update_on").toLocalDate(), paymentMethod,
				proofOfPaymentExt, apartment);
	}

	private void addPaymentListFilter(List<PaymentStatus> paymentStatusList, MapSqlParameterSource parameters,
			StringBuilder sqlBuilder) {
		if (!paymentStatusList.isEmpty()) {
			sqlBuilder.append(" and b.payment_status in (:payment_status_list)");
			parameters.addValue("payment_status_list", statusStrList(paymentStatusList));
		}
	}

	private void addFromFilter(LocalDate from, MapSqlParameterSource parameters, StringBuilder sqlBuilder) {
		if (from != null) {
			sqlBuilder.append(" and b.last_update_on >= :from");
			parameters.addValue("from", from);
		}
	}

	private void addToFilter(LocalDate to, MapSqlParameterSource parameters, StringBuilder sqlBuilder) {
		if (to != null) {
			sqlBuilder.append(" and b.last_update_on <= :to");
			parameters.addValue("to", to);
		}
	}

	private List<String> statusStrList(List<PaymentStatus> paymentStatusList) {
		return paymentStatusList.stream().map(e -> e.toString()).collect(Collectors.toList());
	}

	private ImageExtension proofOfPaymentExtensionOrNull(ResultSet rs) throws SQLException {
		return rs.getString("proof_of_payment_extension") != null
				? ImageExtension.valueOf(rs.getString("proof_of_payment_extension")) : null;
	}

	private PaymentStatus paymentStatusOrNull(ResultSet rs) throws SQLException {
		return rs.getString("payment_status") != null ? PaymentStatus.valueOf(rs.getString("payment_status")) : null;
	}

	private PaymentMethod paymentMethodOrNull(ResultSet rs) throws SQLException {
		return rs.getString("payment_method") != null ? PaymentMethod.valueOf(rs.getString("payment_method")) : null;
	}
}
