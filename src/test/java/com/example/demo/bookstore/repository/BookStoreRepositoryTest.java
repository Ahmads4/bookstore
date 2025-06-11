package com.example.demo.bookstore.repository;

import com.example.demo.BookStoreApplication;
import com.example.demo.bookstore.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@ContextConfiguration(classes = BookStoreApplication.class)
@ComponentScan(basePackages = "com.example.demo.repository", includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {BookStoreRepository.class}))
class BookStoreRepositoryTest {

    @Autowired
    private BookStoreRepository bookStoreRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager.clear();
        bookStoreRepository.deleteAll();
    }

    @Test
    void findBookByAuthor_shouldReturnBooksByGivenAuthor() {
        Book book1 = Book.builder()
                .title("The Lord of the Rings")
                .author("J.R.R. Tolkien")
                .price(15.99)
                .publishDate(new Date())
                .publisher("Publisher A")
                .description("Fantasy novel")
                .averageRating(4.5)
                .ratingCount(100)
                .build();
        Book book2 = Book.builder()
                .title("The Hobbit")
                .author("J.R.R. Tolkien")
                .price(12.50)
                .publishDate(new Date())
                .publisher("Publisher A")
                .description("Fantasy prequel")
                .averageRating(4.2)
                .ratingCount(80)
                .build();
        Book book3 = Book.builder()
                .title("Pride and Prejudice")
                .author("Jane Austen")
                .price(9.99)
                .publishDate(new Date())
                .publisher("Publisher B")
                .description("Classic novel")
                .averageRating(4.8)
                .ratingCount(120)
                .build();

        entityManager.persist(book1);
        entityManager.persist(book2);
        entityManager.persist(book3);
        entityManager.flush();

        List<Book> foundBooks = bookStoreRepository.findBookByAuthor("Tolkien");

        assertThat(foundBooks).hasSize(2);
        assertThat(foundBooks).contains(book1, book2);
        assertThat(foundBooks).doesNotContain(book3);
    }

    @Test
    void findBookByAuthor_shouldReturnEmptyListWhenNoBooksByAuthor() {
        Book book1 = Book.builder()
                .title("The Lord of the Rings")
                .author("J.R.R. Tolkien")
                .price(15.99)
                .publishDate(new Date())
                .publisher("Publisher A")
                .description("Fantasy novel")
                .averageRating(4.5)
                .ratingCount(100)
                .build();
        entityManager.persist(book1);
        entityManager.flush();

        List<Book> foundBooks = bookStoreRepository.findBookByAuthor("NonExistentAuthor");

        assertThat(foundBooks).isEmpty();
    }

    @Test
    void doesBookExistByTitle_shouldReturnTrueWhenBookExists() {
        Book book = Book.builder()
                .title("The Great Gatsby")
                .author("F. Scott Fitzgerald")
                .price(10.00)
                .publishDate(new Date())
                .publisher("Scribner")
                .description("A classic American novel.")
                .averageRating(4.0)
                .ratingCount(50)
                .build();
        entityManager.persist(book);
        entityManager.flush();

        boolean exists = bookStoreRepository.doesBookExistByTitle("The Great Gatsby");

        assertThat(exists).isTrue();
    }

    @Test
    void doesBookExistByTitle_shouldReturnTrueWhenBookExistsCaseInsensitive() {
        Book book = Book.builder()
                .title("The Great Gatsby")
                .author("F. Scott Fitzgerald")
                .price(10.00)
                .publishDate(new Date())
                .publisher("Scribner")
                .description("A classic American novel.")
                .averageRating(4.0)
                .ratingCount(50)
                .build();
        entityManager.persist(book);
        entityManager.flush();

        boolean exists = bookStoreRepository.doesBookExistByTitle("the great gatsby");

        assertThat(exists).isTrue();
    }

    @Test
    void doesBookExistByTitle_shouldReturnFalseWhenBookDoesNotExist() {
        boolean exists = bookStoreRepository.doesBookExistByTitle("NonExistentBook");

        assertThat(exists).isFalse();
    }
}