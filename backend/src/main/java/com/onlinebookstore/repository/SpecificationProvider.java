package com.onlinebookstore.repository;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationProvider<T> {
    String getKey();

    public Specification<T> getSpecification(List<String> params);
}
