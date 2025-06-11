package com.example.demo.bookstore.service;

import com.example.demo.bookstore.model.ReviewData;
import com.example.demo.bookstore.model.ReviewedBook;
import com.example.demo.bookstore.model.User;
import com.example.demo.bookstore.repository.BookStoreRepository;
import com.example.demo.bookstore.model.Book;
import com.example.demo.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class BookStoreService {

    private final BookStoreRepository bookStoreRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookStoreService(BookStoreRepository bookStoreRepository, UserRepository userRepository) {
        this.bookStoreRepository = bookStoreRepository;
        this.userRepository = userRepository;
    }

    public List<Book> getBooks() {
        return bookStoreRepository.findAll();
    }


    public List<Book> getBookByAuthor(String author) {
        return bookStoreRepository.findBookByAuthor(author);
    }

    public void deleteBook(UUID id) {
        var doesBookExist = bookStoreRepository.existsById(id);
        if (!doesBookExist) {
            throw new IllegalStateException("Book with id " + id + " does not exist");
        }
        bookStoreRepository.deleteById(id);

    }

    @Transactional
    public void updateBookPrice(UUID id, Double price) {
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

    @Transactional
    public void rateBook(UUID bookId, UUID userId, double rating, String comment) {
        var user = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("User with id " + userId + " does not exist"));

        Book book = bookStoreRepository.findById(bookId).orElseThrow(() -> new IllegalStateException("Book with id " + bookId + " does not exist"));

        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }

        double currentAvg = Objects.requireNonNullElse(book.getAverageRating(), 0.0);
        int currentCount = Objects.requireNonNullElse(book.getRatingCount(), 0);

        ReviewData existingReview = book.getReviewData().stream()
                .filter(reviewData -> reviewData.getUserId().equals(userId))
                .findFirst()
                .orElse(null);

        if (existingReview != null) {
            handleExistingReview(user, rating, comment, currentAvg, currentCount, existingReview, book);
        } else {
            handleNewReview(user, rating, comment, currentAvg, currentCount, book);
        }

    }

    @Transactional
    private static void handleNewReview(User user, double rating, String comment, double currentAvg, int currentCount, Book book) {
        double newAvg = ((currentAvg * currentCount) + rating) / (currentCount + 1);
        book.setAverageRating((newAvg));
        book.setRatingCount(currentCount + 1);

        ReviewData review = new ReviewData();
        review.setUserId(user.getId());
        review.setRating(rating);
        review.setAdditionalComments(comment);
        user.getReviewedBooks().add(new ReviewedBook(book.getId(), rating, comment));

        book.getReviewData().add(review);

    }

    @Transactional
    private void handleExistingReview(User user, double rating, String comment, double currentAvg, int currentCount, ReviewData existingReview, Book book) {
        double newAvg = updateAverageOnEdit(currentAvg, currentCount, existingReview.getRating(), rating);
        book.setAverageRating((newAvg));
        existingReview.setAdditionalComments(comment);
        existingReview.setRating(rating);
        user.getReviewedBooks().set(0, new ReviewedBook(book.getId(), rating, comment));

    }

    private double updateAverageOnEdit(double currentAvg, int count, double oldRating, double newRating) {
        double total = currentAvg * count;
        total = total - oldRating + newRating;
        return total / count;
    }

    public List<User> getUsers() {
        return userRepository.findAllUsers();
    }
}
