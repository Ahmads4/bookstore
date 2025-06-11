package com.example.demo.bookstore.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "books_table")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString()
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String title;
    private String author;
    private Double price;
    private Date publishDate;
    private String publisher;
    private String description;
    private Double rating;
    private Integer ratingCount;
    private ArrayList<String> additionalComments;
}
