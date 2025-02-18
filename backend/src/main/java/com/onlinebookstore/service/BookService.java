package com.onlinebookstore.service;

import com.onlinebookstore.dto.BookDto;
import com.onlinebookstore.dto.CreateBookRequestDto;
import java.util.List;
import org.springframework.web.bind.annotation.RequestBody;

public interface BookService {
    BookDto save(@RequestBody CreateBookRequestDto requestDto);

    List<BookDto> findAll();

    BookDto getBookById(Long id);
}
