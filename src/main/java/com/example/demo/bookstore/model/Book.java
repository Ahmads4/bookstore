package com.example.demo.bookstore.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;

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
    @SequenceGenerator(
            name = "book_sequence",
            sequenceName = "book_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "book_sequence"
    )
    private int id;
    private String title;
    private String author;
    private Double price;
    private Date publishDate;
    private String publisher;
    private String description;
    private Double rating;
    private Integer ratingCount;
    private ArrayList<String> additionalComments;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "review_id", referencedColumnName = "id")
    private Review reviews;

}
