package com.example.demo.bookstore.service;

import com.example.demo.bookstore.BookStoreRepository;
import com.example.demo.bookstore.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookStoreService {

    private final BookStoreRepository bookStoreRepository;

    @Autowired
    public BookStoreService(BookStoreRepository bookStoreRepository) {
        this.bookStoreRepository = bookStoreRepository;
    }

    public List<Book> getBooks() {
        return bookStoreRepository.findAll();
    }


    public List<Book> getBookByAuthor(String author) {
        return bookStoreRepository.findBookByAuthor(author);
    }

    public void deleteBook(int id) {
        var doesBookExist = bookStoreRepository.existsById(id);
        if (!doesBookExist) {
            throw new IllegalStateException("Book with id " + id + " does not exist");
        }
        bookStoreRepository.deleteById(id);

    }

    @Transactional
    public void updateBookPrice(int id, Double price) {
        if (price == null || price <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        Book book = bookStoreRepository.findById(id).orElseThrow(() -> new IllegalStateException("Book with id " + id + " does not exist"));
        book.setPrice(price);

    }

    public void addBook(Book book) {
        var doesBookExist = bookStoreRepository.doesBookExistByTitle(book.getTitle());
        if (doesBookExist) {
            throw new IllegalStateException("Book with title " + book.getTitle() + " already exists");
        }
        bookStoreRepository.save(book);
    }

    public void rateBook(int id, double rating) {
        Book book = bookStoreRepository.findById(id).orElseThrow(() -> new IllegalStateException("Book with id " + id + " does not exist"));
        if(rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 & 5");
        }
        var bookRating = book.getRating();

    }
}
