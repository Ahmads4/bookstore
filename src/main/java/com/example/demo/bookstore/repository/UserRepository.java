package com.example.demo.bookstore.repository;

import com.example.demo.bookstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
