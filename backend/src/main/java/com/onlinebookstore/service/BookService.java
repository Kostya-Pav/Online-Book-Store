package com.onlinebookstore.service;

import com.onlinebookstore.model.Book;
import com.onlinebookstore.model.SearchParameters;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.data.domain.Pageable;

public interface BookService {
    Book save(Book book);

    List<Book> findAll(Pageable pageable);

    Book getById(Long id);

    void deleteById(Long id);

    Book update(Long id, Consumer<Book> modifier);

    List<Book> search(SearchParameters searchParameters);
}
