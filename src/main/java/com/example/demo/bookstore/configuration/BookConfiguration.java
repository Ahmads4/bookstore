package com.example.demo.bookstore.configuration;

import com.example.demo.bookstore.model.Book;
import com.example.demo.bookstore.model.DataGenerator;
import com.example.demo.bookstore.model.User;
import com.example.demo.bookstore.repository.BookStoreRepository;
import com.example.demo.bookstore.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BookConfiguration {

    @Bean
    CommandLineRunner commandLineRunner(BookStoreRepository bookStoreRepository) {
        return args -> {
            List<Book> books = DataGenerator.generateBookData(args);
            bookStoreRepository.saveAll(books);
        };

    }

    @Bean
    CommandLineRunner userDataLoader(UserRepository userRepository) {
        return args -> {
            List<User> users = DataGenerator.generateUserData();
            userRepository.saveAll(users);
        };
    }
}
