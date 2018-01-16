package com.halasa.demoshop.service.repository;

import java.util.List;
import java.util.Optional;

public class ListResult<T> {

    List<T> results;

    private Optional<Long> totalCount;
    private Optional<Integer> limit;
    private Optional<Integer> offset;

    public ListResult(List<T> results) {
        this(results, Optional.empty(), Optional.empty(), Optional.empty());
    }

    public ListResult(List<T> results, Optional<Long> totalCount, Optional<Integer> limit, Optional<Integer> offset) {
        this.results = results;
        this.totalCount = totalCount;
        this.limit = limit;
        this.offset = offset;
    }

    public List<T> getResults() {
        return results;
    }

    public Optional<Long> getTotalCount() {
        return totalCount;
    }

    public Optional<Integer> getLimit() {
        return limit;
    }

    public Optional<Integer> getOffset() {
        return offset;
    }
}
