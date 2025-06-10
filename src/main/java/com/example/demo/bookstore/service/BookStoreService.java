package com.example.demo.bookstore.service;

import com.example.demo.bookstore.model.Review;
import com.example.demo.bookstore.repository.BookStoreRepository;
import com.example.demo.bookstore.model.Book;
import com.example.demo.bookstore.repository.ReviewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.antlr.v4.runtime.misc.Utils.count;

@Service
public class BookStoreService {

    private final BookStoreRepository bookStoreRepository;

    private final ReviewsRepository reviewsRepository;

    @Autowired
    public BookStoreService(BookStoreRepository bookStoreRepository, ReviewsRepository reviewsRepository) {
        this.bookStoreRepository = bookStoreRepository;
        this.reviewsRepository = reviewsRepository;
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

    public void rateBook(int bookId, double rating, String comment) {
        Book book = bookStoreRepository.findById(bookId).orElseThrow(() -> new IllegalStateException("Book with id " + bookId + " does not exist"));

        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }

        double currentAverage = book.getRating() != null ? book.getRating() : 0.0;
        int currentCount = book.getRatingCount() != null ? book.getRatingCount() : 0;

        double newAverage = ((currentAverage * currentCount) + rating) / (currentCount + 1);

        book.setRating(newAverage);
        book.setRatingCount(currentCount + 1);
        ArrayList<String> comments = book.getAdditionalComments() != null ? book.getAdditionalComments() : new ArrayList<>();
        if (!comment.isEmpty()) {
            comments.add(comment);
        }
        book.setAdditionalComments(comments);
        bookStoreRepository.save(book);
    }
}
