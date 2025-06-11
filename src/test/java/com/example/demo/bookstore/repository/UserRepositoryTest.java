package com.example.demo.bookstore.repository;

import com.example.demo.BookStoreApplication;
import com.example.demo.bookstore.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(UserRepository.class)
@EntityScan(basePackages = "com.example.demo.bookstore.repository")  // Path to your User entity
// Import your repository explicitly
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager.clear();
        userRepository.deleteAll();
    }

    @Test
    void findAllUsers_shouldReturnAllUsers() {
        User user1 = User.builder()
                .email("alice@example.com")
                .firstName("Alice")
                .lastName("Smith")
                .build();
        User user2 = User.builder()
                .email("bob@example.com")
                .firstName("Bob")
                .lastName("Johnson")
                .build();
        User user3 = User.builder()
                .email("charlie@example.com")
                .firstName("Charlie")
                .lastName("Brown")
                .build();

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);
        entityManager.flush();

        List<User> foundUsers = userRepository.findAllUsers();

        assertThat(foundUsers).hasSize(3);
        assertThat(foundUsers).contains(user1, user2, user3);
    }

    @Test
    void findAllUsers_shouldReturnEmptyListWhenNoUsersExist() {
        List<User> foundUsers = userRepository.findAllUsers();

        assertThat(foundUsers).isEmpty();
    }
}