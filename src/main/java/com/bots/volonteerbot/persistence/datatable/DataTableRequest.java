package com.bots.volonteerbot.persistence.datatable;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class DataTableRequest {

    private static final int DEFAULT_PAGE_SIZE = 5;
    private static final String DEFAULT_SORT_FIELD = "created";
    private static final Sort.Direction DEFAULT_ORDER = Sort.Direction.ASC;

    private final long allSize;
    private final int totalPageSize;
    private final int page;
    private final int pageSize;
    private final boolean showNext;
    private final boolean showPrevious;
    private final String sort;
    private final Sort.Direction order;
    private final long currentShowFromEntries;
    private final long currentShowToEntries;

    private DataTableRequest(long allSize, int page) {
        this(allSize, page, DEFAULT_PAGE_SIZE, DEFAULT_SORT_FIELD, DEFAULT_ORDER);
    }

    private DataTableRequest(long allSize, int page, int pageSize) {
        this(allSize, page, pageSize, DEFAULT_SORT_FIELD, DEFAULT_ORDER);
    }

    private DataTableRequest(long allSize, int page, int pageSize, String sort, Sort.Direction order) {
        this.allSize = allSize;
        this.page = page;
        this.pageSize = pageSize;
        this.sort = sort;
        this.order = order;
        totalPageSize = (int) (allSize % pageSize == 0 ? allSize / pageSize : allSize / pageSize + 1);
        this.currentShowFromEntries = (long) (page) * pageSize + 1;
        this.currentShowToEntries = Math.min((long) (page + 1) * pageSize, allSize);
        showPrevious = page > 0;
        showNext = page < (totalPageSize - 1);
    }

    public static DataTableRequest fromPage(long allSize, int page) {
        return new DataTableRequest(allSize, page);
    }

    public static DataTableRequest fromPageAndSize(long allSize, int page, int pageSize) {
        return new DataTableRequest(allSize, page, pageSize);
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public boolean hasNext() {
        return showNext;
    }

    public boolean hasPrevious() {
        return showPrevious;
    }

    public String getSort() {
        return sort;
    }

    public Sort.Direction getOrder() {
        return order;
    }

    public long getAllSize() {
        return allSize;
    }

    public PageRequest pageRequest() {
        return PageRequest.of(page, pageSize, Sort.by(order, sort));
    }

    public int getTotalPageSize() {
        return totalPageSize;
    }

    public long getCurrentShowFromEntries() {
        return currentShowFromEntries;
    }

    public long getCurrentShowToEntries() {
        return currentShowToEntries;
    }
}
