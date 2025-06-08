package com.example.demo.bookstore;

import com.example.demo.bookstore.model.Book;
import com.example.demo.bookstore.model.DataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
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
}
