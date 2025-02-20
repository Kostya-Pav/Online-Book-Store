package com.onlinebookstore.service;

import com.onlinebookstore.dto.CreateBookRequest;
import com.onlinebookstore.model.Book;
import java.util.List;
import org.springframework.web.bind.annotation.RequestBody;

public interface BookService {
    Book save(@RequestBody CreateBookRequest requestDto);

    List<Book> findAll();

    Book getBookById(Long id);
}
