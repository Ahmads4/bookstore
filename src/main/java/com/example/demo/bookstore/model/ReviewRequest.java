package com.example.demo.bookstore.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequest {
    private double rating;
    private String comment;

}
