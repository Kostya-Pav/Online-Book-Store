package com.onlinebookstore.service;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.onlinebookstore.model.Book;
import com.onlinebookstore.model.SearchParameters;
import com.onlinebookstore.repository.book.BookRepository;
import com.onlinebookstore.repository.book.BookSpecificationBuilder;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id" + id));
    }

    @Override
    public void deleteById(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
        }
        throw new ResponseStatusException(NOT_FOUND, "Book not found with id: " + id);
    }

    @Override
    public Book update(Book book) {
        Book bookToUpdate = getBookById(book.getId());
        bookToUpdate.setTitle(book.getTitle());
        bookToUpdate.setAuthor(book.getAuthor());
        bookToUpdate.setIsbn(book.getIsbn());
        bookToUpdate.setPrice(book.getPrice());
        bookToUpdate.setDescription(book.getDescription());
        bookToUpdate.setCoverImage(book.getCoverImage());
        return bookRepository.save(bookToUpdate);
    }

    @Override
    public List<Book> search(SearchParameters parameters) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(parameters);
        return bookRepository.findAll(bookSpecification).stream().toList();
    }
}
