package com.example.demo.bookstore;

import com.example.demo.bookstore.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookStoreRepository extends JpaRepository<Book, Integer> {

    @Query("SELECT b FROM Book b WHERE LOWER(b.author) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<Book> findBookByAuthor(String author);

    @Query("SELECT COUNT(b) > 0 FROM Book b WHERE LOWER(b.title) = LOWER(?1)")
    boolean doesBookExistByTitle(String title);

}
