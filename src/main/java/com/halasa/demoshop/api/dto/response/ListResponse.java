package com.halasa.demoshop.api.dto.response;

import java.util.List;
import java.util.Optional;

public class ListResponse<T> {

    private List<T> results;

    private Optional<Long> totalCount;
    private Optional<Integer> limit;
    private Optional<Integer> offset;

    public ListResponse() {
    }

    public ListResponse(List<T> results, Optional<Long> totalCount, Optional<Integer> limit, Optional<Integer> offset) {
        this.results = results;
        this.totalCount = totalCount;
        this.limit = limit;
        this.offset = offset;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public Optional<Long> getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Optional<Long> totalCount) {
        this.totalCount = totalCount;
    }

    public Optional<Integer> getLimit() {
        return limit;
    }

    public void setLimit(Optional<Integer> limit) {
        this.limit = limit;
    }

    public Optional<Integer> getOffset() {
        return offset;
    }

    public void setOffset(Optional<Integer> offset) {
        this.offset = offset;
    }
}
