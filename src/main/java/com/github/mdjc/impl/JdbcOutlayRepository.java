package com.github.mdjc.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.github.mdjc.commons.db.DBUtils;
import com.github.mdjc.domain.Outlay;
import com.github.mdjc.domain.OutlayCategory;
import com.github.mdjc.domain.OutlayRepository;
import com.github.mdjc.domain.OutlayStats;
import com.github.mdjc.domain.PaginationCriteria;

public class JdbcOutlayRepository implements OutlayRepository {
	private NamedParameterJdbcTemplate template;

	public JdbcOutlayRepository(NamedParameterJdbcTemplate template) {
		this.template = template;
	}

	@Override
	public List<Outlay> findBy(long condoId, LocalDate from, LocalDate to, PaginationCriteria options) {
		if (to == null) {
			to = LocalDate.now();
		}

		SqlParameterSource parameters = DBUtils.parametersMap("condo_id", condoId, "from", from, "to", to, "offset",
				options.getOffset(), "limit", options.getLimit());

		return template.query(
				"select * from outlays  where condo = :condo_id and created_on >= :from and created_on <= :to"
						+ String.format(" order by created_on %s", options.getSortingOrder()) + " limit :offset,:limit",
				parameters, this::mapper);
	}

	@Override
	public int countFindBy(long condoId, LocalDate from, LocalDate to) {
		if (to == null) {
			to = LocalDate.now();
		}

		SqlParameterSource parameters = DBUtils.parametersMap("condo_id", condoId, "from", from, "to", to);

		return template.queryForObject(
				"select count(*) from outlays where condo = :condo_id and created_on >= :from and created_on <= :to ",
				parameters, Integer.class);
	}

	@Override
	public OutlayStats getStatsBy(long condoId, LocalDate from, LocalDate to) {
		try {
			SqlParameterSource parameters = DBUtils.parametersMap("condo_id", condoId, "from", from, "to", to);

			return template.queryForObject("select c.id, sum(amount) as sum_amount from condos c"
					+ " left join outlays o on o.condo = c.id and o.created_on >= :from and o.created_on <= :to"
					+ " where c.id = :condo_id  group by c.id", parameters, this::statsMapper);
		} catch (EmptyResultDataAccessException e) {
			throw new NoSuchElementException("Unexistent Condo");
		}
	}

	private Outlay mapper(ResultSet rs, int rownum) throws SQLException {
		return new Outlay(rs.getLong("id"), OutlayCategory.valueOf(rs.getString("category")), rs.getDouble("amount"),
				rs.getDate("created_on").toLocalDate(), rs.getString("supplier"), rs.getString("comment"));
	}

	private OutlayStats statsMapper(ResultSet rs, int rownum) throws SQLException {
		return new OutlayStats(rs.getDouble("sum_amount"));
	}
}
