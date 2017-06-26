package com.github.mdjc.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.github.mdjc.domain.Outlay;
import com.github.mdjc.domain.OutlayCategory;
import com.github.mdjc.domain.OutlayRepository;
import com.github.mdjc.domain.PaginationCriteria;

public class JdbcOutlayRepository implements OutlayRepository {
	private JdbcTemplate template;

	public JdbcOutlayRepository(JdbcTemplate template) {
		this.template = template;
	}

	@Override
	public List<Outlay> findBy(long buildingId, LocalDate from, LocalDate to, PaginationCriteria options) {
		if (to == null) {
			to = LocalDate.now();
		}
		
		return template.query("select * from outlays"
				+ " where building = ? and created_on >= ? and created_on <= ? "
				+ " order by created_on " + options.getSortingOrder() + " limit " + options.getOffset() + ","
				+ options.getLimit(), this::mapper, buildingId, from, to);
	}

	private Outlay mapper(ResultSet rs, int rownum) throws SQLException {
		return new Outlay(rs.getLong("id"), OutlayCategory.valueOf(rs.getString("category")), rs.getDouble("amount"),
				rs.getDate("created_on").toLocalDate(), rs.getString("supplier"), rs.getString("comment"));
	}

}
