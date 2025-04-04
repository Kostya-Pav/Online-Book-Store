package com.onlinebookstore.service;

import com.onlinebookstore.model.Book;
import com.onlinebookstore.model.SearchParameters;
import com.onlinebookstore.repository.book.BookRepository;
import com.onlinebookstore.repository.book.BookSpecificationBuilder;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public Book getById(Long id) {
        return bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id" + id));
    }

    @Override
    public void deleteById(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
        }
        throw new EntityNotFoundException("Book not found with id: " + id);
    }

    @Override
    public Book update(Long id, Consumer<Book> modifier) {
        Book bookToUpdate = getById(id);
        modifier.accept(bookToUpdate);
        return bookRepository.save(bookToUpdate);
    }

    @Override
    public List<Book> search(SearchParameters parameters) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(parameters);
        return bookRepository.findAll(bookSpecification);
    }
}
