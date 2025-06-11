package com.example.demo.bookstore.controller;

import com.example.demo.bookstore.model.User;
import com.example.demo.bookstore.service.BookStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/users")
public class UserController {

    private final BookStoreService bookStoreService;

    @Autowired
    public UserController(BookStoreService bookStoreService) {
        this.bookStoreService = bookStoreService;
    }

    @GetMapping()
    public List<User> getUsers() {
       return bookStoreService.getUsers();
    }

}
