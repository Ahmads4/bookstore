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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        Book book = bookStoreRepository.findById(bookId)
                .orElseThrow(() -> new IllegalStateException("Book with id " + bookId + " does not exist"));

        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }

        double currentAvg = book.getAverageRating() != null ? book.getAverageRating() : 0.0;
        int currentCount = book.getRatingCount() != null ? book.getRatingCount() : 0;

        ReviewData existingReview = book.getReviewData().stream()
                .filter(rd -> rd.getUserId().equals(userId))
                .findFirst()
                .orElse(null);

        if (existingReview != null) {
            double newAvg = updateAverageOnEdit(currentAvg, currentCount, existingReview.getRating(), rating);
            book.setAverageRating((newAvg));
            existingReview.setRating(rating);
            existingReview.setAdditionalComments(comment);
        } else {
            double newAvg = ((currentAvg * currentCount) + rating) / (currentCount + 1);
            book.setAverageRating((newAvg));
            book.setRatingCount(currentCount + 1);

            ReviewData review = new ReviewData();
            review.setUserId(userId);
            review.setRating(rating);
            if (comment != null && !comment.isEmpty()) {
                review.setAdditionalComments(comment);
            }

            book.getReviewData().add(review);
        }


        ReviewedBook userReview = getReviewedBook(user, bookId);
        if (userReview != null) {
            userReview.setRating(rating);
        } else {
            if(comment != null && !comment.isEmpty() ) {
                user.getReviewedBooks().add(new ReviewedBook(bookId, rating, comment));
            }
        }
    }

    private double updateAverageOnEdit(double currentAvg, int count, double oldRating, double newRating) {
        double total = currentAvg * count;
        total = total - oldRating + newRating;
        return total / count;
    }


    private boolean hasUserReviewedBook(User user, UUID bookId) {
        ReviewedBook reviewedBook = getReviewedBook(user, bookId);
        return reviewedBook != null;
    }

    private static ReviewedBook getReviewedBook(User user, UUID bookId) {
        return Optional.ofNullable(user.getReviewedBooks())
                .stream()
                .flatMap(List::stream)
                .filter(book -> book.getBookId().equals(bookId))
                .findFirst()
                .orElse(null);

    }

    public List<User> getUsers() {
        return userRepository.findAllUsers();
    }
}
