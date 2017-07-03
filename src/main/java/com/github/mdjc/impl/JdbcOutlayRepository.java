package com.github.mdjc.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.github.mdjc.domain.Outlay;
import com.github.mdjc.domain.OutlayCategory;
import com.github.mdjc.domain.OutlayRepository;
import com.github.mdjc.domain.OutlayStats;
import com.github.mdjc.domain.PaginationCriteria;

public class JdbcOutlayRepository implements OutlayRepository {
	private JdbcTemplate template;

	public JdbcOutlayRepository(JdbcTemplate template) {
		this.template = template;
	}

	@Override
	public List<Outlay> findBy(long condoId, LocalDate from, LocalDate to, PaginationCriteria options) {
		if (to == null) {
			to = LocalDate.now();
		}
		
		return template.query("select * from outlays"
				+ " where condo = ? and created_on >= ? and created_on <= ? "
				+ " order by created_on " + options.getSortingOrder() + " limit " + options.getOffset() + ","
				+ options.getLimit(), this::mapper, condoId, from, to);
	}

	@Override
	public OutlayStats getStatsBy(long condoId, LocalDate from, LocalDate to) {
		try {
			return template.queryForObject("select b.id, sum(amount) as sum_amount from condos b"
					+ " left join outlays o on o.condo = b.id and o.created_on >= ? and o.created_on <= ?"
					+ " where b.id = ?"
					+ " group by b.id", this::statsMapper, from, to, condoId);
		} catch(EmptyResultDataAccessException e) {
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
