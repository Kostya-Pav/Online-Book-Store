package com.onlinebookstore.repository.book;

import com.onlinebookstore.model.Book;
import com.onlinebookstore.repository.SpecificationProvider;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class IsbnSpecificationProvider implements SpecificationProvider<Book> {
    @Override
    public String getKey() {
        return "isbn";
    }

    public Specification<Book> getSpecification(List<String> params) {
        System.out.println("Filtering by ISBN: " + params);
        return (root, query, criteriaBuilder) -> root.get("isbn")
                .in(params);
    }
}
