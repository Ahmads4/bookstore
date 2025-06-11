package com.example.demo.bookstore.service;

import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.bookstore.model.Book;
import com.example.demo.bookstore.model.ReviewData;
import com.example.demo.bookstore.model.ReviewedBook;
import com.example.demo.bookstore.model.User;
import com.example.demo.bookstore.repository.BookStoreRepository;
import com.example.demo.bookstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookStoreServiceTest {

    @Mock
    private BookStoreRepository bookStoreRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookStoreService bookStoreService;

    private UUID bookId;
    private UUID userId;
    private Book testBook;
    private User testUser;

    @BeforeEach
    void setUp() {
        bookId = UUID.randomUUID();
        userId = UUID.randomUUID();

        testBook = new Book();
        testBook.setId(bookId);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setPrice(29.99);
        testBook.setReviewData(new ArrayList<>());

        testUser = new User();
        testUser.setId(userId);
        testUser.setReviewedBooks(new ArrayList<>());
    }

    @Test
    void getBooks_ShouldReturnAllBooks() {
        // Given
        List<Book> expectedBooks = Arrays.asList(testBook, new Book());
        when(bookStoreRepository.findAll()).thenReturn(expectedBooks);

        // When
        List<Book> actualBooks = bookStoreService.getBooks();

        // Then
        assertEquals(expectedBooks, actualBooks);
        verify(bookStoreRepository).findAll();
    }

    @Test
    void getBookByAuthor_ShouldReturnBooksByAuthor() {
        // Given
        String author = "Test Author";
        List<Book> expectedBooks = Arrays.asList(testBook);
        when(bookStoreRepository.findBookByAuthor(author)).thenReturn(expectedBooks);

        // When
        List<Book> actualBooks = bookStoreService.getBookByAuthor(author);

        // Then
        assertEquals(expectedBooks, actualBooks);
        verify(bookStoreRepository).findBookByAuthor(author);
    }

    @Test
    void deleteBook_WhenBookExists_ShouldDeleteBook() {
        // Given
        when(bookStoreRepository.existsById(bookId)).thenReturn(true);

        // When
        bookStoreService.deleteBook(bookId);

        // Then
        verify(bookStoreRepository).existsById(bookId);
        verify(bookStoreRepository).deleteById(bookId);
    }

    @Test
    void deleteBook_WhenBookDoesNotExist_ShouldThrowException() {
        // Given
        when(bookStoreRepository.existsById(bookId)).thenReturn(false);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> bookStoreService.deleteBook(bookId));

        assertEquals("Book with id " + bookId + " does not exist", exception.getMessage());
        verify(bookStoreRepository).existsById(bookId);
        verify(bookStoreRepository, never()).deleteById(bookId);
    }

    @Test
    void updateBookPrice_WhenValidPrice_ShouldUpdatePrice() {
        // Given
        Double newPrice = 39.99;
        when(bookStoreRepository.findById(bookId)).thenReturn(Optional.of(testBook));

        // When
        bookStoreService.updateBookPrice(bookId, newPrice);

        // Then
        assertEquals(newPrice, testBook.getPrice());
        verify(bookStoreRepository).findById(bookId);
    }

    @Test
    void updateBookPrice_WhenBookNotFound_ShouldThrowException() {
        // Given
        Double newPrice = 39.99;
        when(bookStoreRepository.findById(bookId)).thenReturn(Optional.empty());

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> bookStoreService.updateBookPrice(bookId, newPrice));

        assertEquals("Book with id " + bookId + " does not exist", exception.getMessage());
    }

    @Test
    void updateBookPrice_WhenPriceIsNull_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookStoreService.updateBookPrice(bookId, null));

        assertEquals("Price must be greater than 0", exception.getMessage());
    }

    @Test
    void updateBookPrice_WhenPriceIsZero_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookStoreService.updateBookPrice(bookId, 0.0));

        assertEquals("Price must be greater than 0", exception.getMessage());
    }

    @Test
    void updateBookPrice_WhenPriceIsNegative_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookStoreService.updateBookPrice(bookId, -10.0));

        assertEquals("Price must be greater than 0", exception.getMessage());
    }

    @Test
    void addBook_WhenBookDoesNotExist_ShouldAddBook() {
        // Given
        when(bookStoreRepository.doesBookExistByTitle(testBook.getTitle())).thenReturn(false);

        // When
        bookStoreService.addBook(testBook);

        // Then
        verify(bookStoreRepository).doesBookExistByTitle(testBook.getTitle());
        verify(bookStoreRepository).save(testBook);
    }

    @Test
    void addBook_WhenBookAlreadyExists_ShouldThrowException() {
        // Given
        when(bookStoreRepository.doesBookExistByTitle(testBook.getTitle())).thenReturn(true);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> bookStoreService.addBook(testBook));

        assertEquals("Book with title " + testBook.getTitle() + " already exists", exception.getMessage());
        verify(bookStoreRepository).doesBookExistByTitle(testBook.getTitle());
        verify(bookStoreRepository, never()).save(testBook);
    }

    @Test
    void rateBook_NewReview_ShouldAddReviewAndUpdateRating() {
        // Given
        double rating = 4.5;
        String comment = "Great book!";
        testBook.setAverageRating(null);
        testBook.setRatingCount(null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(bookStoreRepository.findById(bookId)).thenReturn(Optional.of(testBook));

        // When
        bookStoreService.rateBook(bookId, userId, rating, comment);

        // Then
        assertEquals(4.5, testBook.getAverageRating());
        assertEquals(1, testBook.getRatingCount());
        assertEquals(1, testBook.getReviewData().size());

        ReviewData reviewData = testBook.getReviewData().get(0);
        assertEquals(userId, reviewData.getUserId());
        assertEquals(rating, reviewData.getRating());
        assertEquals(comment, reviewData.getAdditionalComments());

        assertEquals(1, testUser.getReviewedBooks().size());
        ReviewedBook userReview = testUser.getReviewedBooks().get(0);
        assertEquals(bookId, userReview.getBookId());
        assertEquals(rating, userReview.getRating());
        assertEquals(comment, userReview.getAdditionalComments());
    }

    @Test
    void rateBook_ExistingReview_ShouldUpdateReviewAndRating() {
        // Given
        double oldRating = 3.0;
        double newRating = 4.5;
        String newComment = "Updated comment";

        testBook.setAverageRating(3.0);
        testBook.setRatingCount(1);

        ReviewData existingReview = new ReviewData();
        existingReview.setUserId(userId);
        existingReview.setRating(oldRating);
        existingReview.setAdditionalComments("Old comment");
        testBook.getReviewData().add(existingReview);

        ReviewedBook existingUserReview = new ReviewedBook(bookId, oldRating, "Old comment");
        testUser.getReviewedBooks().add(existingUserReview);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(bookStoreRepository.findById(bookId)).thenReturn(Optional.of(testBook));

        // When
        bookStoreService.rateBook(bookId, userId, newRating, newComment);

        // Then
        assertEquals(4.5, testBook.getAverageRating());
        assertEquals(1, testBook.getRatingCount());

        assertEquals(newRating, existingReview.getRating());
        assertEquals(newComment, existingReview.getAdditionalComments());

        assertEquals(newRating, existingUserReview.getRating());
    }

    @Test
    void rateBook_WithMultipleRatings_ShouldCalculateCorrectAverage() {
        // Given
        double rating1 = 4.0;
        double rating2 = 3.0;
        UUID userId2 = UUID.randomUUID();

        // First rating
        testBook.setAverageRating(null);
        testBook.setRatingCount(null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(bookStoreRepository.findById(bookId)).thenReturn(Optional.of(testBook));

        bookStoreService.rateBook(bookId, userId, rating1, "First review");

        // Second rating
        User testUser2 = new User();
        testUser2.setId(userId2);
        testUser2.setReviewedBooks(new ArrayList<>());

        when(userRepository.findById(userId2)).thenReturn(Optional.of(testUser2));

        // When
        bookStoreService.rateBook(bookId, userId2, rating2, "Second review");

        // Then
        assertEquals(3.5, testBook.getAverageRating()); // (4.0 + 3.0) / 2 = 3.5
        assertEquals(2, testBook.getRatingCount());
        assertEquals(2, testBook.getReviewData().size());
    }

    @Test
    void rateBook_WhenUserNotFound_ShouldThrowException() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> bookStoreService.rateBook(bookId, userId, 4.5, "comment"));

        assertEquals("User with id " + userId + " does not exist", exception.getMessage());
    }

    @Test
    void rateBook_WhenBookNotFound_ShouldThrowException() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(bookStoreRepository.findById(bookId)).thenReturn(Optional.empty());

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> bookStoreService.rateBook(bookId, userId, 4.5, "comment"));

        assertEquals("Book with id " + bookId + " does not exist", exception.getMessage());
    }

    @Test
    void rateBook_WhenRatingBelowZero_ShouldThrowException() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(bookStoreRepository.findById(bookId)).thenReturn(Optional.of(testBook));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookStoreService.rateBook(bookId, userId, -1.0, "comment"));

        assertEquals("Rating must be between 0 and 5", exception.getMessage());
    }

    @Test
    void rateBook_WhenRatingAboveFive_ShouldThrowException() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(bookStoreRepository.findById(bookId)).thenReturn(Optional.of(testBook));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookStoreService.rateBook(bookId, userId, 6.0, "comment"));

        assertEquals("Rating must be between 0 and 5", exception.getMessage());
    }

    @Test
    void rateBook_WithEmptyComment_ShouldNotAddToUserReviewedBooks() {
        // Given
        double rating = 4.5;
        String emptyComment = "";
        testBook.setAverageRating(null);
        testBook.setRatingCount(null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(bookStoreRepository.findById(bookId)).thenReturn(Optional.of(testBook));

        // When
        bookStoreService.rateBook(bookId, userId, rating, emptyComment);

        // Then
        assertEquals(4.5, testBook.getAverageRating());
        assertEquals(1, testBook.getRatingCount());
        assertEquals(1, testBook.getReviewData().size());
        assertEquals(0, testUser.getReviewedBooks().size()); // Should be empty due to empty comment
    }

    @Test
    void rateBook_WithNullComment_ShouldNotAddToUserReviewedBooks() {
        // Given
        double rating = 4.5;
        testBook.setAverageRating(null);
        testBook.setRatingCount(null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(bookStoreRepository.findById(bookId)).thenReturn(Optional.of(testBook));

        // When
        bookStoreService.rateBook(bookId, userId, rating, null);

        // Then
        assertEquals(4.5, testBook.getAverageRating());
        assertEquals(1, testBook.getRatingCount());
        assertEquals(1, testBook.getReviewData().size());
        assertEquals(0, testUser.getReviewedBooks().size()); // Should be empty due to null comment
    }

    @Test
    void getUsers_ShouldReturnAllUsers() {
        // Given
        List<User> expectedUsers = Arrays.asList(testUser, new User());
        when(userRepository.findAllUsers()).thenReturn(expectedUsers);

        // When
        List<User> actualUsers = bookStoreService.getUsers();

        // Then
        assertEquals(expectedUsers, actualUsers);
        verify(userRepository).findAllUsers();
    }

    @Test
    void rateBook_WithBoundaryRatingValues_ShouldAcceptValidRatings() {
        // Given
        testBook.setAverageRating(null);
        testBook.setRatingCount(null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(bookStoreRepository.findById(bookId)).thenReturn(Optional.of(testBook));

        // Test rating = 0
        assertDoesNotThrow(() -> bookStoreService.rateBook(bookId, userId, 0.0, "Zero rating"));

        // Reset for next test
        testBook.getReviewData().clear();
        testBook.setAverageRating(null);
        testBook.setRatingCount(null);

        // Test rating = 5
        assertDoesNotThrow(() -> bookStoreService.rateBook(bookId, userId, 5.0, "Max rating"));
    }
}