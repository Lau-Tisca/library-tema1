package com.practica.library.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practica.library.model.Book;
import com.practica.library.model.BookDTO;
import com.practica.library.repository.BookRepositoryJPA;
import jakarta.persistence.EntityNotFoundException; // Good for specific exceptions
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import this

import java.util.List;

@Service
public class BookServiceJPA {

    private final BookRepositoryJPA bookRepositoryJPA;
    private final ObjectMapper mapper;

    public BookServiceJPA(BookRepositoryJPA bookRepositoryJPA, ObjectMapper mapper) {
        this.bookRepositoryJPA = bookRepositoryJPA;
        this.mapper = mapper;
    }

    public List<BookDTO> getBooks() {
        return bookRepositoryJPA.findAll()
                .stream()
                .map(book -> mapper.convertValue(book, BookDTO.class))
                .toList();
    }

    public BookDTO saveBook(Book book) {
        return mapper.convertValue(bookRepositoryJPA.save(book), BookDTO.class);
    }

    @Transactional
    public BookDTO patchBookNameJPA(Long bookId, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("New title cannot be null or empty.");
        }

        Book existingBook = bookRepositoryJPA.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));
        existingBook.setName(newName);

        Book updatedBook = bookRepositoryJPA.save(existingBook);

        return mapper.convertValue(updatedBook, BookDTO.class);
    }
}