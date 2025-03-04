package com.onlinebookstore.repository.book;

import com.onlinebookstore.model.Book;
import com.onlinebookstore.model.SearchParameters;
import com.onlinebookstore.repository.SpecificationBuilder;
import com.onlinebookstore.repository.SpecificationProviderManager;
import java.lang.reflect.Field;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@Component
@RequiredArgsConstructor
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(SearchParameters searchParameters) {
        Specification<Book> specification = Specification.where(null);

        Field[] fields = SearchParameters.class.getDeclaredFields();

        for (Field field : fields) {
            ReflectionUtils.makeAccessible(field);
            Object fieldValue = ReflectionUtils.getField(field, searchParameters);
            if (fieldValue != null && !fieldValue.toString().isEmpty()) {
                specification = specification.and(bookSpecificationProviderManager
                        .getSpecificationProvider(field.getName())
                        .getSpecification((List<String>) fieldValue)
                );
            }
        }
        return specification;
    }
}
