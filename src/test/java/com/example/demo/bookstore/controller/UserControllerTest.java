package com.example.demo.bookstore.controller;

import com.example.demo.bookstore.model.User;
import com.example.demo.bookstore.service.BookStoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private BookStoreService bookStoreService;

    @InjectMocks
    private UserController userController;

    private User user1;
    private User user2;
    private List<User> userList;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setEmail("john@example.com");

        user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setEmail("jane@example.com");

        userList = Arrays.asList(user1, user2);
    }

    @Test
    void testGetUsers_ReturnsListOfUsers() {
        // Given
        when(bookStoreService.getUsers()).thenReturn(userList);

        // When
        List<User> result = userController.getUsers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(user1, result.get(0));
        assertEquals(user2, result.get(1));
        verify(bookStoreService, times(1)).getUsers();
    }

    @Test
    void testGetUsers_ReturnsEmptyListWhenNoUsers() {
        // Given
        when(bookStoreService.getUsers()).thenReturn(Arrays.asList());

        // When
        List<User> result = userController.getUsers();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bookStoreService, times(1)).getUsers();
    }

    @Test
    void testGetUsers_ServiceThrowsException() {
        // Given
        when(bookStoreService.getUsers()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> userController.getUsers());
        verify(bookStoreService, times(1)).getUsers();
    }
}