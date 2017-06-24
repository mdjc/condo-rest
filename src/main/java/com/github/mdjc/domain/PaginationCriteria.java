package com.github.mdjc.domain;

public class PaginationCriteria {
	public static  enum SortingOrder {ASC, DESC};
	private static final int DEFAULT_LIMIT = 2_000;
	
	private final int offset;
	private final int limit;
	private final SortingOrder sortingOrder;
	
	public PaginationCriteria(int offset, int limit, SortingOrder sortingOrder) {
		this.offset = offset;
		this.limit = limit == 0? DEFAULT_LIMIT : limit;
		this.sortingOrder = sortingOrder;
	}

	public int getOffset() {
		return offset;
	}

	public int getLimit() {
		return limit;
	}

	public SortingOrder getSortingOrder() {
		return sortingOrder;
	}
}
