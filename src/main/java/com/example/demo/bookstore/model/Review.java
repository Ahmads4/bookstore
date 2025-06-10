package com.example.demo.bookstore.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reviews_table")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString()
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Double rating;
    private String additionalComments;
}
