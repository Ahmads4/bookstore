package com.example.demo.bookstore.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReviewData {

    private UUID userId;
    private Double rating;
    private String additionalComments;
}
