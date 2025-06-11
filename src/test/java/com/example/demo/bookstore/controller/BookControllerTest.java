package com.example.demo.bookstore.controller;

import com.example.demo.bookstore.model.Book;
import com.example.demo.bookstore.model.ReviewRequest;
import com.example.demo.bookstore.service.BookStoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookStoreService bookStoreService;

    @InjectMocks
    private BookController bookController;

    private Book book1;
    private Book book2;
    private List<Book> bookList;
    private UUID bookId;
    private ReviewRequest reviewRequest;

    @BeforeEach
    void setUp() {
        bookId = UUID.randomUUID();

        book1 = new Book();
        book1.setId(bookId);
        book1.setTitle("Java Programming");
        book1.setAuthor("John Author");
        book1.setPrice(29.99);

        book2 = new Book();
        book2.setId(UUID.randomUUID());
        book2.setTitle("Spring Boot Guide");
        book2.setAuthor("Jane Author");
        book2.setPrice(39.99);

        bookList = Arrays.asList(book1, book2);

        reviewRequest = new ReviewRequest();
        reviewRequest.setUserId(UUID.randomUUID());
        reviewRequest.setRating(5);
        reviewRequest.setComment("Great book!");
    }

    @Test
    void testGetBooks_WithoutAuthorFilter_ReturnsAllBooks() {
        // Given
        when(bookStoreService.getBooks()).thenReturn(bookList);

        // When
        List<Book> result = bookController.getBooks(null);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(book1, result.get(0));
        assertEquals(book2, result.get(1));
        verify(bookStoreService, times(1)).getBooks();
        verify(bookStoreService, never()).getBookByAuthor(any());
    }

    @Test
    void testGetBooks_WithEmptyAuthorFilter_ReturnsAllBooks() {
        // Given
        when(bookStoreService.getBooks()).thenReturn(bookList);

        // When
        List<Book> result = bookController.getBooks("");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(bookStoreService, times(1)).getBooks();
        verify(bookStoreService, never()).getBookByAuthor(any());
    }

    @Test
    void testGetBooks_WithAuthorFilter_ReturnsFilteredBooks() {
        // Given
        String author = "John Author";
        List<Book> filteredBooks = Arrays.asList(book1);
        when(bookStoreService.getBookByAuthor(author)).thenReturn(filteredBooks);

        // When
        List<Book> result = bookController.getBooks(author);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(book1, result.get(0));
        verify(bookStoreService, times(1)).getBookByAuthor(author);
        verify(bookStoreService, never()).getBooks();
    }

    @Test
    void testGetBooks_WithAuthorFilter_ReturnsEmptyListWhenNoMatches() {
        // Given
        String author = "Unknown Author";
        when(bookStoreService.getBookByAuthor(author)).thenReturn(Arrays.asList());

        // When
        List<Book> result = bookController.getBooks(author);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bookStoreService, times(1)).getBookByAuthor(author);
    }

    @Test
    void testDeleteBook_ValidId_CallsServiceDeleteMethod() {
        // Given
        doNothing().when(bookStoreService).deleteBook(bookId);

        // When
        bookController.deleteBook(bookId);

        // Then
        verify(bookStoreService, times(1)).deleteBook(bookId);
    }

    @Test
    void testDeleteBook_ServiceThrowsException() {
        // Given
        doThrow(new RuntimeException("Book not found")).when(bookStoreService).deleteBook(bookId);

        // When & Then
        assertThrows(RuntimeException.class, () -> bookController.deleteBook(bookId));
        verify(bookStoreService, times(1)).deleteBook(bookId);
    }

    @Test
    void testUpdateBookPrice_ValidIdAndPrice_CallsServiceUpdateMethod() {
        // Given
        Double newPrice = 49.99;
        doNothing().when(bookStoreService).updateBookPrice(bookId, newPrice);

        // When
        bookController.updateBookPrice(bookId, newPrice);

        // Then
        verify(bookStoreService, times(1)).updateBookPrice(bookId, newPrice);
    }

    @Test
    void testUpdateBookPrice_NullPrice_CallsServiceWithNullPrice() {
        // Given
        doNothing().when(bookStoreService).updateBookPrice(bookId, null);

        // When
        bookController.updateBookPrice(bookId, null);

        // Then
        verify(bookStoreService, times(1)).updateBookPrice(bookId, null);
    }

    @Test
    void testUpdateBookPrice_ServiceThrowsException() {
        // Given
        Double newPrice = 49.99;
        doThrow(new RuntimeException("Book not found")).when(bookStoreService).updateBookPrice(bookId, newPrice);

        // When & Then
        assertThrows(RuntimeException.class, () -> bookController.updateBookPrice(bookId, newPrice));
        verify(bookStoreService, times(1)).updateBookPrice(bookId, newPrice);
    }

    @Test
    void testAddBook_ValidBook_CallsServiceAddMethod() {
        // Given
        doNothing().when(bookStoreService).addBook(book1);

        // When
        bookController.addBook(book1);

        // Then
        verify(bookStoreService, times(1)).addBook(book1);
    }

    @Test
    void testAddBook_NullBook_CallsServiceWithNullBook() {
        // Given
        doNothing().when(bookStoreService).addBook(null);

        // When
        bookController.addBook(null);

        // Then
        verify(bookStoreService, times(1)).addBook(null);
    }

    @Test
    void testAddBook_ServiceThrowsException() {
        // Given
        doThrow(new RuntimeException("Book already exists")).when(bookStoreService).addBook(book1);

        // When & Then
        assertThrows(RuntimeException.class, () -> bookController.addBook(book1));
        verify(bookStoreService, times(1)).addBook(book1);
    }

    @Test
    void testRateBook_ValidRequest_CallsServiceRateMethod() {
        // Given
        doNothing().when(bookStoreService).rateBook(
                bookId,
                reviewRequest.getUserId(),
                reviewRequest.getRating(),
                reviewRequest.getComment()
        );

        // When
        bookController.rateBook(bookId, reviewRequest);

        // Then
        verify(bookStoreService, times(1)).rateBook(
                bookId,
                reviewRequest.getUserId(),
                reviewRequest.getRating(),
                reviewRequest.getComment()
        );
    }


    @Test
    void testRateBook_ServiceThrowsException() {
        // Given
        doThrow(new RuntimeException("Book not found")).when(bookStoreService).rateBook(
                bookId,
                reviewRequest.getUserId(),
                reviewRequest.getRating(),
                reviewRequest.getComment()
        );

        // When & Then
        assertThrows(RuntimeException.class, () -> bookController.rateBook(bookId, reviewRequest));
        verify(bookStoreService, times(1)).rateBook(
                bookId,
                reviewRequest.getUserId(),
                reviewRequest.getRating(),
                reviewRequest.getComment()
        );
    }

    @Test
    void testRateBook_WithMinimalReviewRequest_CallsServiceRateMethod() {
        // Given
        ReviewRequest minimalRequest = new ReviewRequest();
        minimalRequest.setUserId(UUID.randomUUID());
        minimalRequest.setRating(3);
        minimalRequest.setComment(null);

        doNothing().when(bookStoreService).rateBook(
                bookId,
                minimalRequest.getUserId(),
                minimalRequest.getRating(),
                minimalRequest.getComment()
        );

        // When
        bookController.rateBook(bookId, minimalRequest);

        // Then
        verify(bookStoreService, times(1)).rateBook(
                bookId,
                minimalRequest.getUserId(),
                minimalRequest.getRating(),
                null
        );
    }
}