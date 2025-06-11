package com.example.demo.bookstore.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "books_table")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @Builder.Default
    private Double averageRating = 0.0;
    @Builder.Default
    private Integer ratingCount = 0;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "book_reviews", joinColumns = @JoinColumn(name = "book_id"))
    private List<ReviewData> reviewData = new ArrayList<>();

}
