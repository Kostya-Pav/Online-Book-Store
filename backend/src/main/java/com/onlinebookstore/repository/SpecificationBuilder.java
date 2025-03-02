package com.onlinebookstore.repository;

import com.onlinebookstore.model.SearchParameters;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(SearchParameters bookSearchParameters);
}
