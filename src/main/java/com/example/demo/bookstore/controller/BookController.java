package com.example.demo.bookstore.controller;

import com.example.demo.bookstore.model.Book;
import com.example.demo.bookstore.model.ReviewRequest;
import com.example.demo.bookstore.model.User;
import com.example.demo.bookstore.service.BookStoreService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/books")
public class BookController {

    private final BookStoreService bookStoreService;

    @Autowired
    public BookController(BookStoreService bookStoreService) {
        this.bookStoreService = bookStoreService;
    }

    @GetMapping
    public List<Book> getBooks(@RequestParam(value = "author", required = false) String author) {
        if (author != null && !author.isEmpty()) {
            return bookStoreService.getBookByAuthor(author);
        }
        return bookStoreService.getBooks();
    }

    @DeleteMapping(path = "{id}")
    public void deleteBook(@PathVariable(value = "id") UUID id) {
        bookStoreService.deleteBook(id);
    }

    @PutMapping(path = "{id}")
    public void updateBookPrice(@PathVariable(value = "id") UUID id,
                                @RequestBody Double price) {
        bookStoreService.updateBookPrice(id, price);
    }

    @PostMapping()
    public void addBook(@RequestBody Book book) {
        bookStoreService.addBook(book);
    }

    @PostMapping(path = "/{id}/review")
    public void rateBook(@PathVariable("id") UUID bookId, @RequestBody ReviewRequest reviewRequest) {
        bookStoreService.rateBook(bookId, reviewRequest.getUserId(), reviewRequest.getRating(), reviewRequest.getComment());
    }


}
