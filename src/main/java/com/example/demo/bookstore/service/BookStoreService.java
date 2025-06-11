package com.example.demo.bookstore.service;

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

    public void rateBook(UUID bookId, UUID userId, double rating, String comment) {
        var user = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("User not found"));

        if (hasUserReviewedBook(user, bookId)) {
            updateUserRating(user, bookId, rating);
        } else {
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
                book.setAdditionalComments(comments);
            }
            bookStoreRepository.save(book);
            List<ReviewedBook> reviewedBooks = user.getReviewedBooks();
            reviewedBooks.add(new ReviewedBook(bookId, rating));
        }
    }


    private void updateUserRating(User user, UUID bookId, double rating) {
        ReviewedBook reviewedBook = getReviewedBook(user, bookId);
        reviewedBook.setRating(rating);

    }

    private boolean hasUserReviewedBook(User user, UUID bookId) {
        ReviewedBook reviewedBook = getReviewedBook(user, bookId);
        return reviewedBook != null;
    }

    private static ReviewedBook getReviewedBook(User user, UUID bookId) {
        return Optional.ofNullable(user.getReviewedBooks()) // Wrap the potentially null list in an Optional
                .stream() // Create a stream from the Optional (either containing the list or empty)
                .flatMap(List::stream) // Flatten the list into a stream of ReviewedBook objects
                .filter(book -> book.getBookId().equals(bookId)) // Filter for the matching bookId
                .findFirst() // Get the first matching book
                .orElse(null); // Return the book if found, otherwise null

    }

}
