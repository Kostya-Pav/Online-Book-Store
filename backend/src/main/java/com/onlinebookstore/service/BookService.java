package com.onlinebookstore.service;

import com.onlinebookstore.model.Book;
import com.onlinebookstore.model.SearchParameters;
import java.util.List;

public interface BookService {
    Book save(Book book);

    List<Book> findAll();

    Book getBookById(Long id);

    void deleteById(Long id);

    Book updateBook(Long id, Book updatedBook);

    List<Book> searchBooks(SearchParameters searchParameters);
}
