package com.example.demo.bookstore.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Data
public class ReviewRequest {
    private UUID userId;
    private double rating;
    private String comment;
}
